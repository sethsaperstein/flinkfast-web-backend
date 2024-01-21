package com.example.helloworld.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;


@Value
public class Members {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Member {
        private String id;
        private String email;
        private String name;

        public static Member from(final String id, final String email, final String name) {
            return new Member(id, email, name);
        }
    }

    private List<Member> members;

    public static Members from(final List<Member> members) {
        return new Members(members);
    }
}
