package com.flamingo.qa.models.ui;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class PracticeFormRecord {
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String mobile;
    private String dateOfBirth;
    private String subject;
    private String hobby;
    private String currentAddress;
    private String state;
    private String city;
    private String picture;
}
