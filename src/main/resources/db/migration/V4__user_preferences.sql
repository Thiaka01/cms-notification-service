CREATE TABLE notification_preference (
  id              UUID PRIMARY KEY,
  keycloak_sub    VARCHAR(128) NOT NULL,
  channel         VARCHAR(32) NOT NULL,
  event_type      VARCHAR(128),
  enabled         BOOLEAN NOT NULL DEFAULT true,
  UNIQUE (keycloak_sub, channel, event_type)
);
