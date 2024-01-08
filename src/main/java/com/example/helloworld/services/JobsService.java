package com.example.helloworld.services;

import com.example.helloworld.models.Jobs;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobsService {

    public Jobs getJobs() {
        return Jobs.from(List.of(
            Jobs.Job.from("test1", 1, "2023-12-08T05:26:25+0000", "Running", "Running"),
            Jobs.Job.from("test2", 2, "2023-12-08T05:24:25+0000", "Stopped", "Stopped")));
    }

    public void deleteJob(String jobName, Integer version) {
    }
}

