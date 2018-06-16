package home.javaphite.beholder;

class LoadService<T> {
    private LoaderResolver<T> resolver;

    T getContent(String link) {
       Loader<? extends T> loader = resolver.getLoader(link);
       return loader.load();
    }

    void setResolver(LoaderResolver<T> resolver) throws IllegalArgumentException {
        if (resolver != null)
            this.resolver = resolver;
        else
            throw new IllegalArgumentException("Expected instance of LoaderResolver class, but get null");
    }
}
