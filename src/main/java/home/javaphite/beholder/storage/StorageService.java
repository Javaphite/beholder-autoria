package home.javaphite.beholder.storage;

import home.javaphite.beholder.storage.accessors.Accessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Service intended to store prepared data in remote storage.
 */
public class StorageService {
   private static final Logger LOG = LoggerFactory.getLogger(StorageService.class);
   private Queue<Map<String, Object>> storageQueue = new ConcurrentLinkedQueue<>();
   private Accessor<Map<String, Object>> accessor;

   public StorageService(Accessor<Map<String, Object>> accessor) {
      this.accessor = accessor;
   }

    /**
     * Queues atomic portion of data to be stored in future.
     * @param data atomic portion of data.
     */
   public void queue(Map<String, Object> data) {
       LOG.debug("Adding data to queue {}", data);
       storageQueue.add(data);
    }

    /**
     * Stores all currently queued data in remote storage.
     */
    public void store() {
       LOG.info("Storing data to {}...", accessor);
       storageQueue.forEach(accessor::push);
       LOG.info("Storing data to {} completed!", accessor);
   }
}
