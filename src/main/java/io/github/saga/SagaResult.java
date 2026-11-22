package io.github.saga;

import java.util.List;

/**
 * @param failedStep name of the step whose execute() failed, or {@code null} if completed
 * @param log        ordered events, e.g. "execute:reserve-stock", "failed:charge", "compensate:reserve-stock"
 */
public record SagaResult(String sagaId, SagaStatus status, String failedStep, List<String> log) {
}
