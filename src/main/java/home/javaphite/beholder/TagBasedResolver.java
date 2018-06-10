package home.javaphite.beholder;

import java.util.Map;

public class TagBasedResolver<T> implements LoaderResolver<T> {
    @Override
    public Loader<T> getLoader(Map<String, Loader<T>> loaders, String link) throws IllegalArgumentException {
        Loader<T> loader = loaders.get(link);
        if (loader == null)
            throw new IllegalArgumentException("Can't find element for tag: " + link);

        return loader;
    }
}
