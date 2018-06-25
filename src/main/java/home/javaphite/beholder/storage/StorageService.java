package home.javaphite.beholder.storage;

public interface StorageService<T> {
    void queue(T data);

    void store();
}
