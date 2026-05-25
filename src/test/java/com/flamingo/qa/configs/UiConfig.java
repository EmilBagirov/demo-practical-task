package com.flamingo.qa.configs;

public final class UiConfig {

    public static final String  PRACTICE_FORM_URL = ConfigLoader.get("ui.practice.form.url");
    public static final String  WEB_TABLES_URL    = ConfigLoader.get("ui.web.tables.url");

    public static final boolean HEADLESS       = Boolean.parseBoolean(ConfigLoader.get("ui.headless"));
    public static final String  BROWSER        = ConfigLoader.get("ui.browser");
    public static final int     VIEWPORT_WIDTH = Integer.parseInt(ConfigLoader.get("ui.viewport.width"));
    public static final int     VIEWPORT_HEIGHT= Integer.parseInt(ConfigLoader.get("ui.viewport.height"));

    private UiConfig() {}
}
