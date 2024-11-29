package expression;

import expression.parser.ExpressionParser;

import java.util.InputMismatchException;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int value = sc.nextInt();
        AllExpressions exp1 = new Add(
                                new Subtract(
                                    new Multiply(
                                        new Variable("x"), 
                                        new Variable("x")
                                        ), 
                                    new Multiply(
                                        new Const(2), 
                                        new Variable("x")
                                        )
                                ), 
                                new Const(1)
                                );
        try {
            System.out.println(exp1.evaluate(value));
        } catch (InputMismatchException e) {
            System.out.println("Unexpected variable: " + e.getMessage());
        }
    }
}
