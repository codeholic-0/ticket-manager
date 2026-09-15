CREATE TABLE
    events (
        id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
        name varchar(200) NOT NULL,
        venue varchar(150),
        event_date timestamptz NOT NULL,
        created_at timestamptz NOT NULL DEFAULT now ()
    );

CREATE TABLE
    seats (
        id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
        event_id uuid NOT NULL REFERENCES events (id),
        seat_row varchar(5) NOT NULL,
        seat_number integer NOT NULL,
        status varchar(20) NOT NULL DEFAULT 'AVAILABLE',
        version bigint NOT NULL DEFAULT 0,
        hold_token uuid,
        held_until timestamptz,
        CONSTRAINT uq_seat UNIQUE (event_id, seat_row, seat_number),
        CONSTRAINT chk_seat_status CHECK (status IN ('AVAILABLE', 'HELD', 'RESERVED'))
    );

CREATE INDEX idx_seats_event_status ON seats (event_id, status);