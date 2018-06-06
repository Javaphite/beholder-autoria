package home.javaphite.beholder;

import home.javaphite.testing.LoggedTestCase;
import home.javaphite.testing.TestScenario;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class TagBasedResolverTests extends LoggedTestCase {
    @Test
    @Tag("getLoader")
    void getLoader_MustReturnLoaderAssociatedWithExistentTag() {
        String testedTag = "custom";
        String expectedResult = "I am " + testedTag + " Loader";
        LoaderResolver<String> givenResolver = new TagBasedResolver<>();
        Map<String, Loader<String>> givenLoaders = getFakeLoaders();

        TestScenario<Object, String> scenario = new TestScenario<>();
        scenario.given("<LoaderResolver>: {@}", givenResolver)
                .given("AND bunch of <Loaders>: {@}", givenLoaders)
                .when("getLoader method of <LoaderResolver> invoked with argument '{}' AND <Loader's> load method invoked",
                        g ->((LoaderResolver<String>) g.get(0)).getLoader(givenLoaders, testedTag).load(), testedTag )
                .then("Return must be {}", expectedResult)
                .perform();

        countAsPassed();
    }

    @Test
    @Tag("getLoader")
    void getLoader_MustReturnDefaultLoaderIfCantFindTag() {
        String testedTag = "unknown_tag";
        String expectedResult = "I am default Loader";
        LoaderResolver<String> givenResolver = new TagBasedResolver<>();
        Map<String, Loader<String>> givenLoaders = getFakeLoaders();

        TestScenario<Object, String> scenario = new TestScenario<>();
        scenario.given("<LoaderResolver>: {@}", givenResolver)
                .given("AND bunch of <Loaders>: {@}", givenLoaders)
                .when("getLoader method of <LoaderResolver> invoked with argument '{}' AND <Loader's> load method invoked",
                        g ->((LoaderResolver<String>) g.get(0)).getLoader(givenLoaders, testedTag).load(), testedTag )
                .then("Return must be {}", expectedResult)
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
