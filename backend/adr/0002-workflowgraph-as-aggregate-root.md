# ADR-0002: WorkflowGraph as aggregate root

Status: Accepted
Date: 2026-06-29

## Context
The designer UI needs to load/save the entire workflow in one operation. Managing nodes, transitions, assignments, and conditions as separate REST resources creates O(n) API calls and complex cross-resource validation.

## Decision
WorkflowGraph is the aggregate root. It is a nested structure containing nodes, transitions, and assignments. The frontend sends one JSON payload per save. The server stores both the normalized relational form (for querying) and the raw JSON (for round-trip fidelity).

## Consequences
+ Single save endpoint for the entire workflow
+ Simplified frontend state management
+ Dual write required (normalized + JSON) on every save
+ JSON serves as the source of truth for the designer; normalized tables support the validator and compiler
