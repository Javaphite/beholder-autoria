package home.javaphite.beholder.load;

import home.javaphite.beholder.load.loaders.UrlLoader;
import home.javaphite.beholder.test.tools.log.TestLifecycleLogger;
import home.javaphite.beholder.test.tools.scenario.TestScenario;
import home.javaphite.beholder.test.tools.scenario.UnaryFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("LoadService")
class LoadServiceTest extends TestLifecycleLogger {
    @Test
    @Tag("loadContent")
    void returnContentOfSourceByLink() {
        String testLine = "CONTENT";
        LoadService service = new LoadService();
        UnaryFunction<LoadService, String> action = s -> s.loadContent("some link");

        /*when(loader.load()).thenReturn(testLine);
        when(resolver.getLoader(anyString())).thenReturn(loader);
        service.setResolver(resolver);

        TestScenario scenario = new TestScenario();
        scenario.given("LoadService specialized on strings: {@}", service)
                .when("Try to loadContent for some link", action)
                .then("It must return string: {@}", testLine)
                .perform();*/
    }
}
