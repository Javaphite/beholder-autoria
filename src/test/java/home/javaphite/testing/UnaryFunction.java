package home.javaphite.testing;

@FunctionalInterface
public interface UnaryFunction<X, R> extends MetaFunction<R> {
    R applyForParameters(X value);
}