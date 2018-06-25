package home.javaphite.beholder.extraction;

import java.util.Set;

@FunctionalInterface
interface DataExtractor<T, R> {
    Set<R> extract(T source);
}
