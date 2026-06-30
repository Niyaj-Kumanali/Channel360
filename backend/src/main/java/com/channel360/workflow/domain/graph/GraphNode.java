package com.channel360.workflow.domain.graph;

import com.channel360.workflow.domain.enums.NodeType;
import com.channel360.workflow.domain.enums.TerminalType;
import java.util.UUID;

public record GraphNode(
    UUID id,
    String name,
    NodeType type,
    TerminalType terminalType,
    String label,
    String description,
    Long entityVersion
) {
    public GraphNode(UUID id, String name, NodeType type, TerminalType terminalType,
                     String label, String description) {
        this(id, name, type, terminalType, label, description, null);
    }
}
