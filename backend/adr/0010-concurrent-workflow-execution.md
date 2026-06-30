# ADR-0010: Concurrent workflow execution with optimistic locking

Status: Accepted
Date: 2026-06-29

## Context
Multiple users may act on the same workflow request simultaneously (e.g., two approvers approving the same task, or a designer saving while a scheduler escalates). Without coordination, data loss and inconsistent state can occur.

## Decision
All mutable entities carry `@Version` for optimistic locking:
- WorkflowNode: version checked on designer save per node
- WorkflowTransition: version checked on designer save per transition
- WorkflowRequest: version checked on runtime actions
- WorkflowTask: version checked on task actions
- WorkflowVersion: version checked on publish only (coarse-grained)

Retry logic: the caller (NormalExecutionService, WorkflowDesignerService) catches `OptimisticLockException` and retries up to 3 times with exponential backoff before returning 409 Conflict.

## Consequences
+ No deadlock risk from pessimistic locks
+ Stale updates fail fast with 409 to the client
+ Designer versions are fine-grained (per-node) to allow concurrent editing of different nodes
+ Publish version is coarse to prevent publishing a stale snapshot
+ Retries are caller's responsibility — the engine stays persistence-free
