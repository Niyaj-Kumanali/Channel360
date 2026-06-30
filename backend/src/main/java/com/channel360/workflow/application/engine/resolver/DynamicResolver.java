package com.channel360.workflow.application.engine.resolver;

import com.channel360.workflow.domain.enums.ApproverType;
import com.channel360.workflow.domain.graph.GraphApproverRule;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
public class DynamicResolver implements ApproverResolver {
    @Override
    public ApproverType type() { return ApproverType.DYNAMIC; }

    @Override
    public List<Long> resolve(GraphApproverRule rule, BusinessContext ctx) {
        if (rule.dynamicProvider() == null) return Collections.emptyList();
        Object result = ctx.getRaw("dynamic_" + rule.dynamicProvider());
        if (result instanceof List<?> list) {
            return list.stream().map(e -> ((Number) e).longValue()).toList();
        }
        if (result instanceof Number n) return List.of(n.longValue());
        return Collections.emptyList();
    }
}
