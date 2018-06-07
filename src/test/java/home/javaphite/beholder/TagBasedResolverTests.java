package home.javaphite.beholder;

import home.javaphite.testing.LoggedTestCase;
import home.javaphite.testing.TernaryFunction;
import home.javaphite.testing.TestScenario;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

class TagBasedResolverTests extends LoggedTestCase {
    @ParameterizedTest
    @ValueSource(strings={"custom", "unknown"})
    @Tag("getLoader")
    void getLoader_BehaviorTest(String testedTag) {
        LoaderResolver<String> givenResolver = new TagBasedResolver<>();
        Map<String, Loader<String>> givenLoaders = getFakeLoaders();
        TernaryFunction<LoaderResolver<String>,  Map<String, Loader<String>>, String, String> action =
                (resolver, loaders, tag) -> resolver.getLoader(loaders, tag).load();

        String expectedResult = testedTag.equals("custom") ? "I am custom Loader" : "I am default Loader";

        TestScenario scenario = new TestScenario();
        scenario.given("LoaderResolver: {@}", givenResolver)
                .given("AND group of Loaders: ", givenLoaders)
                .given("AND tag: {@}", testedTag)
                .when("getLoader method of resolver invoked with tag AND loader's load method invoked", action, testedTag )
                .then("Return must be {@}", expectedResult)
                .perform();

        countAsPassed();
    }

    private Map<String, Loader<String>> getFakeLoaders() {
        Map<String, Loader<String>> fakeLoaders = new HashMap<>();
        fakeLoaders.put("default", createFakeStringLoader("I am default Loader"));
        fakeLoaders.put("custom", createFakeStringLoader("I am custom Loader"));
        fakeLoaders.put("specific", createFakeStringLoader("I am very specific Loader"));

        return fakeLoaders;
    }

    private Loader<String> createFakeStringLoader(String predefinedReturn){
       Loader<String> fakeLoader = () -> predefinedReturn;
       return fakeLoader;
   }


}
