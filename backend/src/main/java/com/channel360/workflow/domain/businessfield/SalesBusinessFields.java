package com.channel360.workflow.domain.businessfield;

import java.math.BigDecimal;

public final class SalesBusinessFields {
    public static final BusinessField<String> SALES_REP = new BusinessField<>("salesRep", String.class);
    public static final BusinessField<String> CLIENT_NAME = new BusinessField<>("clientName", String.class);
    public static final BusinessField<BigDecimal> DISCOUNT_PERCENT = new BusinessField<>("discountPercent", BigDecimal.class);
    public static final BusinessField<String> DEAL_STAGE = new BusinessField<>("dealStage", String.class);
    public static final BusinessField<String> PRODUCT_LINE = new BusinessField<>("productLine", String.class);

    private SalesBusinessFields() {}
}
