package expression.exceptions;

import java.util.Scanner;
import expression.*;


public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ExpressionParser expressionParser = new ExpressionParser();
        TripleExpression expression = expressionParser.parse(sc.nextLine());

        System.out.println(expression.evaluate(1266608772, 1302925319, 616961863));
    }
}
