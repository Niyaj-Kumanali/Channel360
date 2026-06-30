package com.channel360.workflow.api.exception;

import com.channel360.common.dto.response.ErrorResponse;
import com.channel360.workflow.domain.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class WorkflowExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WorkflowExceptionHandler.class);

    @ExceptionHandler(WorkflowNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(WorkflowNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.builder().success(false).message(ex.getMessage())
                .timestamp(LocalDateTime.now()).statusCode(404).build());
    }

    @ExceptionHandler(WorkflowValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(WorkflowValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.builder().success(false).message("Validation failed")
                .errors(ex.getErrors()).timestamp(LocalDateTime.now()).statusCode(400).build());
    }

    @ExceptionHandler(WorkflowInUseException.class)
    public ResponseEntity<ErrorResponse> handleInUse(WorkflowInUseException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse.builder().success(false).message(ex.getMessage())
                .timestamp(LocalDateTime.now()).statusCode(409).build());
    }

    @ExceptionHandler(TaskAlreadyActedOnException.class)
    public ResponseEntity<ErrorResponse> handleTaskAlreadyActed(TaskAlreadyActedOnException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse.builder().success(false).message(ex.getMessage())
                .timestamp(LocalDateTime.now()).statusCode(409).build());
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(OptimisticLockException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse.builder().success(false).message(ex.getMessage())
                .timestamp(LocalDateTime.now()).statusCode(409).build());
    }

    @ExceptionHandler({NoValidTransitionException.class, AmbiguousTransitionException.class})
    public ResponseEntity<ErrorResponse> handleTransition(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.builder().success(false).message(ex.getMessage())
                .timestamp(LocalDateTime.now()).statusCode(400).build());
    }

    @ExceptionHandler(GraphConsistencyException.class)
    public ResponseEntity<ErrorResponse> handleGraphConsistency(GraphConsistencyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse.builder().success(false).message(ex.getMessage())
                .timestamp(LocalDateTime.now()).statusCode(409).build());
    }

    @ExceptionHandler(ApproverResolutionException.class)
    public ResponseEntity<ErrorResponse> handleApproverResolution(ApproverResolutionException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ErrorResponse.builder().success(false).message(ex.getMessage())
                .timestamp(LocalDateTime.now()).statusCode(422).build());
    }
}
