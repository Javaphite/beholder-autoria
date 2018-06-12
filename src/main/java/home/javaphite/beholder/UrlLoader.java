package home.javaphite.beholder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

class UrlLoader implements Loader<String> {
    private static final Logger logger = LoggerFactory.getLogger(UrlLoader.class);

    private URL url;
    private URLConnection connection;

    UrlLoader(String link) {
        setUrl(link);
        setupConnection();
    }

    public String load() {
        String result;

        try ( InputStream inStream = connection.getInputStream() ) {
            Scanner scanner = new Scanner(inStream);
            StringBuilder stringBuilder = new StringBuilder();

            scanner.useDelimiter("(?m)$"); //matches position after end of each line (multi-line mode)
            while (scanner.hasNext()) {
                stringBuilder.append(scanner.next());
            }
            result = stringBuilder.toString();
        }
        catch (IOException connectionIoException) {
            logger.error("Connection error: {}", connectionIoException.getMessage());
            throw new RuntimeException(connectionIoException);
        }

        return result;
    }

    private void setUrl(String link) {
        try {
            logger.debug("Trying to form URL object for {}", link);
            this.url = new URL(link);
            logger.debug("URL object for {} successfully created!", link);
        }
        catch (MalformedURLException badUrlException) {
            logger.error(" String {} is not appropriate URL link: {}", link, badUrlException.getMessage());
            throw new IllegalArgumentException(badUrlException);
        }
    }

    private void setupConnection() {
        if (url != null) {
            try {
                logger.debug("Trying to establish connection to {}", url);
                connection = url.openConnection();
                logger.debug("Connection to {} successfully established!", url);
            }
            catch (IOException connectionEstablishmentError) {
                logger.error("Connection establishment failed: {}", connectionEstablishmentError);
                throw new RuntimeException(connectionEstablishmentError);
            }
        }
        else {
            String nullUrlErrorMsg = "Connection establishment failed: URL is null.";

            logger.error(nullUrlErrorMsg);
            throw new IllegalArgumentException(nullUrlErrorMsg);
        }
    }



}