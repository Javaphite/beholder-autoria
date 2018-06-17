package home.javaphite.beholder;

interface StorageService<T> {
    void queue(T data);

    void store();
}
