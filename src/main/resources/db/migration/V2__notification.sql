CREATE TABLE notification (
  id            UUID PRIMARY KEY,
  event_type    VARCHAR(128) NOT NULL,
  event_id      VARCHAR(128) NOT NULL,
  subject       VARCHAR(255),
  body          TEXT NOT NULL,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE notification_recipient (
  id              UUID PRIMARY KEY,
  notification_id UUID NOT NULL REFERENCES notification (id) ON DELETE CASCADE,
  keycloak_sub    VARCHAR(128) NOT NULL,
  read_at         TIMESTAMPTZ,
  UNIQUE (notification_id, keycloak_sub)
);

CREATE INDEX idx_notif_recipient ON notification_recipient (keycloak_sub, read_at);
