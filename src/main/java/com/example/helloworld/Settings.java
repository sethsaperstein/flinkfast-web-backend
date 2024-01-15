package com.example.helloworld;

public class Settings {
    public static final String LOCAL_ENV = "local";

    public static String getEnvironment() {
        String env = System.getenv("ENV");
        return (env != null && !env.isEmpty()) ? env : LOCAL_ENV;
    }
}
