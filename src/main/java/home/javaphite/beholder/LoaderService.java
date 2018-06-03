package home.javaphite.beholder;

//UNDER CONSTRUCTION
interface LoaderService<T> {
   default T getContent(String sourceAddress){
        Loader<? extends T> loader=resolveLoaderFor(sourceAddress);
        return loader.load();
    }

   Loader<T> resolveLoaderFor(String source);

}
