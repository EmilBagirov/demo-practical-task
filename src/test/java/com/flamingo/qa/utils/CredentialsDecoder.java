package com.flamingo.qa.utils;

import java.util.Base64;

public final class CredentialsDecoder {

    private CredentialsDecoder() {}

    public static String decode(String encoded) {
        return new String(Base64.getDecoder().decode(encoded));
    }
}
