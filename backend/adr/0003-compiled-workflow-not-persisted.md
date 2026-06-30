# ADR-0003: CompiledWorkflow is not persisted

Status: Accepted
Date: 2026-06-29

## Context
Persisting the compiled workflow (predicates) as JSON creates a dual-source-of-truth problem. When the compiler evolves (new operator, optimization), already-published workflows contain stale compiled code. This requires compiled-schema versioning, migration, or dual-version support.

## Decision
CompiledWorkflow is computed from the WorkflowGraph at publish time and cached in memory. The cache is warmed on startup for all PUBLISHED versions. No compiled_definition column exists in the database. Compilation is O(n) where n is the number of conditions.

## Consequences
+ No dual-source-of-truth for compiled artifacts
+ Compiler changes automatically apply to all workflows on next publish + cache refresh
+ No compiled-schema versioning needed
+ Cache miss on first access after restart (warmed on startup)
