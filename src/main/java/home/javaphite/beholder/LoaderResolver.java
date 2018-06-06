package home.javaphite.beholder;

import java.util.Map;

interface LoaderResolver<T> {
    Loader<T> getLoader(Map<String, Loader<T>> loaders, String sourceAddress);
}
