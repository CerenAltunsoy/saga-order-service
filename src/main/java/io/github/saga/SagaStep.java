package io.github.saga;

/**
 * One local transaction in a saga, plus the action that semantically undoes it.
 * Compensations must be idempotent: they may be retried.
 */
public interface SagaStep<C> {

    String name();

    void execute(C context);

    void compensate(C context);
}
