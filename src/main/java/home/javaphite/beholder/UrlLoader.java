package home.javaphite.beholder;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

class UrlLoader implements Loader<String> {
    private URL webPageUrl;
    private URLConnection connection;

    UrlLoader(String webPageUrl) {
        try {
            this.webPageUrl = new URL(webPageUrl);
        }
        catch (MalformedURLException badUrlException){
            System.out.println("Error: Given string is not appropriate URL link.");
            throw new IllegalArgumentException(badUrlException);
        }

        try {
            this.connection = this.webPageUrl.openConnection();
        }
        catch (IOException connectionEstablishmentError) {
            System.out.println("Error: connection couldn't be established.");
            throw new RuntimeException(connectionEstablishmentError);
        }
    }

    public String load() {
        String result="";
        try (InputStream inStream = connection.getInputStream()) {
            Scanner scanner = new Scanner(inStream);
            StringBuilder sb = new StringBuilder();

            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }

            result = sb.toString();
        }
        catch (Exception e) {}

        return result;
    }

}
