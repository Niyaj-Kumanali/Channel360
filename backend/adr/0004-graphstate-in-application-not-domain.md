# ADR-0004: GraphState lives in application layer, not domain

Status: Accepted
Date: 2026-06-29

## Context
GraphState (NEW, MODIFIED, DELETED, UNCHANGED) represents UI editing state, not domain behavior. Placing it in the domain model would couple domain entities to presentation concerns.

## Decision
GraphState is defined in `application.designer.model` and used only in the designer save pipeline. The domain `GraphNode`, `GraphTransition`, etc. carry no state indicators. The save command in the API layer carries per-element state, and the consistency validator compares it against the database before applying changes.

## Consequences
+ Domain model remains pure
+ Changing the UI state model requires no domain changes
+ Server is authoritative for state transitions, using DB version checks
