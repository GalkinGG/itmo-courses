package expression.generic;

import expression.exceptions.*;
import expression.generic.calculator.*;

import java.util.Map;

public class GenericTabulator implements Tabulator {

    public static void main(String[] args) {
        String mode = args[0];
        String expression = args[1];
        Object[][][] result = new GenericTabulator().tabulate(mode, expression, -2, 2, -2, 2, -2, 2);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < 5; k++) {
                    System.out.println(result[i][j][k]);
                }
            }
        }
    }

    private final Map<String, Calculator<?>> modes =
            Map.of(
                    "bi", new BigIntCalculator(),
                    "d", new DoubleCalculator(),
                    "i", new CheckedIntegerCalculator(),
                    "u", new UncheckedIntegerCalculator(),
                    "f", new FloatCalculator(),
                    "s", new ShortCalculator()
            );


    @Override
    public Object[][][] tabulate(
            String mode,
            String expression,
            int x1, int x2,
            int y1, int y2,
            int z1, int z2
    ) throws ParseException {
        if (!modes.containsKey(mode)) {
            throw new ParseException("Unknown mode: " + mode);
        }
        return calculateTable(expression, x1, x2, y1, y2, z1, z2, modes.get(mode));
    }

    private <T> Object[][][] calculateTable(
            String expression,
            int x1, int x2,
            int y1, int y2,
            int z1, int z2,
            Calculator<T> calculator) {
        ExpressionParser<T> parser = new ExpressionParser<>();
        GenericExpression<T> parsedExpression = parser.parse(expression, calculator);
        Object[][][] ans = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                for (int k = z1; k <= z2; k++) {
                    try {
                        ans[i - x1][j - y1][k - z1] = parsedExpression.evaluate(
                                calculator.getFromInt(i),
                                calculator.getFromInt(j),
                                calculator.getFromInt(k),
                                calculator);
                    } catch (CalculationException ignored) {}
                }
            }
        }
        return ans;
    }

}
