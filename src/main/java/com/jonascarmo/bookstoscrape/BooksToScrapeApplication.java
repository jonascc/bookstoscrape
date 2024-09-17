package com.jonascarmo.bookstoscrape;

import com.jonascarmo.bookstoscrape.dao.BooksToScrapeDao;
import com.jonascarmo.bookstoscrape.model.Book;
import com.jonascarmo.bookstoscrape.model.Category;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class BooksToScrapeApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BooksToScrapeApplication.class);

    private final BooksToScrapeDao booksToScrapeDao;

    @Value("${bookstoscrape.url}")
    private String booksToScrapeUrl;

    public BooksToScrapeApplication(BooksToScrapeDao booksToScrapeDao) {
        this.booksToScrapeDao = booksToScrapeDao;
    }

    public static void main(String[] args) {
        SpringApplication.run(BooksToScrapeApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Parsing {}", booksToScrapeUrl);
        Document doc = parseUrl(booksToScrapeUrl);

        createTables();

        log.info("Scrapping categories.");
        List<Category> scrapedCategories = scrapeCategories(doc);
        Object[] array = scrapedCategories.stream().map(Category::toString).toArray();
        log.info(Arrays.toString(array));
        log.info("Inserting categories into the database.");
        booksToScrapeDao.insertCategories(scrapedCategories);

        log.info("Scraping books catalogs.");
        List<Book> books = scrapeBooksCatalogsFromAllCategories(scrapedCategories);
        books = books.stream().map(this::scrapeBookInformation).toList();
        log.info("Inserting books into the database.");
        booksToScrapeDao.insertBooks(books);
        log.info("Books inserted into the database.");
    }

    private Book scrapeBookInformation(Book book) {
        log.info("Getting information about {} - {}", book.getCategory().name(), book.getName());
        Document document = parseUrl(book.getUrl());
        Element productDescriptionElement = document.getElementById("product_description");
        String productDescription = productDescriptionElement != null ? productDescriptionElement.nextElementSibling().text() : null;
        String starRatingClass = document.select("p.star-rating").getFirst().attr("class");
        Elements tbody = document.select("table.table > tbody");
        Elements tds = tbody.getFirst().getElementsByTag("td");

        book.setDescription(productDescription);
        book.setUpc(tds.getFirst().text());
        book.setProductType(tds.get(1).text());
        book.setPriceExcludingTax(new BigDecimal(sanitizePrice(tds.get(2).text())));
        book.setPriceIncludingTax(new BigDecimal(sanitizePrice(tds.get(3).text())));
        book.setTax(new BigDecimal(sanitizePrice(tds.get(4).text())));
        book.setAvailability(tds.get(5).text());
        book.setNumberOfReviews(Integer.valueOf(tds.get(6).text()));
        book.setCurrency("£");
        book.setStarRating(StarRating.getFromClassAttribute(starRatingClass));
        return book;
    }

    private String sanitizePrice(String price) {
        // Regular expression to extract the numeric value of a price
        String regex = "\\d+(\\.\\d{1,2})?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(price);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private void createTables() {
        log.info("Creating \"categories\" table.");
        booksToScrapeDao.createCategoriesTable();
        log.info("Creating \"books\" table.");
        booksToScrapeDao.createBooksTable();
    }

    private List<Book> scrapeBooksCatalogsFromAllCategories(List<Category> scrapedCategories) {
        List<Book> books = new ArrayList<>();
        scrapedCategories.forEach(category -> {
            log.info("Category: {}", category.name());
            List<Book> scrapedBooks = scrapeBookCatalogFromCategory(category);
            Object[] booksArray = scrapedBooks.stream().map(Book::getName).toArray();
            log.info(Arrays.toString(booksArray));
            books.addAll(scrapedBooks);
        });
        return books;
    }

    private Document parseUrl(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36")
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return doc;
    }

    private List<Category> scrapeCategories(Document doc) {
        Elements categoryMenu;
        List<Category> categories = null;
        try {
            categoryMenu = doc.select("ul.nav-list > li > ul > li");
            categories = categoryMenu.stream().map(element -> {
                Element link = element.select("a").getFirst();
                return new Category(UUID.randomUUID(), link.text(), link.attr("abs:href"));
            }).toList();
        } catch (Exception e) {
            System.out.println(e);
        }
        return categories;
    }

    private List<Book> scrapeBookCatalogFromCategory(Category category) {
        return scrapeBookCatalogFromCategory(category, null);
    }

    private List<Book> scrapeBookCatalogFromCategory(Category category, String nextPageUrl) {
        Document document;
        if (nextPageUrl == null) {
            document = parseUrl(category.url());
        } else {
            document = parseUrl(nextPageUrl);
        }

        Elements booksElements = document.select("div > ol.row > li");
        List<Book> books = booksElements.stream().map(element -> {
            Elements article = element.select("article");
            Element bookLink = article.select("div > a").getFirst();
            String url = bookLink.attr("abs:href");
            String imgLink = bookLink.select("img").attr("abs:src");
            String bookName = article.select("h3 > a").getFirst().attr("title");

            return Book.builder()
                    .id(UUID.randomUUID())
                    .name(bookName)
                    .url(url)
                    .imgLink(imgLink)
                    .category(category)
                    .build();
        }).toList();

        List<Book> list = new ArrayList<>(books);

        Elements nextPageElements = document.select("div > ul > li.next > a");
        String nextPage = null;
        if (!nextPageElements.isEmpty()) {
            nextPage = nextPageElements.getFirst().attr("abs:href");
        }

        if (nextPage != null) {
            list.addAll(scrapeBookCatalogFromCategory(category, nextPage));
        }
        return list;
    }

}
