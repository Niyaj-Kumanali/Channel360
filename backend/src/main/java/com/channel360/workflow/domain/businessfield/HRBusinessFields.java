package com.channel360.workflow.domain.businessfield;

public final class HRBusinessFields {
    public static final BusinessField<String> EMPLOYEE_ID = new BusinessField<>("employeeId", String.class);
    public static final BusinessField<String> EMPLOYEE_NAME = new BusinessField<>("employeeName", String.class);
    public static final BusinessField<String> ACTION_TYPE = new BusinessField<>("actionType", String.class);
    public static final BusinessField<String> DEPARTMENT = new BusinessField<>("department", String.class);
    public static final BusinessField<String> POSITION = new BusinessField<>("position", String.class);

    private HRBusinessFields() {}
}
