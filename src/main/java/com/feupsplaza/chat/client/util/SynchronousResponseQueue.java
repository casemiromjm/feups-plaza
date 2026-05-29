package com.feupsplaza.chat.client.util;

import com.feupsplaza.chat.shared.protocol.Operation;
import com.feupsplaza.chat.shared.protocol.Response;
import com.feupsplaza.chat.shared.protocol.Status;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronousResponseQueue {
    private final Queue<Response> queue = new LinkedList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();

    /**
     * Called by the Background Listener Thread when there is a response (Producer)
     * @param response
     */
    public void put(Response response) {
        lock.lock();
        try {
            queue.add(response);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Called by ServerConnection.send() to wait for an answer (Consumer)
     * @return
     */
    public Response take() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                try {
                    notEmpty.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return new Response(Operation.ERROR, Status.ERROR, List.of("Interrupted while waiting for response"));
                }
            }
            return queue.poll();

        } finally {
            lock.unlock();
        }
    }

}
