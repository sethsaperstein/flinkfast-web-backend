package com.example.helloworld.controllers;

import com.example.helloworld.models.CreateJobRequestBody;
import com.example.helloworld.models.DeleteJobRequestBody;
import com.example.helloworld.models.Jobs;
import com.example.helloworld.services.FlinkService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @DeleteMapping()
    void deleteJob(Principal user, @RequestBody DeleteJobRequestBody request) {
        flinkService.deleteJob(request.getName(), request.getVersion());
    }

    @PostMapping()
    public Jobs.Job createJob(Principal user, @RequestBody CreateJobRequestBody request) {
        return flinkService.createFlinkJobCluster(user.getName(), request.getSql());
    }
}
