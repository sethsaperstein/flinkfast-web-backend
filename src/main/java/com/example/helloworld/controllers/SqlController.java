package com.example.helloworld.controllers;

import com.example.helloworld.models.DeployedJob;
import com.example.helloworld.models.Session;
import com.example.helloworld.services.FlinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sql")
public class SqlController {

    private final FlinkService flinkService;

    @PostMapping("/session")
    public Session createSession(Principal user) {
        return flinkService.createFlinkSessionCluster(getSessionName(user));
    }

    @GetMapping("/session")
    public ResponseEntity<Session> getSession(Principal user) {
        Optional<Session> session = flinkService.getSession(getSessionName(user));
        if (session.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(session.get());
    }

    private String getSessionName(Principal user) {
        return user.getName().replaceAll("[^a-zA-Z0-9]", "");
    }
}
