package com.flamingo.qa.utils;

import com.flamingo.qa.services.BookingService;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.Assumptions;

public class ServiceHealthChecker {

    private ServiceHealthChecker() {}

    /**
     * Pings the Restful Booker service and aborts the test class if it is unavailable.
     * Restful Booker returns 201 (not 200) from GET /ping to indicate the service is up.
     */
    public static void verifyBookerHealth(BookingService bookingService) {
        Allure.step("Health check: GET /ping → expect 201", () -> {
            int status = bookingService.ping();
            if (status != 201) {
                String message = "Restful Booker health check failed — GET /ping returned HTTP "
                        + status + ". All booking tests are aborted.";
                Allure.addAttachment("Health Check Failure", message);
                Assumptions.assumeTrue(false, message);
            }
        });
    }
}
