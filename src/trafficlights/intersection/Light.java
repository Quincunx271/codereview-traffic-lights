package trafficlights.intersection;

import com.google.common.collect.ImmutableMap;

import java.util.EnumMap;
import java.util.OptionalDouble;

import static com.google.common.base.Preconditions.checkState;

public class Light {
    public enum Color {
        RED, GREEN, YELLOW
    }

    private enum State {
        GREEN, YELLOW, WAITING, OFF;

        Color getColor() {
            switch (this) {
            case GREEN:
                return Color.GREEN;
            case YELLOW:
                return Color.YELLOW;
            case WAITING:
            case OFF:
                return Color.RED;
            }

            throw new InternalError();
        }
    }

    private State state = State.OFF;

    private final EnumMap<State, Double> durations;
    private double elapsedTime;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") private OptionalDouble goal;
    private boolean requestOff;

    public Light(double greenDuration, double yellowDuration, double redPause) {
        durations = new EnumMap<>(ImmutableMap.<State, Double>builder()
                .put(State.GREEN, greenDuration)
                .put(State.YELLOW, yellowDuration)
                .put(State.WAITING, redPause)
                .build());
        this.elapsedTime = 0;
        this.goal = OptionalDouble.empty();
        this.requestOff = false;
    }

    public Color getColor() {
        return state.getColor();
    }

    private OptionalDouble getDurationIfPresent(State state) {
        if (durations.containsKey(state)) return OptionalDouble.of(durations.get(state));
        return OptionalDouble.empty();
    }

    public void turnOff() {
        requestOff = true;
    }

    public void tick(double duration) {
        checkState(isActive());

        performActions(duration);
        updateState(duration);
    }

    public void activate() {
        state = State.GREEN;
        goal = OptionalDouble.of(durations.get(state));
        requestOff = false;
        this.elapsedTime = 0;
    }

    public boolean isActive() {
        return state != State.OFF;
    }

    private void performActions(double duration) {
        switch (state) {
        case GREEN:
        case YELLOW:
        case WAITING:
            elapsedTime += duration;
            break;
        case OFF:
            elapsedTime = 0;
            break;
        }
    }

    private void updateState(double duration) {
        switch (state) {
        case GREEN:
            if (requestOff) {
                requestOff = false;
                incrementState();
                elapsedTime = 0;
            }

            //noinspection ConstantConditions
            while (goal.isPresent() && elapsedTime >= goal.getAsDouble()) {
                incrementState();
            }
            break;
        case YELLOW:
        case WAITING:
            //noinspection ConstantConditions
            while (goal.isPresent() && elapsedTime >= goal.getAsDouble()) {
                incrementState();
            }
            break;
        case OFF:
            break;
        }
    }

    private void incrementState() {
        //noinspection ConstantConditions
        elapsedTime -= goal.getAsDouble();
        state = modularIncrementEnum(state.ordinal(), State.values());
        goal = getDurationIfPresent(state);
    }

    private static <T> T modularIncrementEnum(int index, T[] values) {
        return values[(index + 1) % values.length];
    }
}
