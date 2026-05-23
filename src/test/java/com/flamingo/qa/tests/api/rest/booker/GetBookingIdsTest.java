package com.flamingo.qa.tests.api.rest.booker;

import com.flamingo.qa.model.booking.Booking;
import com.flamingo.qa.model.booking.BookingId;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
@Feature("Restful Booker CRUD")
class GetBookingIdsTest extends BaseBookerTest {

    // Generated once at class-load; fields shared between setUp and filterCombinations.
    // Static so the @MethodSource factory can reference them before @BeforeAll runs.
    private static final Booking FILTER_BOOKING = BookingTestData.randomBooking();

    private int createdBookingId;

    @BeforeEach
    void setUp() {
        createdBookingId = bookingService.create(FILTER_BOOKING).getBookingid();
    }

    @Test
    @DisplayName("GET /booking — returns non-empty list of all booking IDs")
    @Description("Verifies that listing all bookings returns HTTP 200 with a non-empty array containing the " +
            "pre-created booking")
    void shouldReturnAllBookingIds() {
        List<BookingId> ids = bookingService.getBookingIds();

        assertThat(ids).isNotEmpty();
        assertThat(ids).extracting(BookingId::getBookingid).contains(createdBookingId);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("filterCombinations")
    @DisplayName("GET /booking — optional filter combinations return valid results")
    @Description("Verifies that every supported query-parameter combination returns HTTP 200 with a valid list")
    void shouldFilterBookingIds(String description, Map<String, String> filters, boolean assertContainsCreated) {
        List<BookingId> result = bookingService.getBookingIds(filters);

        assertThat(result).as("response list must not be null for filters: " + description).isNotNull();
        if (assertContainsCreated) {
            assertThat(result)
                    .as("list must contain the booking created in setUp for filters: " + description)
                    .extracting(BookingId::getBookingid)
                    .contains(createdBookingId);
        }
    }

    static Stream<Arguments> filterCombinations() {
        String fn       = FILTER_BOOKING.getFirstname();
        String ln       = FILTER_BOOKING.getLastname();
        String checkin  = FILTER_BOOKING.getBookingdates().getCheckin();
        String checkout = FILTER_BOOKING.getBookingdates().getCheckout();

        return Stream.of(
                Arguments.of("no filters",
                        Map.of(), true),
                Arguments.of("firstname=" + fn,
                        Map.of("firstname", fn), true),
                Arguments.of("lastname=" + ln,
                        Map.of("lastname", ln), true),
                Arguments.of("firstname=" + fn + " & lastname=" + ln,
                        Map.of("firstname", fn, "lastname", ln), true),
                Arguments.of("checkin=" + checkin,
                        Map.of("checkin", checkin), false),
                Arguments.of("checkout=" + checkout,
                        Map.of("checkout", checkout), false),
                Arguments.of("checkin=" + checkin + " & checkout=" + checkout,
                        Map.of("checkin", checkin, "checkout", checkout), false),
                Arguments.of("all 4 params (firstname, lastname, checkin, checkout)",
                        Map.of("firstname", fn, "lastname", ln, "checkin", checkin, "checkout", checkout), false),
                Arguments.of("non-matching firstname (no results expected)",
                        Map.of("firstname", "NoSuchGuestXYZ99"), false)
        );
    }
}
