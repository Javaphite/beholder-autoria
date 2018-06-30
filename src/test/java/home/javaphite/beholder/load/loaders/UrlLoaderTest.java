package home.javaphite.beholder.load.loaders;

import home.javaphite.beholder.test.tools.log.TestLifecycleLogger;
import home.javaphite.beholder.test.tools.scenario.TestScenario;
import home.javaphite.beholder.test.tools.scenario.UnaryFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.util.Objects;

@DisplayName("UrlLoader")
class UrlLoaderTest extends TestLifecycleLogger {
    @Test
    @Tag("load")
    void returnsFileContentWithoutChangesByURL() {
        String text = "HEADER LINE" + System.lineSeparator() + "Another line";
        File file = createTestFile(text);
        String filePath = getFileUrl(file);
        UrlLoader loader = new UrlLoader(filePath);
        UnaryFunction<UrlLoader, String> action = UrlLoader::load;

        TestScenario scenario = new TestScenario();
        scenario.given("UrlLoader linked with file {}", loader, filePath)
                .when("Try to load content of file using the loader", action)
                .then("It must return: {@}", text)
                .perform();
    }

    /* Creates new temporary file in system temp directory
     and sets it to be deleted on program exit */
    private File createTestFile(String text) {
        try {
            File newTempFile = File.createTempFile("test_file_", ".tmp");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(newTempFile))) {
                writer.append(text);
                writer.flush();
                writer.close();
            }
            newTempFile.deleteOnExit();
            return newTempFile;
        }
        catch (IOException fileIoException) {
            LOG.error(fileIoException.getMessage());
            throw new UncheckedIOException(fileIoException);
        }
    }

    private String getFileUrl(File file) {
        try {
            String fileUrl = Objects.toString(file.toURI().toURL());
            return fileUrl;
        } catch (MalformedURLException urlException) {
            LOG.error(urlException.getMessage());
            throw new UncheckedIOException(urlException);
        }
    }
}
