package com.channel360.workflow.application.designer.validation.rules.graph;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.enums.NodeType;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExactlyOneStartNodeRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.STRUCTURE; }
    @Override
    public String ruleName() { return "ExactlyOneStartNode"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        List<ValidationError> errors = new ArrayList<>();
        long count = graph.nodes().stream().filter(n -> n.type() == NodeType.START).count();
        if (count == 0) {
            errors.add(new ValidationError(ruleName(), "Workflow must have exactly one START node"));
        } else if (count > 1) {
            errors.add(new ValidationError(ruleName(), "Workflow has " + count + " START nodes, expected 1"));
        }
        return errors;
    }
}
