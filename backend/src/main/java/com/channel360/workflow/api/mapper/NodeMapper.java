package com.channel360.workflow.api.mapper;

import com.channel360.workflow.api.dto.designer.WorkflowGraphDTO;
import com.channel360.workflow.domain.enums.NodeType;
import com.channel360.workflow.domain.enums.TerminalType;
import com.channel360.workflow.domain.graph.GraphNode;
import org.springframework.stereotype.Component;

@Component
public class NodeMapper {

    public GraphNode toDomain(WorkflowGraphDTO.NodeDTO dto) {
        return new GraphNode(
            dto.id(), dto.name(),
            NodeType.valueOf(dto.type()),
            dto.terminalType() != null ? TerminalType.valueOf(dto.terminalType()) : null,
            dto.label(), dto.description(), dto.entityVersion()
        );
    }

    public WorkflowGraphDTO.NodeDTO toDTO(GraphNode node) {
        return new WorkflowGraphDTO.NodeDTO(
            node.id(), node.name(), node.type().name(),
            node.terminalType() != null ? node.terminalType().name() : null,
            node.label(), node.description(), null, node.entityVersion()
        );
    }
}
