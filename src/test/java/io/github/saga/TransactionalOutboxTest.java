package io.github.saga;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionalOutboxTest {

    private final TransactionalOutbox outbox = new TransactionalOutbox();

    @Test
    void writeStoresStateAndEventTogether() {
        outbox.writeWithEvent("order-1", "CREATED", "OrderCreated", () -> { });
        assertThat(outbox.read("order-1")).isEqualTo("CREATED");
        assertThat(outbox.pendingEvents()).extracting(OutboxEvent::type).containsExactly("OrderCreated");
    }

    @Test
    void failedTransactionStoresNeitherStateNorEvent() {
        assertThatThrownBy(() -> outbox.writeWithEvent("order-1", "CREATED", "OrderCreated",
                () -> { throw new IllegalArgumentException("invalid order"); }))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(outbox.read("order-1")).isNull();
        assertThat(outbox.pendingEvents()).isEmpty();
    }

    @Test
    void relayPublishesInOrderAndClearsOutbox() {
        outbox.writeWithEvent("o1", "A", "E1", () -> { });
        outbox.writeWithEvent("o2", "B", "E2", () -> { });
        List<String> published = new ArrayList<>();

        assertThat(outbox.relay(e -> published.add(e.type()))).isEqualTo(2);
        assertThat(published).containsExactly("E1", "E2");
        assertThat(outbox.pendingEvents()).isEmpty();
    }

    @Test
    void publishFailureKeepsEventAndPreservesOrder() {
        outbox.writeWithEvent("o1", "A", "E1", () -> { });
        outbox.writeWithEvent("o2", "B", "E2", () -> { });
        outbox.writeWithEvent("o3", "C", "E3", () -> { });
        List<String> published = new ArrayList<>();

        int n = outbox.relay(e -> {
            if (e.type().equals("E2")) throw new RuntimeException("broker down");
            published.add(e.type());
        });

        assertThat(n).isEqualTo(1);
        assertThat(published).containsExactly("E1");
        assertThat(outbox.pendingEvents()).extracting(OutboxEvent::type).containsExactly("E2", "E3");

        outbox.relay(e -> published.add(e.type()));
        assertThat(published).containsExactly("E1", "E2", "E3");
    }

    @Test
    void eventIdsAreIncreasing() {
        OutboxEvent a = outbox.writeWithEvent("o1", "A", "E", () -> { });
        OutboxEvent b = outbox.writeWithEvent("o1", "B", "E", () -> { });
        assertThat(b.id()).isGreaterThan(a.id());
    }
}
