package com.flamingo.qa.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigLoader {

    private static final Properties PROPS = load();

    private ConfigLoader() {}

    /**
     * Returns the value for {@code key}.
     * System properties take priority over project.properties (enables -D overrides in CI).
     *
     * @throws IllegalStateException if the key is absent from both sources
     */
    public static String get(String key) {
        String value = resolve(key);
        if (value == null) {
            throw new IllegalStateException("Missing config key '" + key + "' in project.properties");
        }
        return value;
    }

    /** Returns the value for {@code key}, or {@code defaultValue} if absent from both sources. */
    public static String get(String key, String defaultValue) {
        String value = resolve(key);
        return value != null ? value : defaultValue;
    }

    private static String resolve(String key) {
        String sysProp = System.getProperty(key);
        return sysProp != null ? sysProp : PROPS.getProperty(key);
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream is = ConfigLoader.class.getClassLoader()
                .getResourceAsStream("project.properties")) {
            if (is == null) {
                throw new IllegalStateException("project.properties not found on classpath");
            }
            props.load(is);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load project.properties", e);
        }
        return props;
    }
}
