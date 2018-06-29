package home.javaphite.beholder.load;

import home.javaphite.beholder.load.loaders.UrlLoader;

public class LoadService {
    /**
     * Loads text content or remote resource by URL.
     * @param link URL link to resource.
     * @return text content of resource.
     */
    public String loadContent(String link) {
       UrlLoader loader = new UrlLoader(link);
       return loader.load();
    }
}
