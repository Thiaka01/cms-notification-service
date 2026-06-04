CREATE TABLE notification_event (
  id                 UUID PRIMARY KEY,
  event_code         VARCHAR(128) NOT NULL UNIQUE,
  name               VARCHAR(255) NOT NULL,
  description        VARCHAR(1000),
  active             BOOLEAN NOT NULL DEFAULT true,
  created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE notification_event_channel (
  event_id           UUID NOT NULL REFERENCES notification_event (id) ON DELETE CASCADE,
  channel            VARCHAR(32) NOT NULL,
  PRIMARY KEY (event_id, channel)
);

INSERT INTO notification_event (id, event_code, name, description, active)
SELECT
  (
    substr(md5(event_type), 1, 8) || '-' ||
    substr(md5(event_type), 9, 4) || '-' ||
    substr(md5(event_type), 13, 4) || '-' ||
    substr(md5(event_type), 17, 4) || '-' ||
    substr(md5(event_type), 21, 12)
  )::uuid,
  event_type,
  initcap(replace(regexp_replace(regexp_replace(event_type, '^cms\.', ''), '\.v[0-9]+$', ''), '.', ' ')),
  'Migrated notification event',
  true
FROM (SELECT DISTINCT event_type FROM notification_template) existing_events
ON CONFLICT (event_code) DO NOTHING;

INSERT INTO notification_event_channel (event_id, channel)
SELECT DISTINCT e.id, t.channel
FROM notification_event e
JOIN notification_template t ON t.event_type = e.event_code
ON CONFLICT DO NOTHING;

ALTER TABLE notification_template
  ADD COLUMN event_id UUID,
  ADD COLUMN template_content TEXT,
  ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT now();

UPDATE notification_template t
SET event_id = e.id,
    template_content = t.body_template
FROM notification_event e
WHERE e.event_code = t.event_type;

ALTER TABLE notification_template
  ALTER COLUMN event_id SET NOT NULL,
  ALTER COLUMN template_content SET NOT NULL,
  ALTER COLUMN body_template DROP NOT NULL,
  ADD CONSTRAINT fk_notification_template_event
    FOREIGN KEY (event_id) REFERENCES notification_event (id);

CREATE UNIQUE INDEX uq_notification_template_event_channel
  ON notification_template (event_id, channel);

CREATE TABLE notification_log (
  id             UUID PRIMARY KEY,
  event_code     VARCHAR(128) NOT NULL,
  recipient      VARCHAR(512) NOT NULL,
  channel        VARCHAR(32) NOT NULL,
  status         VARCHAR(32) NOT NULL,
  error_message  TEXT,
  sent_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT chk_notification_log_status CHECK (status IN ('PENDING', 'SENT', 'FAILED'))
);

CREATE INDEX idx_notification_log_event_channel ON notification_log (event_code, channel);
CREATE INDEX idx_notification_log_recipient ON notification_log (recipient);
