# ADR-0001: Use DAG instead of linear steps

Status: Accepted
Date: 2026-06-29

## Context
The existing approval system uses linear step ordering. Adding branching, parallel approvals, send-back, or loops requires fragile workarounds.

## Decision
Workflows model as a directed acyclic graph (DAG) of nodes connected by transitions. Nodes represent states (START, APPROVAL, TASK, GATEWAY, END). Transitions reference source/target nodes and carry an action and optional condition.

## Consequences
+ Supports branching, parallel approvals, send-back to previous nodes, and gateway-based routing
+ Validation required for graph correctness (no cycles, reachability, determinism)
+ Transition conditions must be deterministic for a given input
