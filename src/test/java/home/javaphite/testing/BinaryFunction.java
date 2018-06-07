package home.javaphite.testing;

@FunctionalInterface
public interface BinaryFunction<X, Y, R> extends MetaFunction<R> {
    R applyForParameters(X xValue, Y yValue);
}