package com.flamingo.qa.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.awaitility.Awaitility.await;

public final class WaitUtils {

    private WaitUtils() {}

    /**
     * Retries {@code action} every 1 s for up to 5 s, ignoring exceptions on intermediate
     * attempts (e.g. element not yet rendered after an action). {@code pollInSameThread} is
     * required because Playwright's Page is not thread-safe.
     */
    public static <T> T waitFor(Supplier<T> action) {
        AtomicReference<T> result = new AtomicReference<>();
        await()
                .pollInterval(1, TimeUnit.SECONDS)
                .atMost(5, TimeUnit.SECONDS)
                .ignoreExceptions()
                .pollInSameThread()
                .until(() -> {
                    result.set(action.get());
                    return true;
                });
        return result.get();
    }
}
