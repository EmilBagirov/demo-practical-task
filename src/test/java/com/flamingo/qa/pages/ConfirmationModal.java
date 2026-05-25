package com.flamingo.qa.pages;

import com.flamingo.qa.models.ui.PracticeFormRecord;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfirmationModal {

    private final Page page;

    public ConfirmationModal(Page page) {
        this.page = page;
    }

    @Step("Check confirmation modal is visible")
    public boolean isVisible() {
        return page.locator(".modal-content").isVisible();
    }

    @Step("Get confirmation modal title")
    public String getTitle() {
        return page.locator("#example-modal-sizes-title-lg").innerText();
    }

    @Step("Get submitted data from confirmation modal")
    public Map<String, String> getSubmittedData() {
        Map<String, String> data = new HashMap<>();
        List<Locator> rows = page.locator(".table-responsive table tbody tr").all();
        for (Locator row : rows) {
            List<Locator> cells = row.locator("td").all();
            if (cells.size() == 2) {
                data.put(cells.get(0).innerText().trim(), cells.get(1).innerText().trim());
            }
        }
        return data;
    }

    /**
     * Universal assertion for a successful form submission.
     * Verifies modal visibility, title, and all non-null fields in the record,
     * so it works for both partial (required-only) and full form submissions.
     */
    @Step("Assert confirmation modal is shown with correct submitted data")
    public void assertData(PracticeFormRecord expected) {
        assertThat(isVisible())
                .as("Confirmation modal must be visible")
                .isTrue();
        assertThat(getTitle())
                .as("Modal title")
                .isEqualTo("Thanks for submitting the form");

        Map<String, String> submitted = getSubmittedData();

        assertThat(submitted.get("Student Name"))
                .as("Student Name")
                .isEqualTo(expected.getFirstName() + " " + expected.getLastName());
        assertThat(submitted.get("Gender"))
                .as("Gender")
                .isEqualTo(expected.getGender());
        assertThat(submitted.get("Mobile"))
                .as("Mobile")
                .isEqualTo(expected.getMobile());

        if (expected.getEmail() != null) {
            assertThat(submitted.get("Student Email")).as("Student Email").isEqualTo(expected.getEmail());
        }
        if (expected.getSubject() != null) {
            assertThat(submitted.get("Subjects")).as("Subjects").contains(expected.getSubject());
        }
        if (expected.getHobby() != null) {
            assertThat(submitted.get("Hobbies")).as("Hobbies").contains(expected.getHobby());
        }
        if (expected.getCurrentAddress() != null) {
            assertThat(submitted.get("Address")).as("Current Address").isEqualTo(expected.getCurrentAddress());
        }
        if (expected.getState() != null) {
            assertThat(submitted.get("State and City"))
                    .as("State and City")
                    .contains(expected.getState())
                    .contains(expected.getCity());
        }
        if (expected.getPicture() != null) {
            assertThat(submitted.get("Picture")).as("Picture").isEqualTo(expected.getPicture());
        }
    }

    @Step("Close confirmation modal")
    public void close() {
        page.locator("#closeLargeModal").click();
    }
}
