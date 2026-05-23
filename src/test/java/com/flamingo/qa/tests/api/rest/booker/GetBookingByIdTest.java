package com.flamingo.qa.tests.api.rest.booker;

import com.flamingo.qa.model.booking.Booking;
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
class GetBookingByIdTest extends BaseBookerTest {

    private Booking booking;
    private int bookingId;

    @BeforeEach
    void setUp() {
        booking = BookingTestData.randomBooking();
        bookingId = bookingService.create(booking).getBookingid();
    }

    @Test
    @DisplayName("GET /booking/{id} — retrieve booking by ID")
    @Description("Fetches the pre-created booking and verifies all fields round-trip correctly")
    void shouldGetBookingById() {
        Booking retrieved = bookingService.getById(bookingId);

        assertThat(retrieved)
                .usingRecursiveComparison()
                .isEqualTo(booking);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("invalidBookingIdCases")
    @DisplayName("GET /booking/{id} — non-existing or non-numeric ID returns 404")
    @Description("Verifies 404 for a numeric ID beyond the server's range and for a non-numeric path segment")
    void shouldReturn404ForInvalidBookingId(String description, String idOverride) {
        // null sentinel = compute a genuinely non-existing numeric ID at runtime
        String id = idOverride != null
                ? idOverride
                : String.valueOf(bookingService.getLatestBookingId()
                + ThreadLocalRandom.current().nextInt(10_000, 100_000));

        int status = bookingService.getStatusCode(id);

        assertThat(status)
                .as("%s should return 404", description)
                .isEqualTo(404);
    }

    static Stream<Arguments> invalidBookingIdCases() {
        return Stream.of(
                Arguments.of("non-existing numeric ID (beyond max)", null),
                Arguments.of("non-numeric booking ID (letters)", "not-a-number")
        );
    }
}
