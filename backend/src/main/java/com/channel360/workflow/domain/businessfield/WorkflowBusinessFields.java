package com.channel360.workflow.domain.businessfield;

import java.math.BigDecimal;

public final class WorkflowBusinessFields {
    public static final BusinessField<BigDecimal> AMOUNT = new BusinessField<>("amount", BigDecimal.class);
    public static final BusinessField<String> REGION = new BusinessField<>("region", String.class);
    public static final BusinessField<String> DEPARTMENT = new BusinessField<>("department", String.class);
    public static final BusinessField<String> COST_CENTER = new BusinessField<>("costCenter", String.class);
    public static final BusinessField<Long> REQUESTOR_ID = new BusinessField<>("requestorId", Long.class);
    public static final BusinessField<String> REQUESTOR_ROLE = new BusinessField<>("requestorRole", String.class);
    public static final BusinessField<String> REQUEST_TYPE = new BusinessField<>("requestType", String.class);
    public static final BusinessField<String> PRIORITY = new BusinessField<>("priority", String.class);

    private WorkflowBusinessFields() {}
}
