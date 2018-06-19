package home.javaphite.beholder;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

// UNDER CONSTRUCTION
class ConcurrentStorageService<T> implements StorageService<T> {
   final Queue<T> storageQueue = new ConcurrentLinkedQueue<>();
   private Accessor<T> storageAccessor;

    public void queue(T data) {
            storageQueue.add(data);
    }

    public void store() {
        while (!storageQueue.isEmpty())
            storeNext();
    }

    private void storeNext() {
        T data = storageQueue.poll();
        storageAccessor.push(data);
    }

    void setStorageAccessor(Accessor<T> accessor) {
        if (accessor != null)
            this.storageAccessor = accessor;
        else
            throw new IllegalArgumentException("Accessor couldn't be null.");
    }
}
