package com.example.helloworld.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String text;
    private String to;

    static public Message from(String text, String to) {
        return new Message(text, to);
    }
}
