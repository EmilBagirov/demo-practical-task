package com.flamingo.qa.extensions;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class TestLoggingExtension implements BeforeEachCallback, AfterEachCallback {

    private static final String LINE = "─".repeat(72);
    private static final AtomicBoolean JUL_CONFIGURED = new AtomicBoolean(false);

    @Override
    public void beforeEach(ExtensionContext context) {
        routeJulToStdout();
        System.out.println("\n" + LINE);
        System.out.println("▶  " + testLabel(context));
        System.out.println(LINE);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        boolean passed = context.getExecutionException().isEmpty();
        System.out.println(LINE);
        System.out.println((passed ? "✔" : "✘") + "  " + testLabel(context)
                + " — " + (passed ? "PASSED" : "FAILED"));
        System.out.println(LINE + "\n");
    }

    private static String testLabel(ExtensionContext context) {
        String className = context.getTestClass().map(Class::getSimpleName).orElse("?");
        return className + " :: " + context.getDisplayName();
    }

    /**
     * Replaces JUL's default ConsoleHandler (writes to stderr) with a handler
     * that writes directly to System.out — the same stream REST Assured logging
     * filters use. This ensures all log lines appear in production order.
     *
     * Uses a plain Handler subclass instead of ConsoleHandler/StreamHandler to
     * avoid StreamHandler.setOutputStream closing the underlying stream.
     */
    private static void routeJulToStdout() {
        if (!JUL_CONFIGURED.compareAndSet(false, true)) {
            return;
        }
        Logger root = Logger.getLogger("");
        for (java.util.logging.Handler h : root.getHandlers()) {
            root.removeHandler(h);
        }

        Formatter compactFormatter = new SimpleFormatter() {
            @Override
            public String format(LogRecord logRecord) {
                return String.format("[%s] %s%n", logRecord.getLevel().getName(), logRecord.getMessage());
            }
        };

        Handler stdoutHandler = new Handler() {
            { setFormatter(compactFormatter); setLevel(Level.ALL); }

            @Override
            public void publish(LogRecord record) {
                if (isLoggable(record)) {
                    System.out.print(getFormatter().format(record));
                    System.out.flush();
                }
            }

            @Override public void flush() { System.out.flush(); }
            // Flush but never close: System.out is owned by the JVM, not by this handler.
            // Closing it would permanently break all subsequent stdout output in the process.
            @Override public void close() { flush(); }
        };

        root.addHandler(stdoutHandler);
        root.setLevel(Level.INFO);
    }
}
