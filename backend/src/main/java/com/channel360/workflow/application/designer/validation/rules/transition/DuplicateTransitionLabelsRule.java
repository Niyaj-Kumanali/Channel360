package com.channel360.workflow.application.designer.validation.rules.transition;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DuplicateTransitionLabelsRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.TRANSITIONS; }
    @Override
    public String ruleName() { return "DuplicateTransitionLabels"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        List<ValidationError> errors = new ArrayList<>();
        Map<UUID, Map<String, Long>> bySource = graph.transitions().stream()
            .collect(Collectors.groupingBy(
                t -> t.sourceNodeId(),
                Collectors.groupingBy(t -> t.label() != null ? t.label() : "", Collectors.counting())));
        for (var sourceEntry : bySource.entrySet()) {
            for (var labelEntry : sourceEntry.getValue().entrySet()) {
                if (labelEntry.getValue() > 1) {
                    errors.add(new ValidationError(ruleName(),
                        "Duplicate transition label '" + labelEntry.getKey()
                        + "' from node " + sourceEntry.getKey(),
                        "NODE", sourceEntry.getKey().toString()));
                }
            }
        }
        return errors;
    }
}
