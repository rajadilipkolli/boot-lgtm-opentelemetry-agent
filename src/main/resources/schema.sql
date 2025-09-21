CREATE TABLE IF NOT EXISTS coupon
(
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title       VARCHAR(255)     NOT NULL,
    description TEXT,
    start_date  TIMESTAMP        NOT NULL,
    end_date    TIMESTAMP        NOT NULL,
    operator    VARCHAR(20),
    value       NUMERIC(19,4)    NOT NULL,
    CONSTRAINT coupon_dates_valid CHECK (start_date <= end_date)
);

CREATE INDEX IF NOT EXISTS idx_coupon_dates ON coupon (start_date, end_date);
