package com.flamingo.qa.tests.api.graphql;

import com.flamingo.qa.tests.api.base.BaseGraphQLTest;
import com.flamingo.qa.models.graphql.GraphQLRequest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@Tag("api")
@Feature("GraphQL Negative")
class GraphQLNegativeTest extends BaseGraphQLTest {

    @Test
    @DisplayName("GraphQL — non-existent ID returns HTTP 200 with null data")
    @Description("GraphQL never returns 4xx for missing data; instead data.character is null with no errors array")
    void shouldReturnNullForNonExistentId() {
        int totalCount = graphqlService.getCharactersCount();
        // Character IDs are sequential 1..totalCount, so anything beyond is guaranteed non-existing
        String nonExistingId = String
                .valueOf(totalCount + ThreadLocalRandom.current().nextInt(1_000, 10_000));

        graphqlService.execute(GraphQLRequest.builder()
                        .query("query GetCharacter($id: ID!) { character(id: $id) { id name } }")
                        .variables(Map.of("id", nonExistingId))
                        .build())
                .statusCode(200)
                .body("data.character", nullValue())
                // Rick & Morty returns no errors for a missing entity — only data: null
                .body("errors", nullValue());
    }

    @Test
    @DisplayName("GraphQL — malformed query returns errors array with no data")
    @Description("Rick & Morty API returns HTTP 400 for syntax errors; errors[0].message must be present")
    void shouldReturnErrorForMalformedQuery() {
        // Missing closing paren after the argument list — deliberate syntax error
        graphqlService.execute(GraphQLRequest.builder()
                        .query("{ character(id: 1 { name } }")
                        .build())
                // Rick & Morty returns 400 for parse/syntax errors (not the spec default of 200)
                .statusCode(400)
                .body("errors.size()", not(0))
                .body("errors[0].message", not(emptyString()));
    }

    @Test
    @DisplayName("GraphQL — requesting a non-existent field returns a validation error")
    @Description("Rick & Morty API returns HTTP 400 for schema validation errors; errors[0].message must be present")
    void shouldReturnErrorForNonExistentField() {
        graphqlService.execute(GraphQLRequest.builder()
                        .query("{ character(id: 1) { id name nonExistentField } }")
                        .build())
                // Rick & Morty returns 400 for field-validation errors (not the spec default of 200)
                .statusCode(400)
                .body("errors.size()", not(0))
                .body("errors[0].message", not(emptyString()));
    }
}
