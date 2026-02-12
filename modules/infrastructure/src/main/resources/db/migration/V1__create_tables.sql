CREATE TABLE crypto_rate
(
    id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ts       bigint         NOT NULL,
    currency VARCHAR(16)    NOT NULL,
    rate     NUMERIC(18, 8) NOT NULL,
    CONSTRAINT uq_crypto UNIQUE (currency, ts)
);

CREATE INDEX idx_crypto_price_symbol_ts ON crypto_rate (currency, ts);
