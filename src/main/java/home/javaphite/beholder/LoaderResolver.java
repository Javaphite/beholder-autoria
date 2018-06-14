package home.javaphite.beholder;

interface LoaderResolver<T> {
    Loader<T> getLoader(String link) throws IllegalArgumentException;
}
