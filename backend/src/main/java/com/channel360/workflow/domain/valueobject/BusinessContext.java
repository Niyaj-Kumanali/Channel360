package com.channel360.workflow.domain.valueobject;

import com.channel360.workflow.domain.businessfield.BusinessField;
import java.util.Map;

public final class BusinessContext {
    private final Map<String, Object> values;

    public BusinessContext(Map<String, Object> values) {
        this.values = Map.copyOf(values);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(BusinessField<T> field) {
        return (T) values.get(field.name());
    }

    public Object getRaw(String key) {
        return values.get(key);
    }

    public Map<String, Object> values() {
        return values;
    }
}
