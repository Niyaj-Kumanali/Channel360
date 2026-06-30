# ADR-0006: Transactional Outbox instead of direct event publishing

Status: Accepted
Date: 2026-06-29

## Context
Publishing domain events directly from transactional code risks event loss if the broker is unavailable. Events published before the transaction commits may be acted on before the transaction is visible.

## Decision
Events are written to the `workflow_outbox` table in the same database transaction as the business operation. A background poller reads PENDING events using `FOR UPDATE SKIP LOCKED` and publishes them asynchronously. Failed events are retried with exponential backoff (up to 5 retries).

## Consequences
+ Zero event loss — the outbox record is part of the business transaction
+ Events survive broker outages
+ Safe for horizontal scaling (SKIP LOCKED prevents double-publish)
+ Slight delay between event creation and publication (5-second poll interval)
