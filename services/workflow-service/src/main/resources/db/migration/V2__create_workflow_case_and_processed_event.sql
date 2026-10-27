CREATE TABLE processed_event (
    event_id UUID PRIMARY KEY,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE workflow_case (
    workflow_case_id UUID PRIMARY KEY,
    application_id UUID NOT NULL UNIQUE,
    application_no VARCHAR(30) NOT NULL,
    customer_id VARCHAR(100) NOT NULL,
    case_status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
