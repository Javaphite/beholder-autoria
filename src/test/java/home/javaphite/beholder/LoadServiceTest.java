package home.javaphite.beholder;

import home.javaphite.testing.LoggedTestCase;
import home.javaphite.testing.MonoFunction;
import home.javaphite.testing.TestScenario;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class LoadServiceTest extends LoggedTestCase {
    @Test
    void getContent_MustReturnContentOfSourceByLink() {
        String testLine = "CONTENT";
        LoadService<String> service = new LoadService<>();
        LoaderResolver<String> resolver = mock(LoaderResolver.class);
        Loader<String> loader = mock(UrlLoader.class);

        when(loader.load()).thenReturn(testLine);
        when(resolver.getLoader(anyString())).thenReturn(loader);
        service.setResolver(resolver);
        MonoFunction<LoadService<String>, String> action = s -> s.getContent("some link");

        TestScenario scenario = new TestScenario();
        scenario.given("LoadService specialized on strings: {@}", service)
                .when("Try to getContent for some link", action)
                .then("It must return string: {@}", testLine)
                .perform();

        countAsPassed();
    }
}
