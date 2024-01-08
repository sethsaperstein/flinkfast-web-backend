package com.example.helloworld.models;

import lombok.Value;

import javax.annotation.Nullable;

@Value
public class Session {

    private String id;
    private String status;

    public static Session from(final String id, @Nullable final String status) {
        return new Session(id, status);
    }
}
