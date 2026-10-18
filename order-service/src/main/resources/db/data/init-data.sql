CREATE TABLE orders (
                        id SERIAL PRIMARY KEY,
                        product_id VARCHAR(255) NOT NULL,
                        quantity INT NOT NULL,
                        price NUMERIC(10,2) NOT NULL,
                        status VARCHAR(50) NOT NULL
);
