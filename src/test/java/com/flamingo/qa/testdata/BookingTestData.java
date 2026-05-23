package com.flamingo.qa.testdata;

import com.flamingo.qa.model.booking.Booking;
import com.flamingo.qa.model.booking.BookingDates;
import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BookingTestData {

    private static final Faker FAKER = new Faker();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private BookingTestData() {}

    /**
     * Returns a fully randomised {@link Booking} suitable for most test scenarios.
     * Checkin is a random future date; checkout is 1–14 days after checkin.
     */
    public static Booking randomBooking() {
        return randomBookingBuilder().build();
    }

    /**
     * Returns a pre-filled {@link Booking.BookingBuilder} backed by Faker defaults.
     * Callers can override individual fields before calling {@code .build()}:
     * <pre>{@code
     *   Booking b = BookingTestData.randomBookingBuilder()
     *       .firstname("Jane")
     *       .lastname("Doe")
     *       .build();
     * }</pre>
     */
    public static Booking.BookingBuilder randomBookingBuilder() {
        LocalDate checkin = LocalDate.now().plusDays(FAKER.number().numberBetween(1, 180));
        LocalDate checkout = checkin.plusDays(FAKER.number().numberBetween(1, 14));

        return Booking.builder()
                .firstname(FAKER.name().firstName())
                .lastname(FAKER.name().lastName())
                .totalprice(FAKER.number().numberBetween(50, 1000))
                .depositpaid(FAKER.bool().bool())
                .bookingdates(BookingDates.builder()
                        .checkin(checkin.format(DATE_FMT))
                        .checkout(checkout.format(DATE_FMT))
                        .build())
                .additionalneeds(FAKER.food().dish());
    }
}
