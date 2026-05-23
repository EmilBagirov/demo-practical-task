package com.flamingo.qa.tests.api.graphql;

import com.flamingo.qa.tests.api.base.BaseGraphQLTest;
import com.flamingo.qa.model.graphql.GraphQLRequest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@Tag("api")
@Feature("GraphQL Positive")
class GraphQLPositiveTest extends BaseGraphQLTest {

    @Test
    @DisplayName("GraphQL — query character list with pagination")
    @Description("Fetches page 1 of characters and verifies pagination metadata and non-empty results")
    void shouldQueryCharacterListWithPagination() {
        graphqlService.execute(GraphQLRequest.builder()
                        .query("{ characters(page: 1) { info { count pages } results { id name } } }")
                        .build())
                .statusCode(200)
                .body("errors", nullValue())
                .body("data.characters.info.count", greaterThan(0))
                .body("data.characters.info.pages", greaterThan(0))
                .body("data.characters.results.size()", greaterThan(0));
    }

    @Test
    @DisplayName("GraphQL — query single character by ID")
    @Description("Fetches character with ID 1 and verifies known stable field values")
    void shouldQuerySingleCharacterById() {
        graphqlService.execute(GraphQLRequest.builder()
                        .query("{ character(id: 1) { id name status species } }")
                        .build())
                .statusCode(200)
                .body("errors", nullValue())
                .body("data.character.id", equalTo("1"))
                .body("data.character.name", equalTo("Rick Sanchez"))
                .body("data.character.status", not(emptyString()))
                .body("data.character.species", not(emptyString()));
    }

    @Test
    @DisplayName("GraphQL — query using variables instead of string interpolation")
    @Description("Passes character ID via a named variable and verifies the response matches")
    void shouldQueryWithGraphQLVariables() {
        graphqlService.execute(GraphQLRequest.builder()
                        .query("query GetCharacter($id: ID!) { character(id: $id) { id name } }")
                        .variables(Map.of("id", "2"))
                        .build())
                .statusCode(200)
                .body("errors", nullValue())
                .body("data.character.id", equalTo("2"))
                .body("data.character.name", equalTo("Morty Smith"));
    }

    @Test
    @DisplayName("GraphQL — query with nested fields across types")
    @Description("Traverses Character → Location → dimension and Character → Episode to verify cross-type nesting")
    void shouldQueryNestedFieldsAcrossTypes() {
        graphqlService.execute(GraphQLRequest.builder()
                        .query("{ character(id: 1) { name origin { name dimension } episode { name air_date } } }")
                        .build())
                .statusCode(200)
                .body("errors", nullValue())
                .body("data.character.name", equalTo("Rick Sanchez"))
                .body("data.character.origin.name", not(emptyString()))
                .body("data.character.origin.dimension", notNullValue())
                .body("data.character.episode.size()", greaterThan(0));
    }
}
