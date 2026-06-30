package com.channel360.workflow.application.designer.validation.rules.transition;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.enums.NodeType;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TerminalNodeNoOutgoingRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.TRANSITIONS; }
    @Override
    public String ruleName() { return "TerminalNodeNoOutgoing"; }

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        Set<UUID> terminalIds = graph.nodes().stream()
            .filter(n -> n.type() == NodeType.END)
            .map(n -> n.id()).collect(Collectors.toSet());
        List<ValidationError> errors = new ArrayList<>();
        for (var t : graph.transitions()) {
            if (terminalIds.contains(t.sourceNodeId())) {
                String nodeName = graph.findNode(t.sourceNodeId())
                    .map(n -> n.name()).orElse("unknown");
                errors.add(new ValidationError(ruleName(),
                    "END node '" + nodeName + "' has an outgoing transition",
                    "NODE", t.sourceNodeId().toString()));
            }
        }
        return errors;
    }
}
