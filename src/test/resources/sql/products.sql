TRUNCATE TABLE products RESTART IDENTITY;

INSERT INTO products (code, name, price_eur, price_usd, is_available)
VALUES
    ('ABC123xyz9', 'Product 1', 12.34, 14.38, true),
    ('XYZ987abc1', 'Product 2', 20.00, 23.30, false);