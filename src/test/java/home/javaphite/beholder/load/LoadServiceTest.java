package home.javaphite.beholder.load;

import home.javaphite.beholder.load.loaders.Loader;
import home.javaphite.beholder.load.loaders.UrlLoader;
import home.javaphite.beholder.test.utils.log.LoggedTestCase;
import home.javaphite.beholder.test.utils.scenario.UnaryFunction;
import home.javaphite.beholder.test.utils.scenario.TestScenario;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class LoadServiceTest extends LoggedTestCase {
    @Test
    void getContent_MustReturnContentOfSourceByLink() {
        String testLine = "CONTENT";
        LoadService<String> service = new LoadService<>();
        LoaderResolver<String> resolver = mock(LoaderResolver.class);
        Loader<String> loader = mock(UrlLoader.class);
        UnaryFunction<LoadService<String>, String> action = s -> s.getContent("some link");

        when(loader.load()).thenReturn(testLine);
        when(resolver.getLoader(anyString())).thenReturn(loader);
        service.setResolver(resolver);

        TestScenario scenario = new TestScenario();
        scenario.given("LoadService specialized on strings: {@}", service)
                .when("Try to getContent for some link", action)
                .then("It must return string: {@}", testLine)
                .perform();

        countAsPassed();
    }
}
