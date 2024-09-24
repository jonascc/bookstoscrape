CREATE TABLE categories (id UUID PRIMARY KEY, name VARCHAR(50), url VARCHAR(255));
INSERT INTO categories (id, name, url) VALUES ('0383342d-925c-4b3b-ac26-e2df7a27d44a', 'Travel', 'https://books.toscrape.com/catalogue/category/books/travel_2/index.html');
INSERT INTO categories (id, name, url) VALUES ('6d64c451-d8e3-467f-a4b3-6dbabe151a81', 'Mystery', 'https://books.toscrape.com/catalogue/category/books/mystery_3/index.html');
INSERT INTO categories (id, name, url) VALUES ('551f00b8-3d95-49d2-8a6c-7cdfbd948a27', 'Historical Fiction', 'https://books.toscrape.com/catalogue/category/books/historical-fiction_4/index.html');

CREATE TABLE books (id UUID PRIMARY KEY, name VARCHAR(255), url VARCHAR(255), imgLink VARCHAR(255),
                availability VARCHAR(50), description CLOB(10K), upc VARCHAR(20), productType VARCHAR(5),
                priceExcludingTax NUMERIC(5,2), priceIncludingTax NUMERIC(5,2), tax NUMERIC(5,2), numberOfReviews INT,
                currency CHAR, starRating INT);
INSERT INTO books (id, name, url, imgLink, availability, description, upc, productType, priceExcludingTax,
                 priceIncludingTax, tax, numberOfReviews, currency, starRating) VALUES
                 ('9b18c311-9100-43e2-a2e8-bdffeac2e12b', 'It''s Only the Himalayas', 'https://books.toscrape.com/catalogue/its-only-the-himalayas_981/index.html', 'https://books.toscrape.com/media/cache/27/a5/27a53d0bb95bdd88288eaf66c9230d7e.jpg', 'In stock (19 available)', '“Wherever you go, whatever you do, just . . . don’t do anything stupid...”', 'a22124811bfa8350', 'Books', 45.17, 45.17, 0.00, 0, '£', 2);
INSERT INTO books (id, name, url, imgLink, availability, description, upc, productType, priceExcludingTax,
                 priceIncludingTax, tax, numberOfReviews, currency, starRating) VALUES
                 ('9b18c311-9100-43e2-a2e8-bdffeac2e12c', 'It''s Only the Himalayas 2', 'https://books.toscrape.com/catalogue/its-only-the-himalayas_981/index.html', 'https://books.toscrape.com/media/cache/27/a5/27a53d0bb95bdd88288eaf66c9230d7e.jpg', 'In stock (19 available)', '“Wherever you go, whatever you do, just . . . don’t do anything stupid...”', 'a22124811bfa8351', 'Books', 41.17, 41.17, 0.00, 0, '£', 3);
INSERT INTO books (id, name, url, imgLink, availability, description, upc, productType, priceExcludingTax,
                 priceIncludingTax, tax, numberOfReviews, currency, starRating) VALUES
                 ('9b18c311-9100-43e2-a2e8-bdffeac2e12d', 'It''s Only the Himalayas 3', 'https://books.toscrape.com/catalogue/its-only-the-himalayas_981/index.html', 'https://books.toscrape.com/media/cache/27/a5/27a53d0bb95bdd88288eaf66c9230d7e.jpg', 'In stock (19 available)', '“Wherever you go, whatever you do, just . . . don’t do anything stupid...”', 'a22124811bfa8350', 'Books', 45.17, 47.17, 0.00, 0, '£', 1);
