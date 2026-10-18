package com.mycompany.leavemanagementsystem;

import io.github.cdimascio.dotenv.Dotenv;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AppConfig {
    private static volatile Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    private AppConfig() {
    }

    public static void loadFromApplicationPath(String applicationPath) {
        if (applicationPath == null || applicationPath.isBlank()) {
            return;
        }

        Path directory = Path.of(applicationPath).toAbsolutePath().normalize();
        for (int depth = 0; directory != null && depth <= 4; depth++, directory = directory.getParent()) {
            if (Files.isRegularFile(directory.resolve(".env"))) {
                dotenv = Dotenv.configure()
                        .directory(directory.toString())
                        .ignoreIfMissing()
                        .load();
                return;
            }
        }
    }

    public static String get(String key, String defaultValue) {
        String environmentValue = System.getenv(key);
        if (environmentValue != null) {
            return environmentValue;
        }
        String dotenvValue = dotenv.get(key);
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
