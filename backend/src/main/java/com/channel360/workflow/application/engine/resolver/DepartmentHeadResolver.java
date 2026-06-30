package com.channel360.workflow.application.engine.resolver;

import com.channel360.workflow.domain.enums.ApproverType;
import com.channel360.workflow.domain.graph.GraphApproverRule;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
public class DepartmentHeadResolver implements ApproverResolver {
    @Override
    public ApproverType type() { return ApproverType.DEPARTMENT_HEAD; }

    @Override
    public List<Long> resolve(GraphApproverRule rule, BusinessContext ctx) {
        String department = rule.department() != null
            ? rule.department()
            : (String) ctx.getRaw("department");
        if (department == null) return Collections.emptyList();
        Object headId = ctx.getRaw("departmentHead_" + department);
        return headId != null ? List.of(((Number) headId).longValue()) : Collections.emptyList();
    }
}
