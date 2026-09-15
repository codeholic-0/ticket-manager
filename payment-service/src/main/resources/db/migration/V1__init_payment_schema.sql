CREATE TABLE
    payment_transactions (
        id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
        order_id uuid NOT NULL,
        amount numeric(12, 2) NOT NULL,
        status varchar(20) NOT NULL,
        external_ref varchar(100),
        version bigint NOT NULL DEFAULT 0,
        created_at timestamptz NOT NULL DEFAULT now (),
        CONSTRAINT chk_payment_status CHECK (status IN ('PENDING', 'SUCCEEDED', 'FAILED'))
    );

CREATE INDEX idx_payment_order ON payment_transactions (order_id);