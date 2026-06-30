# ADR-0005: WorkflowEngine is persistence-free

Status: Accepted
Date: 2026-06-29

## Context
Mixing persistence logic with business logic makes the engine hard to test, hard to reason about, and impossible to run without a database. Every database call adds latency and transactional complexity.

## Decision
WorkflowEngine takes a CompiledWorkflow (in-memory), a NodeRef, a TransitionAction, and a BusinessContext. It returns an immutable ExecutionResult containing what should happen next (TaskPlan, AuditPlan, status change). The engine never calls repositories, publishes events, writes to the database, opens transactions, or knows about JPA, Spring Security, or HTTP.

## Consequences
+ Engine can be unit-tested without a database
+ ExecutionResult is the single output contract
+ Callers (NormalExecutionService, WorkflowSimulator) decide how to persist the result
+ Simple transaction boundaries: one transaction per engine call + result application
