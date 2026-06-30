package com.channel360.workflow.application.designer.validation;

import com.channel360.workflow.domain.graph.WorkflowGraph;
import java.util.List;

public interface ValidationRule {
    ValidationPhase phase();
    String ruleName();
    List<ValidationError> validate(WorkflowGraph graph);
}
