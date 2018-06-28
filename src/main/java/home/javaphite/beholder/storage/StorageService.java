package home.javaphite.beholder.storage;

import home.javaphite.beholder.storage.accessors.Accessor;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

// UNDER CONSTRUCTION
public class StorageService {
   final Queue<Map<String, Object>> storageQueue = new ConcurrentLinkedQueue<>();
   private Accessor<Map<String, Object>> storageAccessor;

   public void queue(Map<String, Object> data) {
       storageQueue.add(data);
    }

   public void store() {
        while (!storageQueue.isEmpty())
            storeNext();
    }

    private void storeNext() {
        Map<String, Object> data = storageQueue.poll();
        storageAccessor.push(data);
    }

    public void setStorageAccessor(Accessor<Map<String, Object>> accessor) {
        if (null != accessor) {
            this.storageAccessor = accessor;
        }
        else {
            throw new IllegalArgumentException("Accessor couldn't be null.");
        }
    }
}
