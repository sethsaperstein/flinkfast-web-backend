package com.example.helloworld.models;

import lombok.Value;

import java.util.List;


@Value
public class Jobs {

    @Value
    public static class Job {
        String name;
        Integer version;
        String createdUTCISO;
        String state;
        String targetState;


        public static Job from(
            final String name,
            final Integer version,
            final String createdUTCISO,
            final String state,
            final String targetState) {
            return new Job(name, version, createdUTCISO, state, targetState);
        }
    }

    List<Job> jobs;

    public static Jobs from(final List<Job> jobs) {
        return new Jobs(jobs);
    }
}
