package home.javaphite.beholder.test.tools.scenario;


/**
 * Node-interface for family of {@link FunctionalInterface}{@code s} representing function entities to treat them similar way.
 * Current version supports only functions of 1-3 different type input parameters.
 * <p>
 * DISCLAIMER: interface is <b>experimental</b> and you should use standard Java SE 8 functional interfaces
 * rather than that one and it's sub-interfaces;
 * @param <R> function return type;
 */
public interface MetaFunction<R> {
    default <X> R apply(X value) {
        R result = ((UnaryFunction<X, R>) this).applyForParameters(value);
        return result;
    }

    default <X, Y> R apply(X xValue, Y yValue) {
        R result = ((BinaryFunction<X, Y, R>) this).applyForParameters(xValue, yValue);
        return result;
    }

    default <X, Y, Z> R apply(X xValue, Y yValue, Z zValue) {
        R result = ((TernaryFunction<X, Y, Z, R>) this).applyForParameters(xValue, yValue, zValue);
        return result;
    }
}