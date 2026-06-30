package com.channel360.workflow.application.engine.condition.operator;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class LtOperator implements Operator {
    @Override
    public String type() { return "lt"; }

    @Override
    public boolean evaluate(Object fieldValue, Object expectedValue) {
        if (fieldValue == null || expectedValue == null) return false;
        return toBigDecimal(fieldValue).compareTo(toBigDecimal(expectedValue)) < 0;
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val instanceof BigDecimal bd) return bd;
        if (val instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        return new BigDecimal(val.toString());
    }
}
