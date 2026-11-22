package io.github.saga;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Transactional outbox: business state changes and the events describing them are
 * written in the <em>same</em> local transaction, so an event is never lost and never
 * published for a change that was rolled back. A relay later publishes pending events.
 * <p>
 * The "database" here is an in-memory map guarded by a lock, standing in for a table.
 */
public class TransactionalOutbox {

    private final Map<String, String> state = new LinkedHashMap<>();   // the business table
    private final List<OutboxEvent> pending = new ArrayList<>();       // the outbox table
    private long nextId = 1;

    /**
     * Atomically sets {@code key=value} and records an event. If {@code validate} throws,
     * neither the state change nor the event is stored.
     */
    public synchronized OutboxEvent writeWithEvent(String key, String value, String eventType, Runnable validate) {
        // TODO
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Publishes pending events in order. Stops at the first failure so ordering is kept;
     * the failed event and the ones after it stay pending for the next run.
     *
     * @return number of events published
     */
    public synchronized int relay(MessagePublisher publisher) {
        // TODO
        throw new UnsupportedOperationException("not implemented yet");
    }

    public synchronized List<OutboxEvent> pendingEvents() {
        // TODO
        throw new UnsupportedOperationException("not implemented yet");
    }

    public synchronized String read(String key) {
        // TODO
        throw new UnsupportedOperationException("not implemented yet");
    }
}
