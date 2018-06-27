package home.javaphite.beholder.load.loaders;

import home.javaphite.beholder.test.tools.log.TestLifecycleLogger;
import home.javaphite.beholder.test.tools.scenario.UnaryFunction;
import home.javaphite.beholder.test.tools.scenario.TestScenario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.MalformedURLException;

@DisplayName("UrlLoader")
class UrlLoaderTest extends TestLifecycleLogger {

    @Test
    void loadMethod_BehaviourTest() {
        String text = "HEADER LINE" + System.lineSeparator() + "Another line";
        File file = createTestFile(text);
        String filePath = getFileUrl(file);
        Loader<String> loader = new UrlLoader(filePath);
        UnaryFunction<Loader<String>, String> action = Loader::load;

        TestScenario scenario = new TestScenario();
        scenario.given("UrlLoader linked with file {}", loader, filePath)
                .when("Try to load content of file using the loader", action)
                .then("It must return: {@}", text)
                .perform();

        file.deleteOnExit();
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
            LOG.error(e.getMessage());
        }

        return newTempFile;
    }

    private String getFileUrl(File file) {
        String fileUrl = null;

        try {
            fileUrl = file.toURI().toURL().toString();
        } catch (MalformedURLException urlException) {
            LOG.error(urlException.getMessage());
        }
        return fileUrl;
    }
}
