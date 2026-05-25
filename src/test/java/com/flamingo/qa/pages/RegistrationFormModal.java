package com.flamingo.qa.pages;

import com.flamingo.qa.models.ui.EmployeeRecord;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class RegistrationFormModal {

    private final Page page;

    public RegistrationFormModal(Page page) {
        this.page = page;
    }

    @Step("Check registration form modal is visible")
    public boolean isVisible() {
        return page.locator(".modal-content").isVisible();
    }

    @Step("Fill registration form")
    public RegistrationFormModal fill(EmployeeRecord employee) {
        page.fill("#firstName",  employee.getFirstName());
        page.fill("#lastName",   employee.getLastName());
        page.fill("#userEmail",  employee.getEmail());
        page.fill("#age",        employee.getAge());
        page.fill("#salary",     employee.getSalary());
        page.fill("#department", employee.getDepartment());
        return this;
    }

    @Step("Fill first name: {value}")
    public RegistrationFormModal fillFirstName(String value) {
        page.fill("#firstName", value);
        return this;
    }

    @Step("Fill salary: {value}")
    public RegistrationFormModal fillSalary(String value) {
        page.fill("#salary", value);
        return this;
    }

    @Step("Submit registration form")
    public void submit() {
        page.locator("#submit").click();
    }
}
