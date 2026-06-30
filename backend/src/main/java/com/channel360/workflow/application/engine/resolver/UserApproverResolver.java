package com.channel360.workflow.application.engine.resolver;

import com.channel360.workflow.domain.enums.ApproverType;
import com.channel360.workflow.domain.graph.GraphApproverRule;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
public class UserApproverResolver implements ApproverResolver {
    @Override
    public ApproverType type() { return ApproverType.USER; }

    @Override
    public List<Long> resolve(GraphApproverRule rule, BusinessContext ctx) {
        if (rule.userId() == null) return Collections.emptyList();
        return List.of(rule.userId());
    }
}
