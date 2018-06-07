package home.javaphite.testing;

@FunctionalInterface
public interface MonoFunction<X, R> extends MetaFunction<R> {
    R applyForParameters(X value);
}