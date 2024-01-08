package com.example.helloworld.models;

import lombok.Value;

import java.util.List;


@Value
public class Members {

    @Value
    public static class Member {
        private String email;
        private String name;

        public static Member from(final String email, final String name) {
            return new Member(email, name);
        }
    }

    private List<Member> members;

    public static Members from(final List<Member> members) {
        return new Members(members);
    }
}
