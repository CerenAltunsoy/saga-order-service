# saga-order-service

Distributed transactions without 2PC: an order workflow (reserve stock → charge payment →
schedule shipping) implemented with the saga pattern and a transactional outbox.

## Components

- `SagaOrchestrator`: runs steps in order; on failure runs compensations in reverse order,
  retrying each; idempotent per saga id.
- `TransactionalOutbox`: business state and outgoing events are written atomically; a relay
  publishes pending events in order with at-least-once semantics.

## Usage

```java
var saga = new SagaOrchestrator<OrderContext>(
        List.of(reserveStock, chargePayment, scheduleShipping), 3);

SagaResult result = saga.run(order.id(), new OrderContext(order));
```

## Build

```bash
mvn verify
```

## Roadmap

- [ ] Orchestrator with compensations, transactional outbox
- [ ] Split into order / inventory / payment Spring Boot services
- [ ] Kafka for commands and events, outbox relay with Debezium or polling
- [ ] Persist saga state so a crashed orchestrator can resume
- [ ] Choreography variant and comparison

## Design decisions

_TODO_
