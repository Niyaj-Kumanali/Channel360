# ADR-0009: BusinessField typed descriptors instead of magic strings

Status: Accepted
Date: 2026-06-29

## Context
Using `Map<String, Object>` with string keys for business context data leads to runtime errors from typos and type mismatches. The compiler cannot catch "Amout" vs "Amount".

## Decision
BusinessContext uses `BusinessField<T>` typed descriptors. Each field has a name and a Class<T>. The context exposes `T get(BusinessField<T>)`. Fields are grouped by domain (WorkflowBusinessFields, SalesBusinessFields, FinanceBusinessFields, HRBusinessFields).

## Consequences
+ Compiler catches type mismatches
+ IDE autocompletion for field names
+ No magic strings in condition evaluation code
+ Grouping by domain prevents a god constants file
