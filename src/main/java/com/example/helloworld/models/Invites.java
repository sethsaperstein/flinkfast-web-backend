package com.example.helloworld.models;

import lombok.Value;

import java.util.List;


@Value
public class Invites {

    private List<String> emails;

    public static Invites from(final List<String> emails) {
        return new Invites(emails);
    }
}