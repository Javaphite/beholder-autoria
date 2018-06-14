package home.javaphite.beholder;

import java.util.Map;
import java.util.function.Supplier;

interface LoaderResolver<T> {
    Loader<T> getLoader(Map<String, Loader<T>> loaders, String link) throws IllegalArgumentException;

    default Loader<T> getLoader(Map<String, Loader<T>> loaders, Supplier<String> linkSupplier)  throws IllegalArgumentException {
        return getLoader(loaders, linkSupplier.get());
    }

    default Loader<T> getLoader(Map<String, Loader<T>> loaders, Supplier<String> link, Supplier<String> defaultLoader) throws IllegalArgumentException {
        Loader<T> loader;
        try {
            loader = getLoader(loaders,link.get());
        }
        catch (IllegalArgumentException noSuchTagException) {
            loader = getLoader(loaders, defaultLoader.get());
        }

        return loader;
    }
}
