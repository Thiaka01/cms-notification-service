CREATE TABLE inbox_processed_event (
  event_id        VARCHAR(128) NOT NULL,
  consumer_group  VARCHAR(128) NOT NULL,
  processed_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (event_id, consumer_group)
);
