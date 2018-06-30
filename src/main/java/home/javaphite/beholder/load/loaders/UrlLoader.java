package home.javaphite.beholder.load.loaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Scanner;

public class UrlLoader{
    private static final Logger LOG = LoggerFactory.getLogger(UrlLoader.class);
    private String source;

    public UrlLoader(String link) {
       source = link;
    }

    public String load() {
        String result;
       try ( Scanner scanner = new Scanner(setupConnection().getInputStream()) ) {
            StringBuilder builder = new StringBuilder();
            scanner.useDelimiter("(?m)$"); //matches position after end of each line (multi-line mode)
            scanner.forEachRemaining(builder::append);
            result = Objects.toString(builder);
        }
        catch (IOException connectionError) {
            LOG.error("Connection error: ", connectionError);
            throw new UncheckedIOException(connectionError);
        }
        return result;
    }

    private URL getUrl(String link) {
        URL url;
        try {
            LOG.debug("Trying to form URL object for {}", link);
            url = new URL(link);
            LOG.debug("URL object for {} successfully created!", link);
        }
        catch (MalformedURLException badUrlException) {
            LOG.error(" String {} is not appropriate URL link: {}", link, badUrlException);
            throw new IllegalArgumentException(badUrlException);
        }
        return url;
    }

    private URLConnection setupConnection() {
        URL url = getUrl(source);
        URLConnection connection;
        try {
            LOG.info("Trying to establish connection to {}", url);
            connection = url.openConnection();
            LOG.info("Connection to {} successfully established!", url);
        }
        catch (IOException connectionError) {
            LOG.error("Connection establishment failed:", connectionError);
            throw new UncheckedIOException(connectionError);
        }
        return connection;
    }
}