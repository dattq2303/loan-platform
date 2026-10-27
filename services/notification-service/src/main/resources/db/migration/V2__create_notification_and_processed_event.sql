CREATE TABLE processed_event (
    event_id UUID PRIMARY KEY,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notification (
    notification_id UUID PRIMARY KEY,
    application_id UUID NOT NULL,
    customer_id VARCHAR(100) NOT NULL,
    notification_type VARCHAR(100) NOT NULL,
    message VARCHAR(500) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notification_customer_created ON notification(customer_id, created_at DESC);
