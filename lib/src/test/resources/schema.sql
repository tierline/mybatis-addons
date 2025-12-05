-- Test table for integration testing
CREATE TABLE test_entity (
    id INTEGER PRIMARY KEY,
    name VARCHAR(100),
    age INTEGER,
    salary BIGINT,
    rate DOUBLE PRECISION,
    amount NUMERIC(10, 2),
    active BOOLEAN,
    birth_date DATE,
    created_at TIMESTAMP WITH TIME ZONE
);
