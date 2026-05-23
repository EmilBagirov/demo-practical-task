package com.flamingo.qa.tests.api.base;

import com.flamingo.qa.config.ApiConfig;
import com.flamingo.qa.service.BookingService;
import com.flamingo.qa.utils.ServiceHealthChecker;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseBookerTest extends BaseApiTest {

    protected static BookingService bookingService;

    @BeforeAll
    static void initBookerService() {
        bookingService = new BookingService(buildSpec(ApiConfig.BOOKER_BASE_URL));
        ServiceHealthChecker.verifyBookerHealth(bookingService);
    }
}
