CREATE TABLE
    orders (
        id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
        reservation_id uuid NOT NULL,
        customer_id uuid NOT NULL,
        total numeric(12, 2) NOT NULL,
        state varchar(20) NOT NULL,
        version bigint NOT NULL DEFAULT 0,
        created_at timestamptz NOT NULL DEFAULT now (),
        updated_at timestamptz NOT NULL DEFAULT now (),
        CONSTRAINT uq_reservation UNIQUE (reservation_id),
        CONSTRAINT chk_order_state CHECK (state IN ('PENDING', 'CONFIRMED', 'CANCELLED'))
    );

CREATE TABLE
    order_items (
        id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
        order_id uuid NOT NULL REFERENCES orders (id),
        event_id uuid NOT NULL,
        seat_id uuid NOT NULL,
        price numeric(12, 2) NOT NULL,
        CONSTRAINT uq_order_item UNIQUE (order_id, seat_id)
    );