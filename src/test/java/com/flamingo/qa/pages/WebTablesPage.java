package com.flamingo.qa.pages;

import com.flamingo.qa.configs.UiConfig;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

import java.util.List;

import static com.flamingo.qa.utils.WaitUtils.waitFor;

public class WebTablesPage {

    private static final String ROW_GROUP = "tbody tr";
    private static final String CELL      = "td";

    private final Page page;

    public WebTablesPage(Page page) {
        this.page = page;
    }

    @Step("Navigate to Web Tables")
    public WebTablesPage navigate() {
        page.navigate(UiConfig.WEB_TABLES_URL);
        return this;
    }

    @Step("Search for: {text}")
    public WebTablesPage search(String text) {
        page.fill("#searchBox", text);
        return this;
    }

    @Step("Clear search")
    public WebTablesPage clearSearch() {
        page.fill("#searchBox", "");
        return this;
    }

    @Step("Get visible data row count")
    public int getDataRowCount() {
        return waitFor(() -> (int) page.locator(ROW_GROUP)
                .all().stream()
                .filter(row -> !row.locator(CELL).first().innerText().trim().isEmpty())
                .count());
    }

    @Step("Click Add button")
    public RegistrationFormModal clickAdd() {
        page.locator("#addNewRecordButton").click();
        return new RegistrationFormModal(page);
    }

    @Step("Click Edit for row with email: {email}")
    public RegistrationFormModal clickEditForRowWithEmail(String email) {
        rowByEmail(email).locator("span[title='Edit']").click();
        return new RegistrationFormModal(page);
    }

    @Step("Click Delete for row with email: {email}")
    public void clickDeleteForRowWithEmail(String email) {
        rowByEmail(email).locator("span[title='Delete']").click();
    }

    @Step("Check row is present with email: {email}")
    public boolean isRowPresentWithEmail(String email) {
        return waitFor(() -> page.locator(ROW_GROUP + ":has-text('" + email + "')").count() > 0);
    }

    @Step("Get row data for email: {email}")
    public List<String> getRowDataByEmail(String email) {
        return waitFor(() -> {
            List<Locator> cells = rowByEmail(email).locator(CELL).all();
            return cells.subList(0, 6).stream()
                    .map(cell -> cell.innerText().trim())
                    .collect(java.util.stream.Collectors.toList());
        });
    }

    @Step("Click column header to sort: {columnName}")
    public WebTablesPage clickColumnHeader(String columnName) {
        page.locator(".rt-th:has-text('" + columnName + "')").click();
        return this;
    }

    @Step("Get all visible values for column index {columnIndex}")
    public List<String> getColumnValues(int columnIndex) {
        return waitFor(() -> page.locator(ROW_GROUP)
                .filter(new Locator.FilterOptions().setHasNotText(""))
                .all().stream()
                .filter(row -> !row.locator(CELL).first().innerText().trim().isEmpty())
                .map(row -> row.locator(CELL).nth(columnIndex).innerText().trim())
                .collect(java.util.stream.Collectors.toList()));
    }

    private Locator rowByEmail(String email) {
        return page.locator(ROW_GROUP).filter(new Locator.FilterOptions().setHasText(email)).first();
    }

}
