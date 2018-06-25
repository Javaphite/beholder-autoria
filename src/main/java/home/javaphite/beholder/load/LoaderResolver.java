package home.javaphite.beholder.load;

import home.javaphite.beholder.load.loaders.Loader;

@FunctionalInterface
public interface LoaderResolver<T> {
    Loader<T> getLoader(String link);
}
