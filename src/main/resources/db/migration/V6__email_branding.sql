CREATE TABLE email_branding (
  id              UUID PRIMARY KEY,
  logo_url        VARCHAR(512),
  header_title    VARCHAR(255) NOT NULL,
  header_subtitle VARCHAR(512),
  header_html     TEXT,
  footer_html     TEXT NOT NULL,
  active          BOOLEAN NOT NULL DEFAULT true,
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_by      VARCHAR(128)
);

CREATE UNIQUE INDEX uq_email_branding_active ON email_branding (active) WHERE active = true;
