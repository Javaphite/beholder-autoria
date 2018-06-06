package home.javaphite.beholder;

import java.util.Map;

public class TagBasedResolver<T> implements LoaderResolver<T> {

    @Override
    public Loader<T> getLoader(Map<String, Loader<T>> loaders, String sourceAddress){return null;}

}
