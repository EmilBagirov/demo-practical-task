package com.flamingo.qa.tests.api.graphql;

import com.flamingo.qa.tests.api.base.BaseGraphQLTest;
import com.flamingo.qa.models.graphql.GraphQLRequest;
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
import static org.hamcrest.Matchers.nullValue;

@Tag("api")
@Feature("GraphQL Positive")
class GraphQLPositiveTest extends BaseGraphQLTest {

    @Test
    @DisplayName("GraphQL — query movie list with pagination")
    @Description("Fetches first 5 movies via moviesConnection and verifies aggregate count, pageInfo, and non-empty edges")
    void shouldQueryMovieListWithPagination() {
        graphqlService.execute(GraphQLRequest.builder()
                        .query("{ moviesConnection(first: 5) { aggregate { count } pageInfo { hasNextPage } edges { node { id title } } } }")
                        .build())
                .statusCode(200)
                .body("errors", nullValue())
                .body("data.moviesConnection.aggregate.count", greaterThan(0))
                .body("data.moviesConnection.pageInfo.hasNextPage", equalTo(true))
                .body("data.moviesConnection.edges.size()", greaterThan(0));
    }

    @Test
    @DisplayName("GraphQL — query single movie by ID")
    @Description("Fetches Jaws by its stable ID and verifies known field values")
    void shouldQuerySingleMovieById() {
        graphqlService.execute(GraphQLRequest.builder()
                        .query("{ movie(where: {id: \"clq16555f0nqq0ak8fvxe2c0d\"}) { id title slug } }")
                        .build())
                .statusCode(200)
                .body("errors", nullValue())
                .body("data.movie.id", equalTo("clq16555f0nqq0ak8fvxe2c0d"))
                .body("data.movie.title", equalTo("Jaws"))
                .body("data.movie.slug", equalTo("jaws"));
    }

    @Test
    @DisplayName("GraphQL — query using variables instead of string interpolation")
    @Description("Passes movie slug via a named variable and verifies the response matches")
    void shouldQueryWithGraphQLVariables() {
        graphqlService.execute(GraphQLRequest.builder()
                        .query("query GetMovie($slug: String!) { movie(where: {slug: $slug}) { id title slug } }")
                        .variables(Map.of("slug", "jaws"))
                        .build())
                .statusCode(200)
                .body("errors", nullValue())
                .body("data.movie.title", equalTo("Jaws"))
                .body("data.movie.slug", equalTo("jaws"));
    }

    @Test
    @DisplayName("GraphQL — query with nested fields across types")
    @Description("Traverses Movie → Asset (moviePoster) and verifies cross-type nesting returns a valid URL")
    void shouldQueryNestedFieldsAcrossTypes() {
        graphqlService.execute(GraphQLRequest.builder()
                        .query("{ movie(where: {slug: \"requiem-for-a-dream\"}) { title moviePoster { id url } } }")
                        .build())
                .statusCode(200)
                .body("errors", nullValue())
                .body("data.movie.title", equalTo("Requiem for a dream"))
                .body("data.movie.moviePoster.id", equalTo("clq5mrve80z470bk0ufvjw8lj"))
                .body("data.movie.moviePoster.url", not(emptyString()));
    }
}
