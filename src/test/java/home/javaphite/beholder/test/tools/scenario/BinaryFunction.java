package home.javaphite.beholder.test.tools.scenario;

@FunctionalInterface
public interface BinaryFunction<X, Y, R> extends MetaFunction<R> {
    R applyForParameters(X xValue, Y yValue);
}