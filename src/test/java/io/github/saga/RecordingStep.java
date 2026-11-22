package io.github.saga;

import java.util.List;

/** Test step that records calls into a shared list and can be told to fail. */
final class RecordingStep implements SagaStep<List<String>> {

    private final String name;
    private boolean failOnExecute;
    private int compensationFailuresLeft;

    RecordingStep(String name) {
        this.name = name;
    }

    RecordingStep failOnExecute() {
        this.failOnExecute = true;
        return this;
    }

    RecordingStep failCompensation(int times) {
        this.compensationFailuresLeft = times;
        return this;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void execute(List<String> calls) {
        calls.add("do " + name);
        if (failOnExecute) throw new IllegalStateException(name + " failed");
    }

    @Override
    public void compensate(List<String> calls) {
        calls.add("undo " + name);
        if (compensationFailuresLeft > 0) {
            compensationFailuresLeft--;
            throw new IllegalStateException("undo " + name + " failed");
        }
    }
}
