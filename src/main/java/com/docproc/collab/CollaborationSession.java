package com.docproc.collab;

import com.docproc.command.Command;
import com.docproc.core.DocumentManager;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CollaborationSession {
    private final DocumentManager manager;

    public CollaborationSession(DocumentManager manager) {
        this.manager = manager;
    }

    public void simulate(List<EditorAction> actions) {
        ExecutorService executor = Executors.newFixedThreadPool(Math.max(1, actions.size()));
        CountDownLatch latch = new CountDownLatch(actions.size());
        for (EditorAction action : actions) {
            executor.submit(() -> {
                try {
                    manager.execute(action.command());
                    System.out.println("[Collab] " + action.editorName() + " executed " + action.command().name());
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Collaboration interrupted", e);
        } finally {
            executor.shutdown();
        }
    }

    public record EditorAction(String editorName, Command command) {
    }
}
