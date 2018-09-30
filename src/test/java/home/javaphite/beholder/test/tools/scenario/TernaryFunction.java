package home.javaphite.beholder.test.tools.scenario;

@FunctionalInterface
public interface TernaryFunction <X, Y, Z, R> extends MetaFunction<R> {
    @Override
    default R apply(Object... o) {
        return apply((X) o[0], (Y) o[1], (Z) o[2]);
    }

    R apply(X xValue, Y yValue, Z zValue);
}