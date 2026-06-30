# ADR-0007: Optimistic locking without pessimistic locks

Status: Accepted
Date: 2026-06-29

## Context
Concurrent editing of the same workflow or acting on the same task can cause data loss. Pessimistic locks (SELECT FOR UPDATE) block concurrent access and risk deadlocks under load.

## Decision
All mutable entities carry `@Version` for optimistic locking. WorkflowNode and WorkflowTransition versions are checked on designer save. WorkflowRequest and WorkflowTask versions are checked on runtime actions. WorkflowVersion version is checked on publish only (not on save). No SELECT FOR UPDATE is used. If contention proves to be a problem, it will be added selectively.

## Consequences
+ No deadlock risk from exclusive locks
+ Stale updates fail with OptimisticLockException (409 to the client)
+ Designer versions are fine-grained (per-node) to allow concurrent editing of different nodes
+ Publish version is coarse (per-workflow) to prevent publishing a stale snapshot
