package io.github.saga;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Orchestration-based saga. Runs steps in order; if one fails, compensates the
 * already executed steps in reverse order. Each compensation is retried up to
 * {@code maxCompensationRetries} extra times; if it still fails the saga keeps
 * compensating the remaining steps (best effort) and ends in COMPENSATION_FAILED.
 * <p>
 * Runs are idempotent per saga id: re-running a finished saga returns the stored result.
 */
public class SagaOrchestrator<C> {

    private final List<SagaStep<C>> steps;
    private final int maxCompensationRetries;
    private final Map<String, SagaResult> results = new ConcurrentHashMap<>();

    public SagaOrchestrator(List<SagaStep<C>> steps, int maxCompensationRetries) {
        if (maxCompensationRetries < 0) throw new IllegalArgumentException("maxCompensationRetries must be >= 0");
        this.steps = List.copyOf(steps);
        this.maxCompensationRetries = maxCompensationRetries;
    }

    public SagaResult run(String sagaId, C context) {
        // TODO
        throw new UnsupportedOperationException("not implemented yet");
    }

}
