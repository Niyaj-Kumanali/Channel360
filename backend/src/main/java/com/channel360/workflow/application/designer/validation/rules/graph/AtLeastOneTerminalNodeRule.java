package com.channel360.workflow.application.designer.validation.rules.graph;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.enums.NodeType;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class AtLeastOneTerminalNodeRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.STRUCTURE; }
    @Override
    public String ruleName() { return "AtLeastOneTerminalNode"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        List<ValidationError> errors = new ArrayList<>();
        boolean hasTerminal = graph.nodes().stream().anyMatch(n -> n.type() == NodeType.END);
        if (!hasTerminal) {
            errors.add(new ValidationError(ruleName(), "Workflow must have at least one END node"));
        }
        return errors;
    }
}
