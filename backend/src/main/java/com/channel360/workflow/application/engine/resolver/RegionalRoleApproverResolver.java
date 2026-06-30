package com.channel360.workflow.application.engine.resolver;

import com.channel360.workflow.domain.enums.ApproverType;
import com.channel360.workflow.domain.graph.GraphApproverRule;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
public class RegionalRoleApproverResolver implements ApproverResolver {
    @Override
    public ApproverType type() { return ApproverType.REGIONAL_ROLE; }

    @Override
    public List<Long> resolve(GraphApproverRule rule, BusinessContext ctx) {
        if (rule.roleName() == null || rule.regionId() == null) return Collections.emptyList();
        String key = "regionApprovers_" + rule.regionId() + "_" + rule.roleName();
        return ctx.getRaw(key) instanceof List<?> list
            ? list.stream().map(e -> ((Number) e).longValue()).toList()
            : Collections.emptyList();
    }
}
