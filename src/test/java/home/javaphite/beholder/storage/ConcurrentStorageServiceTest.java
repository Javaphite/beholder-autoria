package home.javaphite.beholder.storage;

import home.javaphite.beholder.storage.accessors.Accessor;
import home.javaphite.beholder.test.utils.log.LoggedTestCase;
import home.javaphite.beholder.test.utils.scenario.TernaryFunction;
import home.javaphite.beholder.test.utils.scenario.TestScenario;
import home.javaphite.beholder.test.utils.scenario.UnaryFunction;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

class ConcurrentStorageServiceTest extends LoggedTestCase {
    @RepeatedTest(100)
    void queueThreadSafetyCheck() {
        Set<Integer> firstData = new HashSet<>();
        Set<Integer> secondData = new HashSet<>();
        firstData.add(100);
        firstData.add(150);
        secondData.add(200);
        secondData.add(250);
        Queue<Integer> expectedQueue = new ConcurrentLinkedQueue<>();
        expectedQueue.addAll(firstData);
        expectedQueue.addAll(secondData);

        ConcurrentStorageService<Integer> service = new ConcurrentStorageService<>();
        ForkJoinPool tasks = new ForkJoinPool();
        ForkJoinTask queueFirstData = tasks.submit(getDataQueuer(service, firstData));
        ForkJoinTask queueSecondData = tasks.submit(getDataQueuer(service, secondData));

        TernaryFunction<ConcurrentStorageService<Integer>, ForkJoinTask, ForkJoinTask, Boolean> action =
                (s,t1,t2) -> { t1.join(); t2.join(); return checkEffectivelyEqual(expectedQueue, s.storageQueue);};

        TestScenario scenario = new TestScenario();
        scenario.given("ConcurrentStorageService {@}", service)
                .given("AND task #1, queuing data: {}", queueFirstData, firstData)
                .given("AND task #2, queuing data: {}", queueSecondData, secondData)
                .when("Threads queue data to storage service concurrently", action)
                .then("Service's queue must contain non-corrupted data from both threads", true)
                .perform();

        countAsPassed();
    }

    @Test
    void store_SendDataForwardUsingItsAccessor() {
        ConcurrentStorageService<Integer> service = new ConcurrentStorageService<>();
        Set<Integer> storage = new HashSet<>();
        Set<Integer> expectedStorageState = new HashSet<>();
        expectedStorageState.add(100);
        expectedStorageState.add(200);
        Accessor<Integer> accessor = storage::add;

        service.setStorageAccessor(accessor);
        service.queue(100);
        service.queue(200);

        UnaryFunction<StorageService<Integer>, Set<Integer>> action = serv -> { serv.store(); return storage;};

        TestScenario scenario = new TestScenario();
        scenario.given("StorageService {@} associated with some storage", service)
                .when("Try to store data {} queued in service", action, service.storageQueue)
                .then("Storage must contain all data from queue: {@}", expectedStorageState)
                .perform();

        countAsPassed();
    }

    private <T> Runnable getDataQueuer(StorageService<T> service, Set<T> data) {
        return () -> { for (T dataLine : data) service.queue(dataLine);};
    }

    private <T> boolean checkEffectivelyEqual(Queue<T> q1, Queue<T> q2) {
        logger.debug("First queue: {}, second queue: {}", q1, q2);

        boolean result = false;
        if (q1==null || q2==null) return false;
        if (q1.size() != q2.size()) return false;
        result = q2.containsAll(q1);

        return result;
    }
}
