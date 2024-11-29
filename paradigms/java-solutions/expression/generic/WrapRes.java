package expression.generic;

public class WrapRes<T> {
    public GenericExpression<T> result;

    public WrapRes(GenericExpression<T> result) {
        this.result = result;
    }

}
