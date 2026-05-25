package com.flamingo.qa.tests.api.rest.booker;

import com.flamingo.qa.configs.ApiConfig;
import com.flamingo.qa.models.booking.AuthRequest;
import com.flamingo.qa.models.booking.Booking;
import com.flamingo.qa.testdata.BookingTestData;
import com.flamingo.qa.tests.api.base.BaseBookerTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
@Feature("Restful Booker CRUD")
class PatchBookingByIdTest extends BaseBookerTest {

    private String authToken;
    private int bookingId;

    @BeforeEach
    void setUp() {
        authToken = bookingService.authenticate(AuthRequest.builder()
                .username(ApiConfig.BOOKER_USERNAME)
                .password(ApiConfig.BOOKER_PASSWORD)
                .build());
        bookingId = bookingService.create(BookingTestData.randomBooking()).getBookingid();
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("partialUpdateCases")
    @DisplayName("PATCH /booking/{id} — partial update applies only specified fields")
    @Description("Verifies that each supported field can be updated independently, and that multiple-field patches work")
    void shouldPartiallyUpdateBooking(String description, Map<String, Object> patch, Consumer<Booking> verifier) {
        Booking updated = bookingService.patch(bookingId, authToken, patch);

        verifier.accept(updated);
    }

    static Stream<Arguments> partialUpdateCases() {
        return Stream.of(
                Arguments.of(
                        "firstname only",
                        Map.of("firstname", "UpdatedFirstName"),
                        (Consumer<Booking>) b -> assertThat(b.getFirstname())
                                .isEqualTo("UpdatedFirstName")),
                Arguments.of(
                        "lastname only",
                        Map.of("lastname", "UpdatedLastName"),
                        (Consumer<Booking>) b -> assertThat(b.getLastname())
                                .isEqualTo("UpdatedLastName")),
                Arguments.of(
                        "totalprice only",
                        Map.of("totalprice", 999),
                        (Consumer<Booking>) b -> assertThat(b.getTotalprice()).isEqualTo(999)),
                Arguments.of(
                        "depositpaid only",
                        Map.of("depositpaid", true),
                        (Consumer<Booking>) b -> assertThat(b.isDepositpaid()).isTrue()),
                Arguments.of(
                        "additionalneeds only",
                        Map.of("additionalneeds", "Late checkout"),
                        (Consumer<Booking>) b -> assertThat(b.getAdditionalneeds())
                                .isEqualTo("Late checkout")),
                Arguments.of(
                        "bookingdates only",
                        Map.of("bookingdates",
                                Map.of("checkin", "2030-01-01", "checkout", "2030-01-07")),
                        (Consumer<Booking>) b -> {
                            assertThat(b.getBookingdates().getCheckin()).isEqualTo("2030-01-01");
                            assertThat(b.getBookingdates().getCheckout()).isEqualTo("2030-01-07");
                        }),
                Arguments.of(
                        "firstname + totalprice (multiple fields)",
                        Map.of("firstname", "MultiUpdated", "totalprice", 777),
                        (Consumer<Booking>) b -> {
                            assertThat(b.getFirstname()).isEqualTo("MultiUpdated");
                            assertThat(b.getTotalprice()).isEqualTo(777);
                        })
        );
    }

    //TODO clarify correctness of 403 status code for these cases. More suitable code is 401
    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("invalidTokenCases")
    @DisplayName("PATCH /booking/{id} — invalid or missing token returns 403")
    @Description("Verifies 403 Forbidden for invalid, non-existing, empty, and absent token values")
    void shouldReturn403ForInvalidOrMissingToken(String description, String token) {
        int status = bookingService.patchGetStatus(bookingId, token, Map.of("firstname", "X"));

        assertThat(status)
                .as("%s should return 403 Forbidden", description)
                .isEqualTo(403);
    }

    static Stream<Arguments> invalidTokenCases() {
        return Stream.of(
                Arguments.of("invalid token",       "invalid-token-xyz"),
                Arguments.of("non-existing token",  "abc123def456xyz789"),
                Arguments.of("empty token",         ""),
                Arguments.of("without token",       (String) null)
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("invalidBookingIdCases")
    @DisplayName("PATCH /booking/{id} — invalid or non-existing booking ID returns 405")
    @Description("Verifies 405 for a numeric ID beyond the server's range and for a non-numeric path segment")
    void shouldReturn405ForInvalidBookingId(String description, String idOverride) {
        String id = idOverride != null
                ? idOverride
                : String.valueOf(bookingService.getLatestBookingId()
                        + ThreadLocalRandom.current().nextInt(10_000, 100_000));

        int status = bookingService.patchGetStatus(id, authToken, Map.of("firstname", "X"));

        assertThat(status)
                .as("%s should return 405", description)
                .isEqualTo(405);
    }

    static Stream<Arguments> invalidBookingIdCases() {
        return Stream.of(
                Arguments.of("non-existing numeric ID (beyond max)", null),
                Arguments.of("non-numeric booking ID (letters)",     "not-a-number")
        );
    }

    @Test
    @DisplayName("PATCH /booking — omitted booking ID returns 404")
    @Description("Sends PATCH to /booking without an ID path segment and verifies the server returns 404")
    void shouldReturn404WhenBookingIdOmitted() {
        int status = bookingService.patchGetStatusWithoutId(authToken, Map.of("firstname", "X"));

        assertThat(status)
                .as("PATCH /booking without an ID should return 404")
                .isEqualTo(404);
    }
}
