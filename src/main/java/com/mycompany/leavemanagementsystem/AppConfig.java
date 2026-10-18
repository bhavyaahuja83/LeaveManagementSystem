package com.mycompany.leavemanagementsystem;

import io.github.cdimascio.dotenv.Dotenv;

public final class AppConfig {
    private static final Dotenv DOTENV = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    private AppConfig() {
    }

    public static String get(String key, String defaultValue) {
        String environmentValue = System.getenv(key);
        if (environmentValue != null) {
            return environmentValue;
        }
        String dotenvValue = DOTENV.get(key);
        return dotenvValue == null ? defaultValue : dotenvValue;
    }

    public static String required(String key) {
        String value = get(key, null);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required configuration: " + key);
        }
        return value;
    }
}
