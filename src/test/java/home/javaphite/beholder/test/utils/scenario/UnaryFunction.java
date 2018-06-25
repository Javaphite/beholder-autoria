package home.javaphite.beholder.test.utils.scenario;

@FunctionalInterface
public interface UnaryFunction<X, R> extends MetaFunction<R> {
    R applyForParameters(X value);
}