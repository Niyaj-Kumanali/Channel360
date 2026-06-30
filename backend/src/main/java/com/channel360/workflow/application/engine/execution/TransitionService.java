package com.channel360.workflow.application.engine.execution;

import com.channel360.workflow.application.engine.resolver.ApproverResolver;
import com.channel360.workflow.domain.enums.*;
import com.channel360.workflow.domain.exception.AmbiguousTransitionException;
import com.channel360.workflow.domain.exception.ApproverResolutionException;
import com.channel360.workflow.domain.exception.NoValidTransitionException;
import com.channel360.workflow.domain.graph.GraphApproverRule;
import com.channel360.workflow.domain.graph.GraphAssignment;
import com.channel360.workflow.domain.model.*;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TransitionService {

    private final Map<ApproverType, ApproverResolver> resolvers;

    public CompiledTransition resolveTransition(CompiledWorkflow workflow, NodeRef currentNode,
                                                 TransitionAction action, BusinessContext ctx) {
        List<CompiledTransition> candidates = workflow.getOutgoing(currentNode.nodeId()).stream()
            .filter(t -> t.action() == action)
            .sorted(Comparator.comparingInt(CompiledTransition::priority))
            .toList();

        if (candidates.isEmpty()) {
            throw new NoValidTransitionException(currentNode.nodeId(), action.name());
        }

        List<CompiledTransition> matched = candidates.stream()
            .filter(t -> t.condition().evaluate(ctx))
            .toList();

        if (matched.isEmpty()) {
            throw new NoValidTransitionException(currentNode.nodeId(), action.name());
        }

        if (matched.size() > 1) {
            boolean allSameTarget = matched.stream()
                .map(CompiledTransition::targetNodeId).distinct().count() == 1;
            if (!allSameTarget) {
                throw new AmbiguousTransitionException(currentNode.nodeId(), action.name(), matched.size());
            }
            return matched.get(0);
        }

        return matched.get(0);
    }

    public ExecutionResult advanceToNode(CompiledWorkflow workflow, NodeRef targetNode,
                                          BusinessContext ctx, AuditPlan audit) {
        if (targetNode.type() == NodeType.END) {
            boolean isRejection = targetNode.terminalType() == TerminalType.REJECTION;
            return isRejection
                ? ExecutionResult.rejected("Workflow reached rejection terminal: " + targetNode.name())
                : ExecutionResult.approved("Workflow completed at: " + targetNode.name());
        }

        if (targetNode.type() == NodeType.APPROVAL) {
            TaskPlan taskPlan = resolveTaskPlan(targetNode, ctx);
            return ExecutionResult.continueFlow(taskPlan, audit);
        }

        if (targetNode.type() == NodeType.GATEWAY) {
            return ExecutionResult.advance(audit, "Passed through gateway: " + targetNode.name());
        }

        return ExecutionResult.advance(audit, "Advanced to: " + targetNode.name());
    }

    private TaskPlan resolveTaskPlan(NodeRef node, BusinessContext ctx) {
        List<ResolvedAssignment> assignees = resolveApprovers(node, ctx);
        AssignmentPolicy policy = AssignmentPolicy.ANY_ONE;
        return new TaskPlan(node.nodeId(), assignees, policy);
    }

    private List<ResolvedAssignment> resolveApprovers(NodeRef node, BusinessContext ctx) {
        List<GraphApproverRule> rules = ctx.getRaw("assignmentRules_" + node.nodeId()) instanceof List<?> list
            ? list.stream().map(e -> (GraphApproverRule) e).toList()
            : List.of();

        if (rules.isEmpty()) {
            throw new ApproverResolutionException(node.nodeId(), "No assignment rules configured");
        }

        List<ResolvedAssignment> results = new ArrayList<>();
        for (GraphApproverRule rule : rules) {
            ApproverResolver resolver = resolvers.get(rule.type());
            if (resolver == null) continue;
            List<Long> userIds = resolver.resolve(rule, ctx);
            for (Long userId : userIds) {
                results.add(new ResolvedAssignment(userId, rule.type()));
            }
        }

        if (results.isEmpty()) {
            throw new ApproverResolutionException(node.nodeId(), "No approvers could be resolved");
        }
        return results;
    }
}
