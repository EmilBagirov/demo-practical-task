package com.flamingo.qa.pages;

import com.flamingo.qa.configs.UiConfig;
import com.flamingo.qa.models.ui.PracticeFormRecord;
import com.flamingo.qa.testdata.PracticeFormData;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class PracticeFormPage {

    private final Page page;

    public PracticeFormPage(Page page) {
        this.page = page;
    }

    @Step("Navigate to Practice Form")
    public PracticeFormPage navigate() {
        page.navigate(UiConfig.PRACTICE_FORM_URL);
        return this;
    }

    @Step("Fill first name: {value}")
    public PracticeFormPage fillFirstName(String value) {
        page.fill("#firstName", value);
        return this;
    }

    @Step("Fill last name: {value}")
    public PracticeFormPage fillLastName(String value) {
        page.fill("#lastName", value);
        return this;
    }

    @Step("Fill email: {value}")
    public PracticeFormPage fillEmail(String value) {
        page.fill("#userEmail", value);
        return this;
    }

    @Step("Select gender: {gender}")
    public PracticeFormPage selectGender(String gender) {
        page.locator("input[name='gender'][value='" + gender + "'] ~ label").click();
        return this;
    }

    @Step("Fill mobile: {value}")
    public PracticeFormPage fillMobile(String value) {
        page.fill("#userNumber", value);
        return this;
    }

    @Step("Set date of birth: {date}")
    public PracticeFormPage setDateOfBirth(String date) {
        Locator input = page.locator("#dateOfBirthInput");
        input.click();
        input.fill(date);
        page.keyboard().press("Enter");
        return this;
    }

    @Step("Add subject: {subject}")
    public PracticeFormPage addSubject(String subject) {
        page.locator("#subjectsInput").fill(subject);
        page.locator(".subjects-auto-complete__option").first().click();
        return this;
    }

    @Step("Select hobby: {hobby}")
    public PracticeFormPage selectHobby(String hobby) {
        page.locator("label.form-check-label:has-text('" + hobby + "')").click();
        return this;
    }

    @Step("Fill current address: {value}")
    public PracticeFormPage fillCurrentAddress(String value) {
        page.fill("#currentAddress", value);
        return this;
    }

    @Step("Upload picture: {filePath}")
    public PracticeFormPage uploadPicture(Path filePath) {
        page.locator("#uploadPicture").setInputFiles(filePath);
        return this;
    }

    @Step("Select state: {state}")
    public PracticeFormPage selectState(String state) {
        page.locator("#state").click();
        page.locator("#state input").fill(state);
        page.locator("div[class*='-option']:has-text('" + state + "')").first().click();
        return this;
    }

    @Step("Select city: {city}")
    public PracticeFormPage selectCity(String city) {
        page.locator("#city").click();
        page.locator("div[class*='-option']:has-text('" + city + "')").first().click();
        return this;
    }

    @Step("Submit form")
    public PracticeFormPage submit() {
        // Scroll the button into view — the page has ads that can overlay the bottom area
        page.locator("#submit").scrollIntoViewIfNeeded();
        page.locator("#submit").click();
        return this;
    }

    @Step("Fills name and gender with random data; caller provides the email value")
    public static PracticeFormPage fillRequiredExceptEmail(PracticeFormPage fp) {
        PracticeFormRecord base = PracticeFormData.randomRequired();
        return fp.fillFirstName(base.getFirstName())
                .fillLastName(base.getLastName())
                .selectGender(base.getGender())
                .fillMobile(base.getMobile());
    }

    @Step("Fills name and gender with random data; caller provides the mobile value")
    public static PracticeFormPage fillRequiredExceptMobile(PracticeFormPage fp) {
        PracticeFormRecord base = PracticeFormData.randomRequired();
        return fp.fillFirstName(base.getFirstName())
                .fillLastName(base.getLastName())
                .selectGender(base.getGender());
    }

    public ConfirmationModal getConfirmationModal() {
        return new ConfirmationModal(page);
    }

    @Step("Check whether field '{fieldId}' has validation error highlighting")
    public boolean isFieldInvalid(String fieldId) {
        String borderColor = (String) page.locator("#" + fieldId)
                .evaluate("el => window.getComputedStyle(el).borderColor");
        return "rgb(220, 53, 69)".equals(borderColor);
    }

    @Step("Assert fields are invalid: {fieldIds}")
    public void assertFieldsInvalid(String... fieldIds) {
        for (String fieldId : fieldIds) {
            await()
                    .pollInterval(1, TimeUnit.SECONDS)
                    .atMost(5, TimeUnit.SECONDS)
                    .ignoreExceptions()
                    .pollInSameThread()
                    .untilAsserted(() ->
                            assertThat(isFieldInvalid(fieldId))
                                    .as("Field '%s' should be highlighted as invalid", fieldId)
                                    .isTrue());
        }
    }

    @Step("Check whether is City dropdown disabled")
    public boolean isCityDisabled() {
        return "true".equals(page.locator("#city [class*='-control']").getAttribute("aria-disabled"));
    }

    @Step("Get available city options for selected state")
    public List<String> getCityOptions() {
        page.locator("#city").click();
        List<String> options = page.locator("div[class*='-option']").allInnerTexts();
        page.keyboard().press("Escape");
        return options;
    }
}
