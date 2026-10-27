# Loan Origination Platform

Learning-focused banking loan-origination platform built as four independent
Spring Boot services.

## Local quick start

Prerequisites: Docker Engine/Desktop with Docker Compose v2.

```bash
cp .env.example .env
docker compose up --build
```

The local stack exposes:

| Component | Address |
|---|---|
| Loan application service | `http://localhost:8081/actuator/health` |
| Workflow service | `http://localhost:8082/actuator/health` |
| Scoring service | `http://localhost:8083/actuator/health` |
| Notification service | `http://localhost:8084/actuator/health` |
| PostgreSQL | `localhost:5432` |
| Kafka | `localhost:9092` |
| Redis | `localhost:6379` |

To run only infrastructure while debugging a service from an IDE:

```bash
docker compose up postgres kafka redis
```

## Repository structure

```text
platform-bom/       Dependency versions shared by the reactor
platform-parent/    Java and Maven plugin conventions only
services/           Independently deployable service applications
docker/postgres/    Database role and schema initialization
```

The platform modules intentionally contain no business entity, repository,
service, or cross-service client. Each service owns its data and migrations.

