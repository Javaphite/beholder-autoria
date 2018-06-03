package home.javaphite.beholder;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

//UNDER CONSTRUCTION
class AccessorService<T> {
    Queue<T> queuedData =new ConcurrentLinkedDeque<>();

    public void queue(T data){}
}
