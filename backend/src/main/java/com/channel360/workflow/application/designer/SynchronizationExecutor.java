package com.channel360.workflow.application.designer;

import com.channel360.workflow.application.designer.model.GraphState;
import com.channel360.workflow.application.designer.model.SyncResult;
import com.channel360.workflow.application.designer.model.SynchronizationPlan;
import com.channel360.workflow.application.serialization.JsonSerializer;
import com.channel360.workflow.domain.entity.*;
import com.channel360.workflow.domain.graph.GraphNode;
import com.channel360.workflow.domain.graph.GraphTransition;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import com.channel360.workflow.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class SynchronizationExecutor {

    private final WorkflowNodeRepository nodeRepository;
    private final WorkflowTransitionRepository transitionRepository;
    private final NodeAssignmentRepository assignmentRepository;
    private final ApproverRuleRepository approverRuleRepository;
    private final JsonSerializer jsonSerializer;

    public SyncResult execute(com.channel360.workflow.domain.entity.WorkflowVersion version,
                               WorkflowGraph graph, Map<UUID, GraphState> nodeStates,
                               Map<UUID, GraphState> transitionStates) {
        AtomicInteger nodesCreated = new AtomicInteger(0);
        AtomicInteger nodesUpdated = new AtomicInteger(0);
        AtomicInteger nodesDeleted = new AtomicInteger(0);
        AtomicInteger transCreated = new AtomicInteger(0);
        AtomicInteger transUpdated = new AtomicInteger(0);
        AtomicInteger transDeleted = new AtomicInteger(0);
        List<String> warnings = new ArrayList<>();

        for (var entry : nodeStates.entrySet()) {
            UUID nodeId = entry.getKey();
            GraphState state = entry.getValue();
            switch (state) {
                case DELETED -> {
                    assignmentRepository.findByNodeId(
                        nodeRepository.findByNodeUuid(nodeId).map(WorkflowNode::getId).orElse(null)
                    ).ifPresent(a -> {
                        approverRuleRepository.deleteByAssignmentId(a.getId());
                        assignmentRepository.delete(a);
                    });
                    nodeRepository.findByNodeUuid(nodeId).ifPresent(n -> {
                        nodeRepository.delete(n);
                        nodesDeleted.incrementAndGet();
                    });
                }
                case NEW -> {
                    GraphNode gn = graph.findNode(nodeId).orElse(null);
                    if (gn == null) break;
                    nodeRepository.save(WorkflowNode.builder()
                        .nodeUuid(gn.id()).workflowVersion(version)
                        .name(gn.name()).type(gn.type()).terminalType(gn.terminalType())
                        .label(gn.label()).description(gn.description()).build());
                    nodesCreated.incrementAndGet();
                }
                case MODIFIED -> {
                    GraphNode gn = graph.findNode(nodeId).orElse(null);
                    if (gn == null) break;
                    nodeRepository.findByNodeUuid(nodeId).ifPresent(existing -> {
                        existing.setName(gn.name());
                        existing.setType(gn.type());
                        existing.setTerminalType(gn.terminalType());
                        existing.setLabel(gn.label());
                        existing.setDescription(gn.description());
                        nodeRepository.save(existing);
                        nodesUpdated.incrementAndGet();
                    });
                }
            }
        }

        for (var entry : transitionStates.entrySet()) {
            UUID transId = entry.getKey();
            GraphState state = entry.getValue();
            switch (state) {
                case DELETED -> {
                    transitionRepository.findByTransitionUuid(transId).ifPresent(t -> {
                        transitionRepository.delete(t);
                        transDeleted.incrementAndGet();
                    });
                }
                case NEW -> {
                    GraphTransition gt = graph.transitions().stream()
                        .filter(t -> t.id().equals(transId)).findFirst().orElse(null);
                    if (gt == null) break;
                    WorkflowNode source = nodeRepository.findByNodeUuid(gt.sourceNodeId()).orElse(null);
                    WorkflowNode target = nodeRepository.findByNodeUuid(gt.targetNodeId()).orElse(null);
                    if (source == null || target == null) {
                        warnings.add("Cannot create transition " + transId + ": source or target node not found");
                        break;
                    }
                    transitionRepository.save(WorkflowTransition.builder()
                        .transitionUuid(gt.id()).sourceNode(source).targetNode(target)
                        .action(gt.action()).label(gt.label()).priority(gt.priority()).build());
                    transCreated.incrementAndGet();
                }
                case MODIFIED -> {
                    transitionRepository.findByTransitionUuid(transId).ifPresent(existing -> {
                        GraphTransition gt = graph.transitions().stream()
                            .filter(t -> t.id().equals(transId)).findFirst().orElse(null);
                        if (gt == null) return;
                        existing.setAction(gt.action());
                        existing.setLabel(gt.label());
                        existing.setPriority(gt.priority());
                        transitionRepository.save(existing);
                        transUpdated.incrementAndGet();
                    });
                }
            }
        }

        syncAssignments(version, graph);

        version.setGraphJson(jsonSerializer.toJson(graph));

        return new SyncResult(true, nodesCreated.get(), nodesUpdated.get(), nodesDeleted.get(),
            transCreated.get(), transUpdated.get(), transDeleted.get(), warnings);
    }

    private void syncAssignments(com.channel360.workflow.domain.entity.WorkflowVersion version,
                                  WorkflowGraph graph) {
        for (var ga : graph.assignments()) {
            WorkflowNode node = nodeRepository.findByNodeUuid(ga.id()).orElse(null);
            if (node == null) continue;

            assignmentRepository.findByNodeId(node.getId()).ifPresent(a -> {
                approverRuleRepository.deleteByAssignmentId(a.getId());
                assignmentRepository.delete(a);
            });

            NodeAssignment assignment = assignmentRepository.save(NodeAssignment.builder()
                .assignmentUuid(ga.id()).node(node).policy(ga.policy())
                .requiredApprovalCount(ga.requiredApprovalCount()).build());

            for (var rule : ga.rules()) {
                approverRuleRepository.save(ApproverRule.builder()
                    .ruleUuid(rule.id()).assignment(assignment)
                    .approverType(rule.type()).roleName(rule.roleName())
                    .userId(rule.userId()).regionId(rule.regionId())
                    .department(rule.department()).dynamicProvider(rule.dynamicProvider()).build());
            }
        }
    }
}
