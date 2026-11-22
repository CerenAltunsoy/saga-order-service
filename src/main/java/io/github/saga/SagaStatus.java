package io.github.saga;

public enum SagaStatus {
    /** every step executed */
    COMPLETED,
    /** a step failed and all previously executed steps were compensated */
    COMPENSATED,
    /** a step failed and at least one compensation could not be applied: needs manual intervention */
    COMPENSATION_FAILED
}
