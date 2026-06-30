package com.channel360.workflow.application.designer.validation;

import com.channel360.workflow.domain.graph.WorkflowGraph;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@RequiredArgsConstructor
public class WorkflowValidator {

    private final List<ValidationRule> rules;

    public ValidationResult validate(WorkflowGraph graph) {
        Map<ValidationPhase, List<ValidationRule>> grouped = new LinkedHashMap<>();
        for (ValidationPhase phase : ValidationPhase.values()) {
            grouped.put(phase, new ArrayList<>());
        }
        for (ValidationRule rule : rules) {
            grouped.get(rule.phase()).add(rule);
        }

        List<ValidationError> allErrors = new ArrayList<>();
        for (ValidationPhase phase : ValidationPhase.values()) {
            List<ValidationRule> phaseRules = grouped.get(phase);
            if (phaseRules.isEmpty()) continue;

            List<ValidationError> phaseErrors = new ArrayList<>();
            for (ValidationRule rule : phaseRules) {
                phaseErrors.addAll(rule.validate(graph));
            }
            allErrors.addAll(phaseErrors);

            if (!phaseErrors.isEmpty()) {
                break;
            }
        }

        return allErrors.isEmpty()
            ? ValidationResult.success()
            : ValidationResult.failure(allErrors);
    }
}
