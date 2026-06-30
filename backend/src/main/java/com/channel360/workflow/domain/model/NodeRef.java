package com.channel360.workflow.domain.model;

import com.channel360.workflow.domain.enums.NodeType;
import com.channel360.workflow.domain.enums.TerminalType;
import java.util.UUID;

public record NodeRef(
    UUID nodeId,
    String name,
    NodeType type,
    TerminalType terminalType
) {
}
