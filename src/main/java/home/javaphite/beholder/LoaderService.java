package home.javaphite.beholder;

import java.util.Map;

class LoaderService<T> {
    private LoaderResolver<T> resolver;
    private Map<String, Loader<T>> loaders;

    T getContent(String sourceAddress) {
        Loader<? extends T> loader = resolver.getLoader(loaders, sourceAddress);
        return loader.load();
    }

    void setResolver(LoaderResolver<T> resolver) {
        if (this.resolver == null)
            this.resolver = resolver;
    }
}
