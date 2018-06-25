package home.javaphite.beholder.load.loaders;

@FunctionalInterface
public interface Loader<T> {
    T load();
}
