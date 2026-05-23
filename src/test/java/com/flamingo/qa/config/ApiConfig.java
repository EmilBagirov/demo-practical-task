package com.flamingo.qa.config;

import com.flamingo.qa.utils.CredentialsDecoder;

public final class ApiConfig {

    public static final String BOOKER_BASE_URL = "https://restful-booker.herokuapp.com";

    // Credentials encoded as Base64 to keep plaintext out of source
    public static final String BOOKER_USERNAME = CredentialsDecoder.decode("YWRtaW4=");
    public static final String BOOKER_PASSWORD = CredentialsDecoder.decode("cGFzc3dvcmQxMjM=");

    // Rick and Morty public GraphQL API — satisfies all GraphQL test scenarios
    // (list with pagination, single entity, variables, nested fields, error cases)
    public static final String GRAPHQL_BASE_URL = "https://rickandmortyapi.com";
    public static final String GRAPHQL_PATH = "/graphql";

    private ApiConfig() {}
}
