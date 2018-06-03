package home.javaphite.beholder;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

class AccessorService<T> {
    Queue<T> queuedData =new ConcurrentLinkedDeque<>();

    public void queue(T data){}
}
