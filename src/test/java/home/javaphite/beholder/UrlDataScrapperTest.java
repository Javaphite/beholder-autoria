package home.javaphite.beholder;

import home.javaphite.testing.TestScenario;
import home.javaphite.testing.LoggedTestCase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

@Tag("home.javaphite.beholder.UrlDataScrapper")
class UrlDataScrapperTest extends LoggedTestCase{

    @Test
    void applyFiltersMustReturnStringTransformedWithAllFilters(){
        String textWithHtmlTags="<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Title of the document</title>" +
                                "</head><body>Content of the document......</body></html>";
        String expectedResultingText="titleofthedocumentcontentofthedocument";

        List<UnaryOperator<String>> filters=new ArrayList<>();
        String htmlTagsPattern="<\\\\?[^<]+>";
        String punctuationCharsPattern="[. !?,]";
        filters.add(input->input.replaceAll(htmlTagsPattern,""));
        filters.add(input->input.replaceAll(punctuationCharsPattern,""));
        filters.add(String::toLowerCase);

        UrlDataScrapper extractorStub=new UrlDataScrapper(null, null, filters) {
            @Override
           public Set<Map<String, Object>> extractFrom(String dataInDelimitedString) {
                return null;
            }
        };

        TestScenario<Object, String> scenario=new TestScenario<>();
        scenario.given("<UrlDataScrapper> with filters: {}, {}, {}.", extractorStub,
                        "HTML tag remover","simple punctuation remover", "lowercase converter")
                .given("AND HTML page <text>: {@}", textWithHtmlTags)
                .when("<UrlDataScrapper's> filters applied to <text>",
                        g->((UrlDataScrapper) g.get(0)).applyFilters((String) g.get(1)) )
                .then("Resulting string should be: {@}", expectedResultingText)
                .perform();

        countAsPassed();
    }

    @Test
    void extractAndSendBehaviorTest(){

    }
}
