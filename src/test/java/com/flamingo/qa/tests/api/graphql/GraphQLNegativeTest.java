package com.flamingo.qa.tests.api.graphql;

import com.flamingo.qa.tests.api.base.BaseGraphQLTest;
import com.flamingo.qa.models.graphql.GraphQLRequest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@Tag("api")
@Feature("GraphQL Negative")
class GraphQLNegativeTest extends BaseGraphQLTest {

    @Test
    @DisplayName("GraphQL — non-existent ID returns HTTP 200 with null data")
    @Description("GraphQL never returns 4xx for missing data; instead data.movie is null with no errors array")
    void shouldReturnNullForNonExistentId() {
        graphqlService.execute(GraphQLRequest.builder()
                        .query("{ movie(where: {id: \"clq000000000000000000000000\"}) { id title } }")
                        .build())
                .statusCode(200)
                .body("data.movie", nullValue())
                .body("errors", nullValue());
    }

    @Test
    @DisplayName("GraphQL — malformed query returns HTTP 400 with errors array")
    @Description("Hygraph returns HTTP 400 for syntax errors; errors[0].message must be present")
    void shouldReturnErrorForMalformedQuery() {
        // Missing closing paren after the argument list — deliberate syntax error
        graphqlService.execute(GraphQLRequest.builder()
                        .query("{ movie(where: {id: \"x\" { title } }")
                        .build())
                .statusCode(400)
                .body("errors.size()", not(0))
                .body("errors[0].message", not(emptyString()));
    }

    @Test
    @DisplayName("GraphQL — requesting a non-existent field returns HTTP 400 with validation error")
    @Description("Hygraph returns HTTP 400 for schema validation errors; errors[0].message must be present")
    void shouldReturnErrorForNonExistentField() {
        graphqlService.execute(GraphQLRequest.builder()
                        .query("{ movies { id nonExistentField } }")
                        .build())
                .statusCode(400)
                .body("errors.size()", not(0))
                .body("errors[0].message", not(emptyString()));
    }
}
