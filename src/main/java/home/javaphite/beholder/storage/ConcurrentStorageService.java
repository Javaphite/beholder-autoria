package home.javaphite.beholder.storage;

import home.javaphite.beholder.storage.accessors.Accessor;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

// UNDER CONSTRUCTION
public class ConcurrentStorageService<T> implements StorageService<T> {
   final Queue<T> storageQueue = new ConcurrentLinkedQueue<>();
   private Accessor<T> storageAccessor;

   @Override
   public void queue(T data) {
       storageQueue.add(data);
    }

    @Override
    public void store() {
        while (!storageQueue.isEmpty())
            storeNext();
    }

    private void storeNext() {
        T data = storageQueue.poll();
        storageAccessor.push(data);
    }

    public void setStorageAccessor(Accessor<T> accessor) {
        if (null != accessor) {
            this.storageAccessor = accessor;
        }
        else {
            throw new IllegalArgumentException("Accessor couldn't be null.");
        }
    }
}
