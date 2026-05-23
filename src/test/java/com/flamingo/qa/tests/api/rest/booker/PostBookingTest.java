package com.flamingo.qa.tests.api.rest.booker;

import com.flamingo.qa.model.booking.Booking;
import com.flamingo.qa.model.booking.BookingId;
import com.flamingo.qa.testdata.BookingTestData;
import com.flamingo.qa.tests.api.base.BaseBookerTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
@Feature("Restful Booker CRUD")
class PostBookingTest extends BaseBookerTest {

    @Test
    @DisplayName("POST /booking — create a new booking")
    @Description("Creates a randomised booking and verifies the response body round-trips all sent fields")
    void shouldCreateBooking() {
        Booking payload = BookingTestData.randomBooking();

        BookingId response = bookingService.create(payload);

        assertThat(response.getBookingid()).isPositive();
        assertThat(response.getBooking())
                .usingRecursiveComparison()
                .isEqualTo(payload);
    }
}
