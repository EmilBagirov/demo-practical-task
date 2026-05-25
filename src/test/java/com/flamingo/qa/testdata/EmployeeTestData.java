package com.flamingo.qa.testdata;

import com.flamingo.qa.models.ui.EmployeeRecord;
import com.github.javafaker.Faker;

public class EmployeeTestData {

    private static final Faker FAKER = new Faker();

    public static EmployeeRecord randomEmployee() {
        return EmployeeRecord.builder()
                .firstName(FAKER.name().firstName())
                .lastName(FAKER.name().lastName())
                .age(String.valueOf(FAKER.number().numberBetween(18, 65)))
                .email(FAKER.internet().emailAddress())
                .salary(String.valueOf(FAKER.number().numberBetween(30_000, 150_000)))
                .department(FAKER.commerce().department())
                .build();
    }
}
