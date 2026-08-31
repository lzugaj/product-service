CREATE SEQUENCE products_id_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE products (
    id BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('products_id_seq'),
    code VARCHAR(10) NOT NULL,
    name VARCHAR(255) NOT NULL,
    price_eur NUMERIC(10, 2) NOT NULL,
    price_usd NUMERIC(10, 2) NOT NULL,
    is_available BOOLEAN NOT NULL
);

ALTER SEQUENCE products_id_seq
    OWNED BY products.id;

ALTER TABLE products
    ADD CONSTRAINT products_code_key UNIQUE (code);

ALTER TABLE products
    ADD CONSTRAINT products_code_length
        CHECK (char_length(code) = 10);

ALTER TABLE products
    ADD CONSTRAINT products_price_eur_non_negative
        CHECK (price_eur >= 0);

ALTER TABLE products
    ADD CONSTRAINT products_price_usd_non_negative
        CHECK (price_usd >= 0);