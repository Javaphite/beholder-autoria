package home.javaphite.beholder.load;

import home.javaphite.beholder.load.loaders.Loader;

public class LoadService<T> {
    private LoaderResolver<T> resolver;

    public T getContent(String link) {
       Loader<? extends T> loader = resolver.getLoader(link);
       return loader.load();
    }

    public void setResolver(LoaderResolver<T> resolver) {
        if (null != resolver) {
            this.resolver = resolver;
        }
        else {
            throw new IllegalArgumentException("Expected instance of LoaderResolver class, but get null.");
        }
    }
}
