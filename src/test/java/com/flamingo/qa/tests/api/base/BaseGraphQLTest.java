package com.flamingo.qa.tests.api.base;

import com.flamingo.qa.configs.ApiConfig;
import com.flamingo.qa.services.GraphQLService;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseGraphQLTest extends BaseApiTest {

    protected static GraphQLService graphqlService;

    @BeforeAll
    static void initGraphQLService() {
        graphqlService = new GraphQLService(buildSpec(ApiConfig.GRAPHQL_BASE_URL));
    }
}
