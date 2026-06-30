package com.channel360.workflow.domain.exception;

public class TaskAlreadyActedOnException extends RuntimeException {
    public TaskAlreadyActedOnException(Long taskId) {
        super("Task " + taskId + " has already been acted on");
    }
}
