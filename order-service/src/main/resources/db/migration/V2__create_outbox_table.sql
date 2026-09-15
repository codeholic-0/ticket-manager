CREATE TABLE
    outbox (
        id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
        aggregate_id uuid NOT NULL,
        event_type varchar(100) NOT NULL,
        payload jsonb NOT NULL,
        created_at timestamptz NOT NULL DEFAULT now (),
        published_at timestamptz
    );

CREATE INDEX idx_outbox_unpublished ON outbox (published_at)
WHERE
    published_at IS NULL;