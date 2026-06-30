package com.channel360.workflow.application.designer.validation.rules.assignment;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.enums.ApproverType;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class UnpublishedApproverRolesRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.ASSIGNMENTS; }
    @Override
    public String ruleName() { return "UnpublishedApproverRoles"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        List<ValidationError> errors = new ArrayList<>();
        Set<String> roleNames = new HashSet<>();
        for (var assignment : graph.assignments()) {
            for (var rule : assignment.rules()) {
                if (rule.type() == ApproverType.ROLE && rule.roleName() != null) {
                    if (!roleNames.add(rule.roleName())) continue;
                    if (!rule.roleName().matches("^[A-Z_]+$")) {
                        errors.add(new ValidationError(ruleName(),
                            "Approver role '" + rule.roleName() + "' does not match expected format",
                            "APPROVER_RULE", rule.id().toString()));
                    }
                }
            }
        }
        return errors;
    }
}
