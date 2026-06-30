package com.channel360.workflow.application.engine.condition.compiler;

import com.channel360.workflow.domain.enums.ConditionType;
import com.channel360.workflow.domain.enums.LogicalOperator;
import com.channel360.workflow.domain.enums.NodeType;
import com.channel360.workflow.domain.graph.GraphConditionExpression;
import com.channel360.workflow.domain.graph.GraphNode;
import com.channel360.workflow.domain.graph.GraphTransition;
import com.channel360.workflow.domain.graph.WorkflowGraph;
import com.channel360.workflow.domain.model.CompiledCondition;
import com.channel360.workflow.domain.model.CompiledTransition;
import com.channel360.workflow.domain.model.CompiledWorkflow;
import com.channel360.workflow.domain.model.NodeRef;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class ConditionCompiler {

    private final Map<String, BiFunction<Object, String, Boolean>> operators = createOperators();

    public CompiledWorkflow compile(WorkflowGraph graph) {
        Map<UUID, NodeRef> nodes = graph.nodes().stream()
            .collect(Collectors.toMap(GraphNode::id, n ->
                new NodeRef(n.id(), n.name(), n.type(), n.terminalType())));

        Map<UUID, List<CompiledTransition>> outgoing = new LinkedHashMap<>();
        for (GraphTransition t : graph.transitions()) {
            CompiledCondition condition = compileCondition(t.condition());
            outgoing.computeIfAbsent(t.sourceNodeId(), k -> new ArrayList<>())
                .add(new CompiledTransition(t.id(), t.label(), t.action(),
                    t.sourceNodeId(), t.targetNodeId(), condition, t.priority()));
        }

        GraphNode start = graph.nodes().stream()
            .filter(n -> n.type() == NodeType.START)
            .findFirst().orElseThrow(() -> new IllegalStateException("WorkflowGraph must have exactly one START node"));

        return new CompiledWorkflow(null, start.id(), nodes, outgoing);
    }

    private CompiledCondition compileCondition(GraphConditionExpression expr) {
        if (expr == null) return CompiledCondition.alwaysTrue();

        if (expr.type() == ConditionType.GROUP) {
            List<CompiledCondition> compiled = expr.children().stream()
                .map(this::compileCondition)
                .toList();
            Predicate<BusinessContext> predicate = expr.operator() == LogicalOperator.AND
                ? ctx -> compiled.stream().allMatch(c -> c.evaluate(ctx))
                : ctx -> compiled.stream().anyMatch(c -> c.evaluate(ctx));
            return new CompiledCondition(predicate);
        }

        BiFunction<Object, String, Boolean> op = operators.get(expr.op());
        if (op == null) {
            return CompiledCondition.alwaysFalse();
        }
        String expectedValue = expr.value();
        return new CompiledCondition(ctx -> {
            Object val = ctx.getRaw(expr.field());
            return val != null && op.apply(val, expectedValue);
        });
    }

    private static Map<String, BiFunction<Object, String, Boolean>> createOperators() {
        Map<String, BiFunction<Object, String, Boolean>> map = new HashMap<>();
        map.put("eq", (val, expected) -> val.toString().equals(expected));
        map.put("neq", (val, expected) -> !val.toString().equals(expected));
        map.put("gt", (val, expected) -> compare(val, expected) > 0);
        map.put("gte", (val, expected) -> compare(val, expected) >= 0);
        map.put("lt", (val, expected) -> compare(val, expected) < 0);
        map.put("lte", (val, expected) -> compare(val, expected) <= 0);
        map.put("contains", (val, expected) ->
            val.toString().toLowerCase().contains(expected.toLowerCase()));
        map.put("in", (val, expected) ->
            Arrays.asList(expected.split(",")).contains(val.toString()));
        map.put("between", (val, expected) -> {
            String[] parts = expected.split(",");
            if (parts.length != 2) return false;
            return compare(val, parts[0]) >= 0 && compare(val, parts[1]) <= 0;
        });
        return Collections.unmodifiableMap(map);
    }

    private static int compare(Object val, String expected) {
        if (val instanceof BigDecimal bd) return bd.compareTo(new BigDecimal(expected));
        if (val instanceof Number n) return BigDecimal.valueOf(n.doubleValue()).compareTo(new BigDecimal(expected));
        return val.toString().compareTo(expected);
    }
}
