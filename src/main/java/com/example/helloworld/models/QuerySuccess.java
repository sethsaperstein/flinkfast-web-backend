package com.example.helloworld.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuerySuccess {
    private final String type = "success";
    private final String message = "SUCCESS.";
}
