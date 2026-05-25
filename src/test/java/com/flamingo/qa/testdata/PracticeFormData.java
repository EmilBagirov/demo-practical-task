package com.flamingo.qa.testdata;

import com.flamingo.qa.models.ui.PracticeFormRecord;
import com.github.javafaker.Faker;

import java.util.List;

public class PracticeFormData {

    private static final Faker FAKER = new Faker();

    private static final List<String> GENDERS  = List.of("Male", "Female", "Other");
    private static final List<String> HOBBIES  = List.of("Sports", "Reading", "Music");
    private static final List<String> SUBJECTS = List.of("Maths", "English", "Physics", "Chemistry", "Biology");
    private static final String[] MONTHS = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    private static final List<String[]> STATE_CITY_PAIRS = List.of(
            new String[]{"NCR",            "Delhi"},
            new String[]{"NCR",            "Gurgaon"},
            new String[]{"Uttar Pradesh",  "Agra"},
            new String[]{"Haryana",        "Panipat"},
            new String[]{"Rajasthan",      "Jaipur"}
    );

    private PracticeFormData() {}

    /** Every field populated with random data. */
    public static PracticeFormRecord random() {
        return random(null);
    }

    /** Every field populated with random data, plus a picture upload. */
    public static PracticeFormRecord random(String pictureFileName) {
        return randomBuilder().picture(pictureFileName).build();
    }

    /** Only the three mandatory fields (name, gender, mobile); optional fields are null. */
    public static PracticeFormRecord randomRequired() {
        return randomRequired(null);
    }

    /** Required fields only, plus a picture upload. */
    public static PracticeFormRecord randomRequired(String pictureFileName) {
        return randomBuilder()
                .email(null)
                .subject(null)
                .hobby(null)
                .currentAddress(null)
                .state(null)
                .city(null)
                .picture(pictureFileName)
                .build();
    }

    /**
     * Pre-filled builder backed by random defaults.
     * Override individual fields before calling {@code .build()}:
     * <pre>{@code
     *   PracticeFormRecord r = PracticeFormData.randomBuilder()
     *       .state("NCR")
     *       .city("Delhi")
     *       .build();
     * }</pre>
     */
    public static PracticeFormRecord.PracticeFormRecordBuilder randomBuilder() {
        String[] sc = STATE_CITY_PAIRS.get(FAKER.number().numberBetween(0, STATE_CITY_PAIRS.size()));

        return PracticeFormRecord.builder()
                .firstName(FAKER.name().firstName())
                .lastName(FAKER.name().lastName())
                .email(FAKER.internet().emailAddress())
                .gender(GENDERS.get(FAKER.number().numberBetween(0, GENDERS.size())))
                .mobile(String.valueOf(FAKER.number().numberBetween(1_000_000_000L, 9_999_999_999L)))
                .dateOfBirth(randomDob())
                .subject(SUBJECTS.get(FAKER.number().numberBetween(0, SUBJECTS.size())))
                .hobby(HOBBIES.get(FAKER.number().numberBetween(0, HOBBIES.size())))
                .currentAddress(FAKER.address().streetAddress())
                .state(sc[0])
                .city(sc[1]);
    }

    private static String randomDob() {
        int day = FAKER.number().numberBetween(1, 28);
        int mon = FAKER.number().numberBetween(1, 12);
        int yr  = FAKER.number().numberBetween(1950, 2005);
        return String.format("%02d %s %d", day, MONTHS[mon - 1], yr);
    }
}
