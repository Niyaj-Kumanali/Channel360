package com.channel360.workflow.api.mapper;

import com.channel360.workflow.api.dto.designer.WorkflowGraphDTO;
import com.channel360.workflow.domain.graph.*;
import org.springframework.stereotype.Component;

@Component
public class WorkflowGraphMapper {

    private final NodeMapper nodeMapper;
    private final TransitionMapper transitionMapper;
    private final AssignmentMapper assignmentMapper;

    public WorkflowGraphMapper(NodeMapper nodeMapper, TransitionMapper transitionMapper,
                                AssignmentMapper assignmentMapper) {
        this.nodeMapper = nodeMapper;
        this.transitionMapper = transitionMapper;
        this.assignmentMapper = assignmentMapper;
    }

    public WorkflowGraph toDomain(WorkflowGraphDTO dto) {
        return new WorkflowGraph(
            dto.nodes().stream().map(nodeMapper::toDomain).toList(),
            dto.transitions().stream().map(transitionMapper::toDomain).toList(),
            dto.assignments().stream().map(assignmentMapper::toDomain).toList()
        );
    }
}
