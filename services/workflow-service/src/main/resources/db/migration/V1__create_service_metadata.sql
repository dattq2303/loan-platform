CREATE TABLE service_metadata (
    service_name VARCHAR(100) PRIMARY KEY,
    initialized_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO service_metadata (service_name) VALUES ('workflow-service');

