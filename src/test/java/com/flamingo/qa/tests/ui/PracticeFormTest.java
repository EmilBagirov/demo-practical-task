package com.flamingo.qa.tests.ui;

import com.flamingo.qa.models.ui.PracticeFormRecord;
import com.flamingo.qa.pages.PracticeFormPage;
import com.flamingo.qa.testdata.PracticeFormData;
import com.flamingo.qa.tests.ui.base.BaseUITest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.flamingo.qa.pages.PracticeFormPage.fillRequiredExceptEmail;
import static com.flamingo.qa.pages.PracticeFormPage.fillRequiredExceptMobile;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("ui")
@Feature("Practice Form")
class PracticeFormTest extends BaseUITest {

    private PracticeFormPage formPage;

    @BeforeEach
    void openForm() {
        formPage = new PracticeFormPage(page);
        formPage.navigate();
    }

    // ── Happy path ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Submit form with required fields only — confirmation modal is shown")
    @Description("Fills only the mandatory fields (name, gender, mobile) and verifies the success modal appears")
    void shouldSubmitFormWithRequiredFieldsOnly() {
        PracticeFormRecord data = PracticeFormData.randomRequired();

        formPage
                .fillFirstName(data.getFirstName())
                .fillLastName(data.getLastName())
                .selectGender(data.getGender())
                .fillMobile(data.getMobile())
                .submit();

        formPage.getConfirmationModal().assertData(data);
    }

    @Test
    @DisplayName("Submit form with all fields — confirmation modal shows correct values")
    @Description("Fills every field on the form and verifies the modal table reflects all submitted values")
    void shouldSubmitFormWithAllFields() throws URISyntaxException {
        PracticeFormRecord data = PracticeFormData.random("picture.jpg");

        formPage
                .fillFirstName(data.getFirstName())
                .fillLastName(data.getLastName())
                .fillEmail(data.getEmail())
                .selectGender(data.getGender())
                .fillMobile(data.getMobile())
                .setDateOfBirth(data.getDateOfBirth())
                .addSubject(data.getSubject())
                .selectHobby(data.getHobby())
                .uploadPicture(getFixture(data.getPicture()))
                .fillCurrentAddress(data.getCurrentAddress())
                .selectState(data.getState())
                .selectCity(data.getCity())
                .submit();

        formPage.getConfirmationModal().assertData(data);
    }

    @ParameterizedTest(name = "[{index}] gender = {0}")
    @ValueSource(strings = {"Male", "Female", "Other"})
    @DisplayName("Each gender option can be selected and submitted")
    @Description("Parameterized test verifying that Male, Female, and Other radio options are each selectable")
    void shouldAcceptEachGenderOption(String gender) {
        PracticeFormRecord base = PracticeFormData.randomRequired().toBuilder().gender(gender).build();

        formPage
                .fillFirstName(base.getFirstName())
                .fillLastName(base.getLastName())
                .selectGender(gender)
                .fillMobile(base.getMobile())
                .submit();

        formPage.getConfirmationModal().assertData(base);
    }

    @Test
    @DisplayName("All three hobbies can be selected simultaneously")
    @Description("Selects Sports, Reading, and Music together and verifies the modal lists all three")
    void shouldSelectMultipleHobbies() {
        PracticeFormRecord base = PracticeFormData.randomRequired();

        formPage
                .fillFirstName(base.getFirstName())
                .fillLastName(base.getLastName())
                .selectGender(base.getGender())
                .fillMobile(base.getMobile())
                .selectHobby("Sports")
                .selectHobby("Reading")
                .selectHobby("Music")
                .submit();

        String hobbies = formPage.getConfirmationModal().getSubmittedData().get("Hobbies");
        assertThat(hobbies).contains("Sports", "Reading", "Music");
    }

    @Test
    @DisplayName("City has no options until a State is selected")
    @Description("Verifies the city dropdown is empty before any state is chosen (State–City dependency)")
    void shouldHaveNoCityOptionsBeforeStateIsSelected() {
        assertThat(formPage.isCityDisabled())
                .as("City dropdown must have no options when no state is selected")
                .isTrue();
    }

    @ParameterizedTest(name = "[{index}] State: {0}")
    @MethodSource("stateCityCases")
    @DisplayName("City dropdown shows only the cities that belong to the selected state")
    @Description("Selects each state and verifies the city dropdown contains exactly the expected cities for that " +
            "state")
    void shouldShowOnlyCitiesForSelectedState(String state, List<String> expectedCities) {
        formPage.selectState(state);

        assertThat(formPage.getCityOptions())
                .as("Available cities for state '%s'", state)
                .containsExactlyInAnyOrderElementsOf(expectedCities);
    }

