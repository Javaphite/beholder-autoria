package home.javaphite.beholder;

import java.util.Set;

interface DataExctractor<T, R> {
    Set<R> extractFrom(T source);
}
