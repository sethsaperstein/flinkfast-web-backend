package com.example.helloworld.models;

import lombok.Value;

@Value
public class DeployedJob {
    private String jobId;

    public static DeployedJob from(
        final String jobId
    ) {
        return new DeployedJob(jobId);
    }
}
