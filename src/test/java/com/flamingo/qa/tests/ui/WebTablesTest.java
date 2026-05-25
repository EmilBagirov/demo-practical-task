package com.flamingo.qa.tests.ui;

import com.flamingo.qa.models.ui.EmployeeRecord;
import com.flamingo.qa.pages.RegistrationFormModal;
import com.flamingo.qa.pages.WebTablesPage;
import com.flamingo.qa.testdata.EmployeeTestData;
import com.flamingo.qa.tests.ui.base.BaseUITest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("emil")
@Feature("Web Tables")
class WebTablesTest extends BaseUITest {

    // DemoQA pre-populates these 3 rows on every fresh page load
    private static final List<String> DEFAULT_EMAILS = List.of(
            "cierra@example.com",
            "alden@example.com",
            "kierra@example.com"
    );
    private static final int DEFAULT_ROW_COUNT = 3;

    private WebTablesPage tablePage;

    @BeforeEach
    void openPage() {
        tablePage = new WebTablesPage(page);
        tablePage.navigate();
    }

    @Test
    @DisplayName("Web Tables — page loads with default records")
    @Description("Verifies that the three pre-populated rows are present on a fresh page load")
    void shouldDisplayDefaultRecords() {
        DEFAULT_EMAILS.forEach(email ->
                assertThat(tablePage.isRowPresentWithEmail(email))
                        .as("Default row with email '%s' must be present", email)
                        .isTrue());
    }

    @Test
    @DisplayName("Add new record — row appears in the table with correct data")
    @Description("Clicks Add, fills all fields with random data, submits, and verifies the new row is visible")
    void shouldAddNewRecord() {
        EmployeeRecord employee = EmployeeTestData.randomEmployee();

        RegistrationFormModal form = tablePage.clickAdd();
        form.fill(employee).submit();

        assertThat(tablePage.isRowPresentWithEmail(employee.getEmail()))
                .as("Newly added row must be visible in the table")
                .isTrue();

        assertThat(tablePage.getRowDataByEmail(employee.getEmail()))
                .containsExactly(
                        employee.getFirstName(), employee.getLastName(), employee.getAge(),
                        employee.getEmail(), employee.getSalary(), employee.getDepartment());
    }

    @Test
    @DisplayName("Edit record — changes are reflected in the table")
    @Description("Adds a row, edits its first name and salary, and verifies the updated values appear in the table")
    void shouldEditExistingRecord() {
        EmployeeRecord original = EmployeeTestData.randomEmployee();
        tablePage.clickAdd().fill(original).submit();

        RegistrationFormModal editForm = tablePage.clickEditForRowWithEmail(original.getEmail());
        editForm.fillFirstName("UpdatedName").fillSalary("99999").submit();

        assertThat(tablePage.getRowDataByEmail(original.getEmail()))
                .satisfies(
                        row -> assertThat(row.get(0)).isEqualTo("UpdatedName"),
                        row -> assertThat(row.get(4)).isEqualTo("99999"));
    }

    @Test
    @DisplayName("Delete record — row is removed from the table")
    @Description("Adds a row, deletes it by email, and verifies it no longer appears in the table")
    void shouldDeleteRecord() {
        EmployeeRecord employee = EmployeeTestData.randomEmployee();
        tablePage.clickAdd().fill(employee).submit();

        assertThat(tablePage.isRowPresentWithEmail(employee.getEmail())).isTrue();

        tablePage.clickDeleteForRowWithEmail(employee.getEmail());

        assertThat(tablePage.isRowPresentWithEmail(employee.getEmail()))
                .as("Deleted row must not be visible in the table")
                .isFalse();
    }

    @Test
    @DisplayName("Search by any column — only the matching row is shown")
    @Description("Adds a row and verifies that searching by each column value returns exactly that row")
    void shouldFilterByAnyColumn() {
        EmployeeRecord employee = EmployeeTestData.randomEmployee();
        tablePage.clickAdd().fill(employee).submit();

        List.of(
                employee.getFirstName(), employee.getLastName(), employee.getAge(),
                employee.getEmail(), employee.getSalary(), employee.getDepartment()
        ).forEach(term -> {
            tablePage.search(term);
            assertThat(tablePage.getDataRowCount()).as("search term: '%s'", term).isEqualTo(1);
            assertThat(tablePage.isRowPresentWithEmail(employee.getEmail())).isTrue();
            tablePage.clearSearch();
        });
    }

    @Test
    @DisplayName("Search with no matches — table shows no data rows")
    @Description("Searches for a string that matches no record and verifies the table is effectively empty")
    void shouldShowNoResultsForNonMatchingSearch() {
        tablePage.search("NON_EXISTING_RECORD");

        assertThat(tablePage.getDataRowCount())
                .as("Table must have no data rows for a non-matching search term")
                .isZero();
    }

    @Test
    @DisplayName("Clear search — all rows are restored")
    @Description("Applies a filter, clears the search box, and verifies all default rows are visible again")
    void shouldRestoreAllRowsAfterClearingSearch() {
        tablePage.search("Cierra");
        assertThat(tablePage.getDataRowCount()).isEqualTo(1);

        tablePage.clearSearch();

        assertThat(tablePage.getDataRowCount())
                .as("Clearing the search must restore all default rows")
                .isEqualTo(DEFAULT_ROW_COUNT);
    }
}
