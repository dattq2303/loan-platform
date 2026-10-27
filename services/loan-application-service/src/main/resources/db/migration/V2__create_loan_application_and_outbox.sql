CREATE SEQUENCE application_no_seq START WITH 10000001 INCREMENT BY 1;

CREATE TABLE loan_product (
    product_id UUID PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    min_amount NUMERIC(19, 2) NOT NULL,
    max_amount NUMERIC(19, 2) NOT NULL,
    min_tenor_months INTEGER NOT NULL,
    max_tenor_months INTEGER NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE loan_application (
    application_id UUID PRIMARY KEY,
    application_no VARCHAR(30) NOT NULL UNIQUE,
    owner_customer_id VARCHAR(100) NOT NULL,
    product_id UUID NOT NULL REFERENCES loan_product(product_id),
    requested_amount NUMERIC(19, 2) NOT NULL,
    tenor_months INTEGER NOT NULL,
    application_status VARCHAR(40) NOT NULL,
    submission_channel VARCHAR(40),
    submitted_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE applicant_profile (
    applicant_profile_id UUID PRIMARY KEY,
    loan_application_id UUID NOT NULL UNIQUE REFERENCES loan_application(application_id),
    customer_id VARCHAR(100) NOT NULL,
    full_name VARCHAR(200) NOT NULL,
    date_of_birth DATE NOT NULL,
    monthly_income NUMERIC(19, 2) NOT NULL,
    employment_type VARCHAR(40) NOT NULL
);

CREATE TABLE outbox_event (
    event_id UUID PRIMARY KEY,
    aggregate_id UUID NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    topic VARCHAR(200) NOT NULL,
    event_key VARCHAR(100) NOT NULL,
    payload JSONB NOT NULL,
    status VARCHAR(20) NOT NULL,
    attempt_count INTEGER NOT NULL DEFAULT 0,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    published_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_loan_application_status_created ON loan_application(application_status, created_at);
CREATE INDEX idx_loan_application_owner_created ON loan_application(owner_customer_id, created_at);
CREATE INDEX idx_outbox_event_status_occurred ON outbox_event(status, occurred_at);

INSERT INTO loan_product (
    product_id, product_code, name, min_amount, max_amount,
    min_tenor_months, max_tenor_months, active
) VALUES (
    '00000000-0000-0000-0000-000000000001', 'PERSONAL_STD', 'Personal Standard Loan',
    10000000, 500000000, 6, 60, true
);
