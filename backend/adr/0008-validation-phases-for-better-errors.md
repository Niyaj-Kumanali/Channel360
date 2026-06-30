# ADR-0008: Validation phases for better error messages

Status: Accepted
Date: 2026-06-29

## Context
Running all 23 validation rules simultaneously produces cascading errors. A missing START node triggers dozens of downstream errors about orphaned nodes, unreachable nodes, and missing transitions.

## Decision
Validation rules are grouped into phases and executed sequentially: STRUCTURE -> REFERENCES -> GRAPH -> TRANSITIONS -> ASSIGNMENTS -> CONDITIONS -> PUBLISH. If any phase produces errors, remaining phases are skipped. Rules within a phase are all evaluated (to report all errors within that concern).

## Consequences
+ Earliest, most critical errors are shown first
+ No cascading error noise
+ Each rule declares its phase, making grouping explicit and auto-discoverable
+ Adding a new rule means picking the right phase
