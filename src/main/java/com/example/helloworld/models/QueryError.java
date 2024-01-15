package com.example.helloworld.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryError {
    private final String type = "error";
    private String message;

    static public QueryError from(String error) {
        return new QueryError(error);
    }
}
