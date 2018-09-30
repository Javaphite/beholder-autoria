package home.javaphite.beholder.test.tools.scenario;


/**
 * Node-interface for family of {@link FunctionalInterface}{@code s} representing function entities to treat them similar way.
 * Current version supports only functions of 1-3 different type input parameters.
 * <p>
 * DISCLAIMER: interface is <b>experimental</b> and you should use standard Java SE 8 functional interfaces
 * rather than that one and it's sub-interfaces;
 * @param <R> function return type;
 */

@FunctionalInterface
public interface MetaFunction<R> {

    R apply(Object... params);

}