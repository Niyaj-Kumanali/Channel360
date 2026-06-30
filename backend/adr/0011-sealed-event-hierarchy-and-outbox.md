# ADR-0011: Sealed event hierarchy and transactional outbox

Status: Accepted
Date: 2026-06-29

## Context
The workflow engine produces domain events at various lifecycle points (task created, approved, rejected, escalated, timed out, workflow started, completed, etc.). These events must be:
1. Reliably delivered — not lost on broker outage
2. Discoverable — each event is a specific type, not a generic envelope
3. Future-proof — adding new event types should be a compile-time check

## Decision
### Sealed hierarchy
`WorkflowEvent` is a sealed interface with exactly 13 permitted implementations. The compiler enforces exhaustive matching in `switch` expressions. Each event record carries `aggregateId`, `eventType()`, and `occurredAt()`.

### Transactional outbox
Events are not published directly. Instead:
1. The business operation writes the event payload to `workflow_outbox` in the same DB transaction
2. A background poller (`OutboxPoller`) reads PENDING events every 5 seconds using `FOR UPDATE SKIP LOCKED`
3. Published events are marked PUBLISHED; failed events are retried (up to 5 attempts)
4. `OutboxPublisher` allows publishing synchronously after commit for latency-sensitive consumers

### Event listeners
- `WorkflowEventListener` handles V2 events transactionally (AFTER_COMMIT phase)
- `DomainEventListener` remains for V1 events
- Each listener method is idempotent

## Consequences
+ Zero event loss — outbox write is part of the business transaction
+ Events survive broker outages with automatic retry
+ `SKIP LOCKED` prevents double-publish in horizontal scaling
+ Sealed hierarchy makes adding events a deliberate act (modify the sealed interface)
+ Slight delay between event creation and publication (5-second poll interval)
+ Failed events with exhausted retries remain in FAILED state for manual inspection
