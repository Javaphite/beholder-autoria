package home.javaphite.beholder;

import java.util.HashMap;
import java.util.Map;

class LoaderService<T> {
    private LoaderResolver<T> resolver;
    private Map<String, Loader<T>> loaders = new HashMap<>();

    void registerLoader(String alias, Loader<T> loader) {
        loaders.put(alias, loader);
    }

    T getContent(String link) {
        Loader<? extends T> loader = resolver.getLoader(loaders, link);
        return loader.load();
    }

    void setResolver(LoaderResolver<T> resolver) {
        if (this.resolver == null)
            this.resolver = resolver;
    }
}
