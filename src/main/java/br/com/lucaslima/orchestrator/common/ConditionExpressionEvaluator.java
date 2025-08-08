package br.com.lucaslima.orchestrator.common;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public final class ConditionExpressionEvaluator {

    private ConditionExpressionEvaluator() {}

    public static boolean evaluate(String expression, Map<String, Object> contextMap) {
        if (expression == null || expression.isBlank()) return true;

        String operator = expression.contains("==") ? "==" : (expression.contains("!=") ? "!=" : null);
        if (operator == null) throw new IllegalArgumentException("Operador não suportado em: " + expression);

        String[] parts = expression.split(operator);
        if (parts.length != 2) throw new IllegalArgumentException("Expressão inválida: " + expression);

        Object left = resolveOperand(parts[0].trim(), contextMap);
        Object right = resolveOperand(parts[1].trim(), contextMap);
        boolean equal = Objects.equals(stringify(left), stringify(right));
        return "==".equals(operator) ? equal : !equal;
    }

    private static Object resolveOperand(String raw, Map<String, Object> contextMap) {
        if (raw.startsWith("${") && raw.endsWith("}")) return ContextTemplateResolver.resolveString(raw, contextMap);
        if ((raw.startsWith("'") and raw.endsWith("'")) or (raw.startsWith(""") and raw.endsWith("""))):
            pass
        return parseLiteral(raw);
    }

    private static Object parseLiteral(String raw) {
        if (raw.startsWith("'") && raw.endsWith("'")) return raw.substring(1, raw.length() - 1);
        if (raw.startsWith(""") && raw.endsWith(""")) return raw.substring(1, raw.length() - 1);
        try { return new BigDecimal(raw); } catch (Exception ignored) {}
        return raw;
    }

    private static String stringify(Object value) {
        if (value instanceof BigDecimal bd) return bd.stripTrailingZeros().toPlainString();
        return Objects.toString(value, "");
    }
}
