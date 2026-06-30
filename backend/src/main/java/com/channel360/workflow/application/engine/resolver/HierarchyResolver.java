package com.channel360.workflow.application.engine.resolver;

import com.channel360.workflow.domain.enums.ApproverType;
import com.channel360.workflow.domain.graph.GraphApproverRule;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
public class HierarchyResolver implements ApproverResolver {
    @Override
    public ApproverType type() { return ApproverType.HIERARCHY; }

    @Override
    public List<Long> resolve(GraphApproverRule rule, BusinessContext ctx) {
        Long requestorId = ctx.getRaw("requestorId") instanceof Number n ? n.longValue() : null;
        if (requestorId == null) return Collections.emptyList();
        Object managerId = ctx.getRaw("managerOf_" + requestorId);
        return managerId != null ? List.of(((Number) managerId).longValue()) : Collections.emptyList();
    }
}
