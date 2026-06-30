package com.channel360.workflow.application.engine.condition.operator;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class BetweenOperator implements Operator {
    @Override
    public String type() { return "between"; }

    @Override
    public boolean evaluate(Object fieldValue, Object expectedValue) {
        if (fieldValue == null || expectedValue == null) return false;
        String[] parts = expectedValue.toString().split(",");
        if (parts.length != 2) return false;
        BigDecimal val = toBigDecimal(fieldValue);
        return val.compareTo(new BigDecimal(parts[0].trim())) >= 0
            && val.compareTo(new BigDecimal(parts[1].trim())) <= 0;
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val instanceof BigDecimal bd) return bd;
        if (val instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        return new BigDecimal(val.toString());
    }
}
