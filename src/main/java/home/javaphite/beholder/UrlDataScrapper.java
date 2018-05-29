package home.javaphite.beholder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

abstract class UrlDataScrapper implements DataExctractor<String, Map<String, Object>>{
    DataSchema dataSchema;
    String sourceUrl;
    List<UnaryOperator<String>> filters;
    LoaderService loaderService;
    DatabaseAccessorService accessorService;

    UrlDataScrapper(DataSchema dataSchema, String sourceUrl, List<UnaryOperator<String>> filters){
        this.dataSchema=dataSchema;
        this.sourceUrl=sourceUrl;
        this.filters=new ArrayList<>(filters);
    }

    String applyFilters(String unfilteredString){
        String resultingString=unfilteredString;
        for (UnaryOperator<String> filter:filters) resultingString=filter.apply(resultingString);

        return resultingString;
    }

    //--- IMPLEMENT ME ---
    //# NEED TEST
    public void extractAndSend(){
        /*
        1) Ask LoaderService to upload data from url
        2) Then apply filters to received string
        3) Split resulting string to lines by delimiter and pack them to list - extract method
        4) Read data from list -> Set of data Maps - extract method
        5) Add set to the end of queue in accessorService
        */
    }

    // -------

    public void setAccessorService(DatabaseAccessorService accessorService) {
        this.accessorService = accessorService;
    }

    public void setLoaderService(LoaderService loaderService) {
        this.loaderService = loaderService;
    }
}