    // ── Validation (negative) ───────────────────────────────────────────────

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("invalidFormCases")
    @DisplayName("Form does not submit with invalid input — fields are highlighted")
    void shouldNotSubmitWithInvalidInput(String description, Consumer<PracticeFormPage> testCase) {
        testCase.accept(formPage);
    }

    static Stream<Arguments> stateCityCases() {
        return Stream.of(
                Arguments.of("NCR",           List.of("Delhi", "Gurgaon", "Noida")),
                Arguments.of("Uttar Pradesh", List.of("Agra", "Lucknow", "Merrut")),
                Arguments.of("Haryana",       List.of("Karnal", "Panipat")),
                Arguments.of("Rajasthan",     List.of("Jaipur", "Jaiselmer"))
        );
    }

    static Stream<Arguments> invalidFormCases() {
        return Stream.of(
                // Empty form
                Arguments.of(
                        "Empty form — all required fields missing",
                        (Consumer<PracticeFormPage>) fp -> {
                            fp.submit();
                            assertThat(fp.getConfirmationModal().isVisible())
                                    .as("modal must not appear").isFalse();
                            fp.assertFieldsInvalid("firstName", "lastName", "userNumber",
                                    "gender-radio-1", "gender-radio-2", "gender-radio-3");
                        }),

                // Invalid email variations
                Arguments.of(
                        "Email missing @ symbol",
                        (Consumer<PracticeFormPage>) fp -> {
                            fillRequiredExceptEmail(fp).fillEmail("invalidemail.com").submit();
                            assertThat(fp.getConfirmationModal().isVisible())
                                    .as("modal must not appear").isFalse();
                            fp.assertFieldsInvalid("userEmail");
                        }),
                Arguments.of(
                        "Email missing domain after @",
                        (Consumer<PracticeFormPage>) fp -> {
                            fillRequiredExceptEmail(fp).fillEmail("user@").submit();
                            assertThat(fp.getConfirmationModal().isVisible())
                                    .as("modal must not appear").isFalse();
                            fp.assertFieldsInvalid("userEmail");
                        }),
                Arguments.of(
                        "Email missing local part before @",
                        (Consumer<PracticeFormPage>) fp -> {
                            fillRequiredExceptEmail(fp).fillEmail("@domain.com").submit();
                            assertThat(fp.getConfirmationModal().isVisible())
                                    .as("modal must not appear").isFalse();
                            fp.assertFieldsInvalid("userEmail");
                        }),
                Arguments.of(
                        "Email with spaces",
                        (Consumer<PracticeFormPage>) fp -> {
                            fillRequiredExceptEmail(fp).fillEmail("user name@domain.com").submit();
                            assertThat(fp.getConfirmationModal().isVisible())
                                    .as("modal must not appear").isFalse();
                            fp.assertFieldsInvalid("userEmail");
                        }),
                Arguments.of(
                        "Email with double @",
                        (Consumer<PracticeFormPage>) fp -> {
                            fillRequiredExceptEmail(fp).fillEmail("user@@domain.com").submit();
                            assertThat(fp.getConfirmationModal().isVisible())
                                    .as("modal must not appear").isFalse();
                            fp.assertFieldsInvalid("userEmail");
                        }),

                // Invalid mobile — numeric but too short: JS keeps the value, field is highlighted
                Arguments.of(
                        "Mobile too short — 3 digits",
                        (Consumer<PracticeFormPage>) fp -> {
                            fillRequiredExceptMobile(fp).fillMobile("123").submit();
                            assertThat(fp.getConfirmationModal().isVisible())
                                    .as("modal must not appear").isFalse();
                            fp.assertFieldsInvalid("userNumber");
                        }),
                Arguments.of(
                        "Mobile too short — 9 digits",
                        (Consumer<PracticeFormPage>) fp -> {
                            fillRequiredExceptMobile(fp).fillMobile("123456789").submit();
                            assertThat(fp.getConfirmationModal().isVisible())
                                    .as("modal must not appear").isFalse();
                            fp.assertFieldsInvalid("userNumber");
                        }),

                // Invalid mobile — non-numeric input: JS strips chars, field ends up empty;
                // form doesn't submit, no specific field to highlight
                Arguments.of(
                        "Mobile with letters",
                        (Consumer<PracticeFormPage>) fp -> {
                            fillRequiredExceptMobile(fp).fillMobile("abcdefghij").submit();
                            assertThat(fp.getConfirmationModal().isVisible())
                                    .as("modal must not appear").isFalse();
                        }),
                Arguments.of(
                        "Mobile with special symbols",
                        (Consumer<PracticeFormPage>) fp -> {
                            fillRequiredExceptMobile(fp).fillMobile("+1(800)123").submit();
                            assertThat(fp.getConfirmationModal().isVisible())
                                    .as("modal must not appear").isFalse();
                        })
        );
    }
}
