package com.example.helloworld.controllers;

import com.example.helloworld.models.BillingSummary;
import com.example.helloworld.models.Jobs;
import com.example.helloworld.models.Message;
import com.example.helloworld.services.BillingService;
import com.example.helloworld.services.JobsService;
import com.example.helloworld.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
public class JobsController {

    private final JobsService jobsService;

    @GetMapping()
    public Jobs getJobs() {
        return jobsService.getJobs();
    }

    @DeleteMapping
    void deleteJob(String jobName, Integer version) {

    }
}
