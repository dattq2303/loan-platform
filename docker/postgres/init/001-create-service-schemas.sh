#!/usr/bin/env sh
set -eu

psql --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" \
  --set=loan_application_password="$LOAN_APPLICATION_DB_PASSWORD" \
  --set=workflow_password="$WORKFLOW_DB_PASSWORD" \
  --set=scoring_password="$SCORING_DB_PASSWORD" \
  --set=notification_password="$NOTIFICATION_DB_PASSWORD" <<'EOSQL'
REVOKE CREATE ON SCHEMA public FROM PUBLIC;

CREATE ROLE loan_application_user LOGIN PASSWORD :'loan_application_password';
CREATE SCHEMA loan_application AUTHORIZATION loan_application_user;
GRANT CONNECT ON DATABASE loan_platform TO loan_application_user;

CREATE ROLE workflow_user LOGIN PASSWORD :'workflow_password';
CREATE SCHEMA workflow AUTHORIZATION workflow_user;
GRANT CONNECT ON DATABASE loan_platform TO workflow_user;

CREATE ROLE scoring_user LOGIN PASSWORD :'scoring_password';
CREATE SCHEMA scoring AUTHORIZATION scoring_user;
GRANT CONNECT ON DATABASE loan_platform TO scoring_user;

CREATE ROLE notification_user LOGIN PASSWORD :'notification_password';
CREATE SCHEMA notification AUTHORIZATION notification_user;
GRANT CONNECT ON DATABASE loan_platform TO notification_user;
EOSQL

