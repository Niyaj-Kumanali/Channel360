package com.channel360.workflow.domain.businessfield;

import java.math.BigDecimal;

public final class FinanceBusinessFields {
    public static final BusinessField<BigDecimal> AMOUNT = new BusinessField<>("amount", BigDecimal.class);
    public static final BusinessField<String> CURRENCY = new BusinessField<>("currency", String.class);
    public static final BusinessField<String> BUDGET_CODE = new BusinessField<>("budgetCode", String.class);
    public static final BusinessField<String> EXPENSE_TYPE = new BusinessField<>("expenseType", String.class);
    public static final BusinessField<String> APPROVAL_THRESHOLD = new BusinessField<>("approvalThreshold", String.class);

    private FinanceBusinessFields() {}
}
