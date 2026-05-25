package com.flamingo.qa.services;

import com.flamingo.qa.configs.ApiConfig;
import com.flamingo.qa.models.graphql.GraphQLRequest;
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
