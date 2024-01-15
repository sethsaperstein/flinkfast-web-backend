package com.example.helloworld.controllers;

import com.example.helloworld.models.Jobs;
import com.example.helloworld.services.FlinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
public class JobsController {

    private final FlinkService flinkService;

    @GetMapping()
    public Jobs getJobs() {
        return flinkService.getJobs();
    }

    @DeleteMapping
    void deleteJob(String jobName, Integer version) {

    }

    @PostMapping
    public Jobs.Job createJob(Principal user, String sql) {
        return flinkService.createJob(user.getName(), sql);
    }
}
