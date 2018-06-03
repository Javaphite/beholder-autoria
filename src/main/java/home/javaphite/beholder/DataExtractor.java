package home.javaphite.beholder;

import java.util.Set;

interface DataExtractor<T, R> {
    Set<R> extract(T source);
}
