package io.github.saga;

public record OutboxEvent(long id, String aggregateId, String type, String payload) {
}
