package io.github.saga;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SagaOrchestratorTest {

    private final List<String> calls = new ArrayList<>();

    private SagaOrchestrator<List<String>> saga(int retries, RecordingStep... steps) {
        return new SagaOrchestrator<>(List.of(steps), retries);
    }

    @Test
    void allStepsSucceed() {
        SagaResult r = saga(0, new RecordingStep("reserve"), new RecordingStep("charge"), new RecordingStep("ship"))
                .run("order-1", calls);
        assertThat(r.status()).isEqualTo(SagaStatus.COMPLETED);
        assertThat(r.failedStep()).isNull();
        assertThat(calls).containsExactly("do reserve", "do charge", "do ship");
        assertThat(r.log()).containsExactly("execute:reserve", "execute:charge", "execute:ship");
    }

    @Test
    void failureCompensatesPreviousStepsInReverseOrder() {
        SagaResult r = saga(0, new RecordingStep("reserve"), new RecordingStep("charge"),
                new RecordingStep("ship").failOnExecute()).run("order-1", calls);

        assertThat(r.status()).isEqualTo(SagaStatus.COMPENSATED);
        assertThat(r.failedStep()).isEqualTo("ship");
        assertThat(calls).containsExactly("do reserve", "do charge", "do ship", "undo charge", "undo reserve");
        assertThat(r.log()).containsExactly(
                "execute:reserve", "execute:charge", "failed:ship", "compensate:charge", "compensate:reserve");
    }

    @Test
    void failedStepItselfIsNotCompensated() {
        saga(0, new RecordingStep("reserve").failOnExecute(), new RecordingStep("charge")).run("o", calls);
        assertThat(calls).containsExactly("do reserve");
    }

    @Test
    void transientCompensationFailureIsRetried() {
        SagaResult r = saga(2, new RecordingStep("reserve").failCompensation(2),
                new RecordingStep("charge").failOnExecute()).run("o", calls);
        assertThat(r.status()).isEqualTo(SagaStatus.COMPENSATED);
        assertThat(calls).containsExactly("do reserve", "do charge", "undo reserve", "undo reserve", "undo reserve");
    }

    @Test
    void permanentCompensationFailureStillCompensatesTheRest() {
        SagaResult r = saga(1,
                new RecordingStep("reserve"),
                new RecordingStep("charge").failCompensation(Integer.MAX_VALUE),
                new RecordingStep("ship").failOnExecute()).run("o", calls);

        assertThat(r.status()).isEqualTo(SagaStatus.COMPENSATION_FAILED);
        assertThat(calls).containsExactly("do reserve", "do charge", "do ship",
                "undo charge", "undo charge", "undo reserve");
        assertThat(r.log()).contains("compensation-gave-up:charge", "compensate:reserve");
    }

    @Test
    void rerunningSameSagaIdDoesNotExecuteAgain() {
        SagaOrchestrator<List<String>> s = saga(0, new RecordingStep("reserve"));
        SagaResult first = s.run("order-1", calls);
        SagaResult second = s.run("order-1", calls);
        assertThat(second).isEqualTo(first);
        assertThat(calls).containsExactly("do reserve");
    }

    @Test
    void differentSagaIdsRunIndependently() {
        SagaOrchestrator<List<String>> s = saga(0, new RecordingStep("reserve"));
        s.run("order-1", calls);
        s.run("order-2", calls);
        assertThat(calls).containsExactly("do reserve", "do reserve");
    }
}
