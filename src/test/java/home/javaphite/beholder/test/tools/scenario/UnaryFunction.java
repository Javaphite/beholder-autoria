package home.javaphite.beholder.test.tools.scenario;

@FunctionalInterface
public interface UnaryFunction<X, R> extends MetaFunction<R> {
    @Override
    default R apply(Object... o) {
        return apply((X) o[0]);
    }

    R apply(X xValue);
}