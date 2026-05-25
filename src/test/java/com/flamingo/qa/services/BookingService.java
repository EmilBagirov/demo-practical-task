package com.flamingo.qa.services;

import com.flamingo.qa.models.booking.AuthRequest;
import com.flamingo.qa.models.booking.AuthResponse;
import com.flamingo.qa.models.booking.Booking;
import com.flamingo.qa.models.booking.BookingId;
import io.qameta.allure.Step;
import io.restassured.common.mapper.TypeRef;
import io.restassured.specification.RequestSpecification;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static io.restassured.RestAssured.given;

public class BookingService {

    private static final Logger LOG = Logger.getLogger(BookingService.class.getName());

    private final RequestSpecification spec;

    public BookingService(RequestSpecification spec) {
        this.spec = spec;
    }

    @Step("POST /auth — authenticate as {credentials.username}")
    public String authenticate(AuthRequest credentials) {
        LOG.info("[STEP] POST /auth — authenticate as '" + credentials.getUsername() + "'");
        return given(spec)
                .body(credentials)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .as(AuthResponse.class)
                .getToken();
    }

    @Step("POST /booking — create booking for {booking.firstname} {booking.lastname}")
    public BookingId create(Booking booking) {
        LOG.info("[STEP] POST /booking — create booking [" + booking.getFirstname() + " " +
                booking.getLastname() + "]");
        return given(spec)
                .body(booking)
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .extract()
                .as(BookingId.class);
    }

    @Step("GET /booking/{id}")
    public Booking getById(int id) {
        LOG.info("[STEP] GET /booking/" + id);
        return given(spec)
                .when()
                .get("/booking/{id}", id)
                .then()
                .statusCode(200)
                .extract()
                .as(Booking.class);
    }

    @Step("PUT /booking/{id} — update booking")
    public Booking update(int id, String token, Booking booking) {
        LOG.info("[STEP] PUT /booking/" + id + " — update booking [" + booking.getFirstname() + " " +
                booking.getLastname() + "]");
        return given(spec)
                .header("Cookie", "token=" + token)
                .body(booking)
                .when()
                .put("/booking/{id}", id)
                .then()
                .statusCode(200)
                .extract()
                .as(Booking.class);
    }

    @Step("PUT /booking/{id} — extract status code")
    public int updateGetStatus(String id, String token, Booking booking) {
        LOG.info("[STEP] PUT /booking/" + id + " — extract status code");
        RequestSpecification req = given(spec);
        if (token != null) {
            req = req.header("Cookie", "token=" + token);
        }
        return req
                .body(booking)
                .when()
                .put("/booking/{id}", id)
                .then()
                .extract()
                .statusCode();
    }

    public int updateGetStatus(int id, String token, Booking booking) {
        return updateGetStatus(String.valueOf(id), token, booking);
    }

    @Step("PUT /booking — extract status code (no booking ID)")
    public int updateGetStatusWithoutId(String token, Booking booking) {
        LOG.info("[STEP] PUT /booking — extract status code (no booking ID)");
        return given(spec)
                .header("Cookie", "token=" + token)
                .body(booking)
                .when()
                .put("/booking")
                .then()
                .extract()
                .statusCode();
    }

    @Step("PATCH /booking/{id} — partial update")
    public Booking patch(int id, String token, Map<String, Object> fields) {
        LOG.info("[STEP] PATCH /booking/" + id + " — partial update " + fields.keySet());
        return given(spec)
                .header("Cookie", "token=" + token)
                .body(fields)
                .when()
                .patch("/booking/{id}", id)
                .then()
                .statusCode(200)
                .extract()
                .as(Booking.class);
    }

    @Step("PATCH /booking/{id} — extract status code")
    public int patchGetStatus(String id, String token, Map<String, Object> fields) {
        LOG.info("[STEP] PATCH /booking/" + id + " — extract status code");
        RequestSpecification req = given(spec);
        if (token != null) {
            req = req.header("Cookie", "token=" + token);
        }
        return req
                .body(fields)
                .when()
                .patch("/booking/{id}", id)
                .then()
                .extract()
                .statusCode();
    }

    public int patchGetStatus(int id, String token, Map<String, Object> fields) {
        return patchGetStatus(String.valueOf(id), token, fields);
    }

    @Step("PATCH /booking — extract status code (no booking ID)")
    public int patchGetStatusWithoutId(String token, Map<String, Object> fields) {
        LOG.info("[STEP] PATCH /booking — extract status code (no booking ID)");
        return given(spec)
                .header("Cookie", "token=" + token)
                .body(fields)
                .when()
                .patch("/booking")
                .then()
                .extract()
                .statusCode();
    }

    @Step("DELETE /booking/{id}")
    public void delete(int id, String token) {
        LOG.info("[STEP] DELETE /booking/" + id);
        given(spec)
                .header("Cookie", "token=" + token)
                .when()
                .delete("/booking/{id}", id)
                .then()
                .statusCode(201);
    }

    @Step("DELETE /booking/{id} — extract status code")
    public int deleteGetStatus(String id, String token) {
        LOG.info("[STEP] DELETE /booking/" + id + " — extract status code");
        return given(spec)
                .header("Cookie", "token=" + token)
                .when()
                .delete("/booking/{id}", id)
                .then()
                .extract()
                .statusCode();
    }

    public int deleteGetStatus(int id, String token) {
        return deleteGetStatus(String.valueOf(id), token);
    }

    @Step("GET /booking/{id} — check status code")
    public int getStatusCode(String id) {
        LOG.info("[STEP] GET /booking/" + id + " — check status code");
        return given(spec)
                .when()
                .get("/booking/{id}", id)
                .then()
                .extract()
                .statusCode();
    }

    public int getStatusCode(int id) {
        return getStatusCode(String.valueOf(id));
    }

    @Step("GET /booking — list all booking IDs")
    public List<BookingId> getBookingIds() {
        return getBookingIds(Map.of());
    }

    @Step("GET /booking — list booking IDs with filters {filters}")
    public List<BookingId> getBookingIds(Map<String, String> filters) {
        LOG.info("[STEP] GET /booking — list booking IDs" + (filters.isEmpty() ? "" : " [filters: " + filters + "]"));
        return given(spec)
                .queryParams(filters)
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<List<BookingId>>() {});
    }

    @Step("GET /booking — find latest booking ID")
    public int getLatestBookingId() {
        LOG.info("[STEP] GET /booking — find latest booking ID");
        return getBookingIds().stream()
                .mapToInt(BookingId::getBookingid)
                .max()
                .orElseThrow(() -> new IllegalStateException("No bookings found"));
    }

    @Step("GET /ping — health check")
    public int ping() {
        LOG.info("[STEP] GET /ping — health check");
        return given(spec)
                .when()
                .get("/ping")
                .then()
                .extract()
                .statusCode();
    }
}
