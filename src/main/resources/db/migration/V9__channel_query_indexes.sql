-- Additional indexes for channel-specific notification queries

CREATE INDEX idx_notification_log_channel_status ON notification_log (channel, status);
CREATE INDEX idx_notification_log_channel_event_status ON notification_log (channel, event_code, status);
CREATE INDEX idx_notification_log_status ON notification_log (status);

CREATE INDEX idx_delivery_attempt_channel_status ON delivery_attempt (channel, status);
CREATE INDEX idx_delivery_attempt_channel ON delivery_attempt (channel);
