CREATE TABLE delivery_attempt (
  id              UUID PRIMARY KEY,
  notification_id UUID NOT NULL REFERENCES notification (id),
  channel         VARCHAR(32) NOT NULL,
  status          VARCHAR(32) NOT NULL,
  provider_ref    VARCHAR(255),
  error_message   TEXT,
  attempted_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT chk_delivery_status CHECK (status IN ('PENDING', 'SENT', 'FAILED'))
);
