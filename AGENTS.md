# Loan Platform Agent Guide

## Read This First

This repository contains implementation code only. The planning and decision
source of truth is the companion second-brain vault, not this repository.

## Two-Workspace Contract

Treat the two workspaces as one project with non-overlapping responsibilities:

| Concern | Canonical workspace | Do not duplicate it elsewhere |
|---|---|---|
| Plan, requirements, ADRs, architecture, API/database design, roadmap, implementation notes, learning decisions | The vault project folder | Do not create a competing plan or ADR in this source repository |
| Installation, Java source, Maven configuration, Docker, Flyway migrations, tests, scripts, and executable runtime settings | This `loan-platform` repository | Do not put implementation source or runtime files in the vault |

In short: **read plan/documents from the vault; implement and verify source in
`loan-platform`.** After verified implementation work, update the vault's
evidence and roadmap rather than maintaining a second status record here.

Current Linux paths:

```text
Source repository: /home/dattp/loanApp/loan-platform
Project vault:     /home/dattp/second-vault/second-brain/02-Projects/Banking Loan Origination Platform
```

When the same sibling layout is used, the project vault is also available at:

```text
../../second-vault/second-brain/02-Projects/Banking Loan Origination Platform
```

If the vault is unavailable or its location is unknown, do not invent a new
architecture or alter an existing decision silently. Ask the user for the
vault location first.

Before making a project decision or materially changing code, read the
relevant vault documents in this order:

1. `04-Project Roadmap and Progress.md` — current progress, evidence, and
   next action.
2. `01-Requirements.md` — business rules and state transitions.
3. `02-Architecture.md` — service boundaries and communication.
4. `04-ADR/` — accepted decisions; especially ADR-001 through ADR-008.
5. `07-API Design/` and `08-Database Design/` — contracts and ownership.
6. `05-Implementation Notes/02-Current Implementation Status.md` — the
   exact paused implementation state and safe resume checklist.

Update the vault roadmap and implementation notes after meaningful work. Do
not treat generated source files as completed delivery without compile/test or
runtime evidence.

## Project Intent

This is a learning-focused, interview-ready Banking Loan Origination Platform.
The user prefers Vietnamese, practical explanations, and visible reasoning for
domain boundaries, transactions, concurrency, event reliability, and system
design. Generate routine scaffolding readily, but make learning-critical
decisions explicit before hiding them in code.

## Technology And Repository Rules

- Java **21** and Spring Boot **4.1.2** are currently pinned deliberately.
  Do not downgrade to the host Java version; use Docker's Java 21 build when
  necessary.
- Maven reactor modules: `platform-bom`, `platform-parent`, and four
  independently deployable services in `services/`.
- `platform-bom` and `platform-parent` may share dependency/plugin
  conventions only. Never put domain entities, repositories, business rules,
  or service clients there.
- Use Maven/Spring conventions; prefer simple maintainable code over a broad
  abstraction layer.
- Do not read another service's schema/table from business code. One local
  PostgreSQL instance is used, but each service owns its schema and Flyway
  migrations.
- Use `apply_patch` for edits. Preserve existing/unrelated work. Do not commit
  or push unless the user explicitly asks.

## Accepted Phase-1 Architecture

- Four services run independently: `loan-application-service`,
  `workflow-service`, `scoring-service`, `notification-service`.
- No API gateway in phase 1.
- Real identity provider authentication is deferred until immediately after
  the first verified outbox/Kafka slice. `X-Actor-Id` is only a local
  development identity bridge, never production security.
- Loan application owns customer-facing application state; workflow owns its
  case/audit; scoring owns scoring; notification owns notifications.
- Business events use transactional outbox, Kafka delivery, and idempotent
  consumers. Assume at-least-once delivery.

## Current Paused Slice — Read Before Continuing

Source for the following slice has been added, but it is **not yet accepted**:

```text
Create draft -> Update draft -> Submit
  -> write ApplicationSubmitted in same DB transaction
  -> scheduled outbox publisher
  -> Kafka topic loan.application.events.v1
  -> workflow case + notification in separate consumer groups
```

The running Docker containers were found to use a stale JAR that did not
contain the new controller/repositories; its endpoint returned `404`. Do not
claim this slice works and do not raise roadmap percentage until the resume
checklist in the vault status note is completed.

Preserve these existing design choices while verifying/fixing it:

- PostgreSQL sequence generates business application number; gaps are valid.
- JPA `@Version` protects updates/submission from stale concurrent requests.
- Outbox selection uses `FOR UPDATE SKIP LOCKED` so multiple publisher
  instances do not claim the same pending row.
- Kafka acknowledgement followed by DB failure can cause re-publication;
  each consumer's `processed_event` table is the deduplication boundary.
- The outbox `event_id` must equal the `eventId` in its JSON payload.

## Required Verification Order For This Slice

1. Build the three modified service images completely; confirm the loan image
   includes `LoanApplicationController`.
2. Recreate loan, workflow, and notification services; check Flyway `V2` and
   JPA repository discovery in logs.
3. Exercise product lookup, draft creation, update, submit, and owner checks.
4. Verify one submitted application produces one published outbox event, one
   workflow case, and one notification.
5. Demonstrate duplicate submit and duplicate delivery behavior, then add
   focused automated tests.
6. Update the vault evidence/progress. Only then return to the authentication
   decision.

## Safety And Review Checklist

For commands that mutate state, state the intended scope first. Review in this
order: business correctness; state/data integrity; transaction/concurrency;
authorization and sensitive data; event reliability/idempotency; JPA/query
behavior; tests; maintainability.

For every meaningful slice, explicitly consider duplicate requests, stale
versions, invalid transitions, broker/database failures, duplicate/out-of-
order events, and sensitive data in logs.
