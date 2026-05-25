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

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
@Feature("Restful Booker CRUD")
class PutBookingByIdTest extends BaseBookerTest {

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

    @Test
    @DisplayName("PUT /booking/{id} — update existing booking")
    @Description("Replaces the pre-created booking with randomised data and verifies the response reflects all changes")
    void shouldUpdateBooking() {
        Booking updatePayload = BookingTestData.randomBooking();

        Booking updated = bookingService.update(bookingId, authToken, updatePayload);

        assertThat(updated)
                .usingRecursiveComparison()
                .isEqualTo(updatePayload);
    }

    //TODO clarify corectness of 403 status code for these cases. More suitable code is 401
    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("invalidTokenCases")
    @DisplayName("PUT /booking/{id} — invalid or missing token returns 403")
    @Description("Verifies 403 Forbidden for invalid, non-existing, empty, and absent token values")
    void shouldReturn403ForInvalidOrMissingToken(String description, String token) {
        int status = bookingService.updateGetStatus(bookingId, token, BookingTestData.randomBooking());

        assertThat(status)
                .as("%s should return 403 Forbidden", description)
                .isEqualTo(403);
    }

    static Stream<Arguments> invalidTokenCases() {
        return Stream.of(
                Arguments.of("invalid token",       "invalid-token-xyz"),
                Arguments.of("non-existing token",  "abc123def456xyz789"),
                Arguments.of("empty token",         ""),
                Arguments.of("without token",       (String) null)  // null = Cookie header omitted
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("invalidBookingIdCases")
    @DisplayName("PUT /booking/{id} — invalid or non-existing booking ID returns 405")
    @Description("Verifies 405 for a numeric ID beyond the server's range and for a non-numeric path segment")
    void shouldReturn405ForInvalidBookingId(String description, String idOverride) {
        // null sentinel = compute a genuinely non-existing numeric ID at runtime
        String id = idOverride != null
                ? idOverride
                : String.valueOf(bookingService.getLatestBookingId()
                        + ThreadLocalRandom.current().nextInt(10_000, 100_000));

        int status = bookingService.updateGetStatus(id, authToken, BookingTestData.randomBooking());

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
    @DisplayName("PUT /booking — omitted booking ID returns 404")
    @Description("Sends PUT to /booking without an ID path segment and verifies the server returns 404")
    void shouldReturn404WhenBookingIdOmitted() {
        int status = bookingService.updateGetStatusWithoutId(authToken, BookingTestData.randomBooking());

        assertThat(status)
                .as("PUT /booking without an ID should return 404")
                .isEqualTo(404);
    }
}
