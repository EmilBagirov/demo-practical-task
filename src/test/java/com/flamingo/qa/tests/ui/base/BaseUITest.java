package com.flamingo.qa.tests.ui.base;

import com.flamingo.qa.configs.UiConfig;
import com.flamingo.qa.extensions.ScreenshotOnFailureExtension;
import com.flamingo.qa.extensions.TestLoggingExtension;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URISyntaxException;
import java.nio.file.Path;

@Tag("ui")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({TestLoggingExtension.class, ScreenshotOnFailureExtension.class})
public abstract class BaseUITest {

    // Instance fields — each concrete test class (PracticeFormTest, WebTablesTest, …)
    // gets its own Playwright + Browser, so concurrent class execution is safe.
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    // public so ScreenshotOnFailureExtension can access it via test instance
    public Page page;

    @BeforeAll
    void launchBrowser() {
        playwright = Playwright.create();
        browser = selectBrowser(playwright).launch(
                new BrowserType.LaunchOptions().setHeadless(UiConfig.HEADLESS));
    }

    @AfterAll
    void closeBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(UiConfig.VIEWPORT_WIDTH, UiConfig.VIEWPORT_HEIGHT));
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        if (context != null) context.close();
    }

    /** Resolves a file from {@code src/test/resources/files/} by name only. */
    protected Path getFixture(String filename) throws URISyntaxException {
        return Path.of(getClass().getClassLoader().getResource("files/" + filename).toURI());
    }

    private BrowserType selectBrowser(Playwright pw) {
        String b = UiConfig.BROWSER.toLowerCase();
        if ("firefox".equals(b)) return pw.firefox();
        if ("webkit".equals(b))  return pw.webkit();
        return pw.chromium();
    }
}
