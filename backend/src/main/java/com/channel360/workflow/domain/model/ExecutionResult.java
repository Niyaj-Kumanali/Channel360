package com.channel360.workflow.domain.model;

import com.channel360.workflow.domain.enums.RequestStatus;
import java.util.List;
import java.util.Optional;

public record ExecutionResult(
    Optional<TaskPlan> nextTask,
    Optional<AuditPlan> audit,
    Optional<RequestStatus> newStatus,
    String message,
    boolean terminal,
    List<ExecutionResult> parallelResults
) {
    public static ExecutionResult approved(String message) {
        return new ExecutionResult(
            Optional.empty(), Optional.empty(),
            Optional.of(RequestStatus.APPROVED), message, true, List.of());
    }

    public static ExecutionResult rejected(String message) {
        return new ExecutionResult(
            Optional.empty(), Optional.empty(),
            Optional.of(RequestStatus.REJECTED), message, true, List.of());
    }

    public static ExecutionResult cancelled(String message) {
        return new ExecutionResult(
            Optional.empty(), Optional.empty(),
            Optional.of(RequestStatus.CANCELLED), message, true, List.of());
    }

    public static ExecutionResult withdrawn(String message) {
        return new ExecutionResult(
            Optional.empty(), Optional.empty(),
            Optional.of(RequestStatus.WITHDRAWN), message, true, List.of());
    }

    public static ExecutionResult continueFlow(TaskPlan task, AuditPlan audit) {
        return new ExecutionResult(
            Optional.of(task), Optional.of(audit),
            Optional.of(RequestStatus.IN_PROGRESS), null, false, List.of());
    }

    public static ExecutionResult advance(AuditPlan audit, String message) {
        return new ExecutionResult(
            Optional.empty(), Optional.of(audit),
            Optional.empty(), message, false, List.of());
    }

    public static ExecutionResult parallel(List<ExecutionResult> results) {
        return new ExecutionResult(
            Optional.empty(), Optional.empty(),
            Optional.empty(), "Parallel execution", false, results);
    }
}
