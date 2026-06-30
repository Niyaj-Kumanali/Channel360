package com.channel360.workflow.application.engine.resolver;

import com.channel360.workflow.domain.enums.ApproverType;
import com.channel360.workflow.domain.graph.GraphApproverRule;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import java.util.List;

public interface ApproverResolver {
    ApproverType type();
    List<Long> resolve(GraphApproverRule rule, BusinessContext ctx);
}
