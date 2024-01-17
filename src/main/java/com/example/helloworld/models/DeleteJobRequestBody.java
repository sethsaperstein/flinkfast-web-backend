package com.example.helloworld.models;

import lombok.Data;

@Data
public class DeleteJobRequestBody {
    private String name;
    private Integer version;
}
