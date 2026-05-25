package com.flamingo.qa.configs;

import com.flamingo.qa.utils.CredentialsDecoder;

public final class ApiConfig {

    public static final String BOOKER_BASE_URL = ConfigLoader.get("booker.base.url");
    public static final String BOOKER_USERNAME = CredentialsDecoder.decode(ConfigLoader.get("booker.username"));
    public static final String BOOKER_PASSWORD = CredentialsDecoder.decode(ConfigLoader.get("booker.password"));

    public static final String GRAPHQL_BASE_URL = ConfigLoader.get("graphql.base.url");
    public static final String GRAPHQL_PATH     = ConfigLoader.get("graphql.path");

    private ApiConfig() {}
}
