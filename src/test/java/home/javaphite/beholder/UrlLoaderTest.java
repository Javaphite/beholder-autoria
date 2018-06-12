package home.javaphite.beholder;

import home.javaphite.testing.LoggedTestCase;
import home.javaphite.testing.MonoFunction;
import home.javaphite.testing.TestScenario;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.MalformedURLException;


class UrlLoaderTest extends LoggedTestCase {

    @Test
    void loadMethod_BehaviourTest() {
        String text = "HEADER LINE" + System.lineSeparator() + "Another line";
        File file = createTestFile(text);
        String filePath = getFileUrl(file);
        Loader<String> loader = new UrlLoader(filePath);
        MonoFunction<Loader<String>, String> action = Loader::load;

        TestScenario scenario = new TestScenario();
        scenario.given("UrlLoader linked with file {}", loader, filePath)
                .when("Try to load content of file using the loader", action)
                .then("It must return: {@}", text)
                .perform();

        file.deleteOnExit();
        countAsPassed();
    }

    private File createTestFile(String text) {
        File newTempFile = null;

        try {
            newTempFile = File.createTempFile("test_file_", ".tmp");
            BufferedWriter writer = new BufferedWriter(new FileWriter(newTempFile));
            writer.append(text);
            writer.flush();
            writer.close();
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }

        return newTempFile;
    }

    private String getFileUrl(File file) {
        String fileUrl = null;

        try {
            fileUrl = file.toURI().toURL().toString();
        } catch (MalformedURLException urlException) {
            logger.error(urlException.getMessage());
        }
        return fileUrl;
    }
}
