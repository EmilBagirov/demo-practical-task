package com.flamingo.qa.extensions;

import com.flamingo.qa.tests.ui.base.BaseUITest;
import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.ByteArrayInputStream;

// AfterTestExecutionCallback fires before @AfterEach, so the page is still open.
// TestWatcher.testFailed() fires after @AfterEach — too late, context is already closed.
public class ScreenshotOnFailureExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (!context.getExecutionException().isPresent()) {
            return;
        }
        context.getTestInstance()
                .filter(instance -> instance instanceof BaseUITest)
                .map(instance -> (BaseUITest) instance)
                .ifPresent(test -> {
                    if (test.page != null && !test.page.isClosed()) {
                        byte[] screenshot = test.page.screenshot(
                                new Page.ScreenshotOptions().setFullPage(true));
                        Allure.addAttachment("Screenshot on failure", "image/png",
                                new ByteArrayInputStream(screenshot), "png");
                    }
                });
    }
}
