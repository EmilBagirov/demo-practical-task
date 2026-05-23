package com.flamingo.qa.service;

import com.flamingo.qa.config.ApiConfig;
import com.flamingo.qa.model.graphql.GraphQLRequest;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class GraphQLService {

    private final RequestSpecification spec;

    public GraphQLService(RequestSpecification spec) {
        this.spec = spec;
    }

    @Step("POST /graphql — execute query")
    public ValidatableResponse execute(GraphQLRequest request) {
        return given(spec)
                .body(request)
                .when()
                .post(ApiConfig.GRAPHQL_PATH)
                .then();
    }

    @Step("GraphQL — get total character count")
    public int getCharactersCount() {
        return given(spec)
                .body(GraphQLRequest.builder()
                        .query("{ characters { info { count } } }")
                        .build())
                .when()
                .post(ApiConfig.GRAPHQL_PATH)
                .then()
                .statusCode(200)
                .extract()
                .path("data.characters.info.count");
    }
}
