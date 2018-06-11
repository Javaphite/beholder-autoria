package home.javaphite.beholder;

import home.javaphite.testing.LoggedTestCase;
import home.javaphite.testing.TernaryFunction;
import home.javaphite.testing.TestScenario;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

class TagBasedResolverTests extends LoggedTestCase {
    @ParameterizedTest
    @ValueSource(strings={"custom", "unknown"})
    @Tag("getLoader")
    void getLoader_SpecifiedOrDefaultLoadersShouldBeFound(String testedTag) {
        LoaderResolver<String> givenResolver = new TagBasedResolver<>();
        Map<String, Loader<String>> givenLoaders = getFakeLoaders();
        TernaryFunction<LoaderResolver<String>,  Map<String, Loader<String>>, String, String> action =
                (resolver, loaders, tag) -> resolver.getLoader(loaders, ()->tag, ()->"default").toString();

        String expectedResult = testedTag.equals("custom") ? "CustomLoader" : "DefaultLoader";

        TestScenario scenario = new TestScenario();
        scenario.given("LoaderResolver: {}", givenResolver, givenResolver.getClass().getName())
                .given("AND group of Loaders: {@}", givenLoaders)
                .given("AND tag: {@}", testedTag)
                .when("Trying to get loader by tag", action)
                .then("Returned Loader must be: {@}", expectedResult)
                .perform();

        countAsPassed();
    }

    @Test
    void getLoader_ExceptionThrownIf_NeitherSpecifiedNorDefaultLoadersFound() {
        LoaderResolver<String> givenResolver = new TagBasedResolver<>();
        Map<String, Loader<String>> givenLoaders = getFakeLoaders();
        Class <? extends Throwable> expectedException = IllegalArgumentException.class;
        Boolean expectedResult = Boolean.TRUE;

        TernaryFunction<LoaderResolver<String>,  Map<String, Loader<String>>, String, Boolean> action =
                (resolver, loaders, tag) -> checkExceptionThrown(expectedException, () -> resolver.getLoader(loaders, ()->tag, ()->"unknown") );

        TestScenario scenario = new TestScenario();
        scenario.given("LoaderResolver: {}", givenResolver, givenResolver.getClass().getName())
                .given("AND group of Loaders: {@}", givenLoaders)
                .given("AND tag: {@}", "unknown")
                .when("Trying to get loader by unknown tag while default variant also unknown", action)
                .then("Condition must be {@}: exception thrown {}", expectedResult, expectedException)
                .perform();
    }

    private Map<String, Loader<String>> getFakeLoaders() {
        Map<String, Loader<String>> fakeLoaders = new HashMap<>();
        fakeLoaders.put("default", createFakeLoader("DefaultLoader"));
        fakeLoaders.put("custom", createFakeLoader("CustomLoader"));
        fakeLoaders.put("specific", createFakeLoader("VerySpecificLoader"));

        return fakeLoaders;
    }

    private Loader<String> createFakeLoader(String predefinedReturn){
       Loader<String> fakeLoader = new Loader<String> () {
           @Override
           public String load() {
               return predefinedReturn;
           }

           @Override
           public String toString() {
               return predefinedReturn;
           }
       };
       return fakeLoader;
   }
}
