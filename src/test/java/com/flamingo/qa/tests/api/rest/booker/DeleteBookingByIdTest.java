package com.flamingo.qa.tests.api.rest.booker;

import com.flamingo.qa.config.ApiConfig;
import com.flamingo.qa.model.booking.AuthRequest;
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
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
@Feature("Restful Booker CRUD")
class DeleteBookingByIdTest extends BaseBookerTest {

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
    @DisplayName("DELETE /booking/{id} — valid request returns 201 and booking is gone")
    @Description("Deletes the pre-created booking with a valid token and verifies a subsequent GET returns 404")
    void shouldDeleteBooking() {
        bookingService.delete(bookingId, authToken);

        assertThat(bookingService.getStatusCode(bookingId))
                .as("deleted booking must return 404")
                .isEqualTo(404);
    }

    @ParameterizedTest(name = "[{index}] token: \"{0}\"")
    @ValueSource(strings = {
            "",                        // empty string
            "invalid-token-xyz",       // arbitrary garbage
            "abc123def456xyz789"       // token-shaped but non-existing
    })
    @DisplayName("DELETE /booking/{id} — invalid or non-existing token returns 403")
    @Description("Attempts deletion with empty, garbage, and fake-formatted tokens and verifies 403 Forbidden in " +
            "each case")
    void shouldReturn403ForInvalidToken(String invalidToken) {
        int status = bookingService.deleteGetStatus(bookingId, invalidToken);

        assertThat(status)
                .as("token '%s' should be rejected with 403 Forbidden", invalidToken)
                .isEqualTo(403);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("invalidBookingIdCases")
    @DisplayName("DELETE /booking/{id} — non-existing or non-numeric ID returns 405")
    @Description("Verifies 405 for a numeric ID beyond the server's range and for a non-numeric path segment")
    void shouldReturn405ForInvalidBookingId(String description, String idOverride) {
        // null sentinel = compute a genuinely non-existing numeric ID at runtime
        String id = idOverride != null
                ? idOverride
                : String.valueOf(bookingService.getLatestBookingId()
                        + ThreadLocalRandom.current().nextInt(10_000, 100_000));

        int status = bookingService.deleteGetStatus(id, authToken);

        assertThat(status)
                .as("%s should return 405", description)
                .isEqualTo(405);
    }

    static Stream<Arguments> invalidBookingIdCases() {
        return Stream.of(
                Arguments.of("non-existing numeric ID (beyond max)", null),
                Arguments.of("non-numeric booking ID (letters)", "not-a-number")
        );
    }
}
