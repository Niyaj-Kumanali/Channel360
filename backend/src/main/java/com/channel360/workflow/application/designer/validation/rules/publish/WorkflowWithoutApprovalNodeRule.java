package com.channel360.workflow.application.designer.validation.rules.publish;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.enums.NodeType;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class WorkflowWithoutApprovalNodeRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.PUBLISH; }
    @Override
    public String ruleName() { return "WorkflowWithoutApprovalNode"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        boolean hasApproval = graph.nodes().stream()
            .anyMatch(n -> n.type() == NodeType.APPROVAL);
        if (!hasApproval) {
            return List.of(new ValidationError(ruleName(),
                "Workflow has no APPROVAL nodes. Consider if this is intentional."));
        }
        return List.of();
    }
}
