package io.github.saga;

/** E.g. a Kafka producer. Throws if the broker does not acknowledge. */
@FunctionalInterface
public interface MessagePublisher {

    void publish(OutboxEvent event);
}
