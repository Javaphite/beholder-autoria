package home.javaphite.beholder.test.tools.scenario;

@FunctionalInterface
public interface BinaryFunction<X, Y, R> extends MetaFunction<R> {
    @Override
    default R apply(Object... o) {
        return apply((X) o[0], (Y) o[1]);
    }

    R apply(X xValue, Y yValue);
}