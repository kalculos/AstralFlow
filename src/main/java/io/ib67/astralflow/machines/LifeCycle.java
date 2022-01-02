package io.ib67.astralflow.machines;

public interface LifeCycle {
    default void onLoad() {
    }

    default void onUnload() {
    }
}
