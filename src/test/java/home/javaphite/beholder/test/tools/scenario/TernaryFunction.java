package home.javaphite.beholder.test.tools.scenario;

@FunctionalInterface
public interface TernaryFunction <X, Y, Z, R> extends MetaFunction<R> {
    R applyForParameters(X xValue, Y yValue, Z zValue);
}