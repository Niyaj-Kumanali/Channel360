package com.channel360.workflow.application.designer;

import com.channel360.workflow.application.designer.model.GraphState;
import com.channel360.workflow.application.designer.validation.GraphConsistencyValidator;
import com.channel360.workflow.application.designer.validation.ValidationResult;
import com.channel360.workflow.application.designer.validation.WorkflowValidator;
import com.channel360.workflow.application.serialization.JsonSerializer;
import com.channel360.workflow.domain.entity.*;
import com.channel360.workflow.domain.enums.NodeType;
import com.channel360.workflow.domain.enums.VersionStatus;
import com.channel360.workflow.domain.exception.WorkflowNotFoundException;
import com.channel360.workflow.domain.exception.WorkflowValidationException;
import com.channel360.workflow.domain.graph.*;
import com.channel360.workflow.domain.model.CompiledWorkflow;
import com.channel360.workflow.infrastructure.persistence.repository.*;
import com.channel360.workflow.infrastructure.workflowdefinition.CachedWorkflowDefinitionProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowDesignerService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowVersionRepository versionRepository;
    private final WorkflowNodeRepository nodeRepository;
    private final WorkflowTransitionRepository transitionRepository;
    private final NodeAssignmentRepository assignmentRepository;
    private final ApproverRuleRepository approverRuleRepository;
    private final GraphConsistencyValidator consistencyValidator;
    private final WorkflowValidator workflowValidator;
    private final JsonSerializer jsonSerializer;
    private final CachedWorkflowDefinitionProvider definitionProvider;

    public String loadGraphJson(Long workflowId) {
        WorkflowVersion draft = findDraftVersion(workflowId);
        return draft.getGraphJson();
    }

    @Transactional
    public void saveGraph(Long workflowId, WorkflowGraph graph, Map<UUID, GraphState> nodeStates,
                           Map<UUID, GraphState> transitionStates) {
        WorkflowVersion version = findDraftVersion(workflowId);

        Map<UUID, GraphNode> nodeMap = graph.nodes().stream()
            .collect(Collectors.toMap(GraphNode::id, n -> n));
        consistencyValidator.validateNodes(nodeStates, nodeMap);
        consistencyValidator.validateTransitions(transitionStates);

        applyNodeChanges(version, graph, nodeStates);
        applyTransitionChanges(version, graph, transitionStates);
        applyAssignmentChanges(version, graph);

        version.setGraphJson(jsonSerializer.toJson(graph));
        versionRepository.save(version);
    }

    public ValidationResult validateGraph(Long workflowId) {
        WorkflowVersion draft = findDraftVersion(workflowId);
        WorkflowGraph graph = jsonSerializer.fromJson(draft.getGraphJson(), WorkflowGraph.class);
        return workflowValidator.validate(graph);
    }

    private WorkflowVersion findDraftVersion(Long workflowId) {
        return versionRepository.findTopByWorkflowIdAndStatusOrderByVersionNumberDesc(workflowId, VersionStatus.DRAFT)
            .orElseThrow(() -> new WorkflowNotFoundException("No draft version for workflow " + workflowId));
    }

    private void applyNodeChanges(WorkflowVersion version, WorkflowGraph graph, Map<UUID, GraphState> states) {
        for (Map.Entry<UUID, GraphState> entry : states.entrySet()) {
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
                    nodeRepository.findByNodeUuid(nodeId).ifPresent(nodeRepository::delete);
                }
                case NEW -> {
                    GraphNode gn = graph.findNode(nodeId).orElse(null);
                    if (gn == null) break;
                    WorkflowNode entity = WorkflowNode.builder()
                        .nodeUuid(gn.id()).workflowVersion(version)
                        .name(gn.name()).type(gn.type()).terminalType(gn.terminalType())
                        .label(gn.label()).description(gn.description()).build();
                    nodeRepository.save(entity);
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
                    });
                }
            }
        }
    }

    private void applyTransitionChanges(WorkflowVersion version, WorkflowGraph graph,
                                         Map<UUID, GraphState> states) {
        for (Map.Entry<UUID, GraphState> entry : states.entrySet()) {
            UUID transId = entry.getKey();
            GraphState state = entry.getValue();
            switch (state) {
                case DELETED -> transitionRepository.findByTransitionUuid(transId)
                    .ifPresent(transitionRepository::delete);
                case NEW -> {
                    GraphTransition gt = graph.transitions().stream()
                        .filter(t -> t.id().equals(transId)).findFirst().orElse(null);
                    if (gt == null) break;
                    WorkflowNode source = nodeRepository.findByNodeUuid(gt.sourceNodeId()).orElse(null);
                    WorkflowNode target = nodeRepository.findByNodeUuid(gt.targetNodeId()).orElse(null);
                    if (source == null || target == null) break;
                    transitionRepository.save(WorkflowTransition.builder()
                        .transitionUuid(gt.id()).sourceNode(source).targetNode(target)
                        .action(gt.action()).label(gt.label()).priority(gt.priority()).build());
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
                    });
                }
            }
        }
    }

    private void applyAssignmentChanges(WorkflowVersion version, WorkflowGraph graph) {
        for (GraphAssignment ga : graph.assignments()) {
            WorkflowNode node = nodeRepository.findByNodeUuid(ga.id()).orElse(null);
            if (node == null) continue;

            assignmentRepository.findByNodeId(node.getId()).ifPresent(a -> {
                approverRuleRepository.deleteByAssignmentId(a.getId());
                assignmentRepository.delete(a);
            });

            NodeAssignment assignment = assignmentRepository.save(NodeAssignment.builder()
                .assignmentUuid(ga.id()).node(node).policy(ga.policy())
                .requiredApprovalCount(ga.requiredApprovalCount()).build());

            for (GraphApproverRule rule : ga.rules()) {
                approverRuleRepository.save(ApproverRule.builder()
                    .ruleUuid(rule.id()).assignment(assignment)
                    .approverType(rule.type()).roleName(rule.roleName())
                    .userId(rule.userId()).regionId(rule.regionId())
                    .department(rule.department()).dynamicProvider(rule.dynamicProvider()).build());
            }
        }
    }
}
