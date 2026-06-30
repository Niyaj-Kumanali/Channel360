package com.channel360.workflow.application.designer.validation.rules.publish;

import com.channel360.workflow.application.designer.validation.*;
import com.channel360.workflow.domain.enums.NodeType;
import com.channel360.workflow.domain.enums.TransitionAction;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class ValidTransitionActionsRule implements ValidationRule {
    @Override
    public ValidationPhase phase() { return ValidationPhase.PUBLISH; }
    @Override
    public String ruleName() { return "ValidTransitionActions"; }

    private static final Map<NodeType, Set<TransitionAction>> VALID_ACTIONS = Map.of(
        NodeType.START, Set.of(TransitionAction.SUBMIT, TransitionAction.AUTO),
        NodeType.APPROVAL, Set.of(TransitionAction.APPROVE, TransitionAction.REJECT,
            TransitionAction.SEND_BACK, TransitionAction.DELEGATE, TransitionAction.ESCALATE,
            TransitionAction.TIMEOUT),
        NodeType.TASK, Set.of(TransitionAction.APPROVE, TransitionAction.REJECT,
            TransitionAction.SEND_BACK),
        NodeType.GATEWAY, Set.of(TransitionAction.AUTO),
        NodeType.END, Set.of()
    );

    @Override
    public List<ValidationError> validate(WorkflowGraph graph) {
        Map<UUID, NodeType> nodeTypes = new HashMap<>();
        for (var node : graph.nodes()) {
            nodeTypes.put(node.id(), node.type());
        }

        List<ValidationError> errors = new ArrayList<>();
        for (var t : graph.transitions()) {
            NodeType sourceType = nodeTypes.get(t.sourceNodeId());
            if (sourceType != null) {
                Set<TransitionAction> valid = VALID_ACTIONS.getOrDefault(sourceType, Set.of());
                if (!valid.contains(t.action())) {
                    String nodeName = graph.findNode(t.sourceNodeId())
                        .map(n -> n.name()).orElse("unknown");
                    errors.add(new ValidationError(ruleName(),
                        "Action " + t.action() + " is not valid for " + sourceType
                        + " node '" + nodeName + "'",
                        "TRANSITION", t.id().toString()));
                }
            }
        }
        return errors;
    }
}
