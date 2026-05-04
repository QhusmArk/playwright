package com.example.helpers.testData;

public final class TestContextHolder {

    private static final ThreadLocal<Context> CONTEXT = ThreadLocal.withInitial(Context::new);

    private TestContextHolder() {
    }

    /** Returns the context for the current scenario thread. */
    public static Context getContext() {
        return context().get();
    }

    /** Clears the context after the scenario to avoid leaking data between tests. */
    public static void clear() {
        context().remove();
    }
}