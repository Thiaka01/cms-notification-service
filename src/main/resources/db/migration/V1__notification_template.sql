CREATE TABLE notification_template (
  id            UUID PRIMARY KEY,
  event_type    VARCHAR(128) NOT NULL,
  channel       VARCHAR(32) NOT NULL,
  subject       VARCHAR(255),
  body_template TEXT NOT NULL,
  active        BOOLEAN NOT NULL DEFAULT true,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (event_type, channel)
);
