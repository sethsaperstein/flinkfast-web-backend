package com.example.helloworld.controllers;

import com.auth0.exception.Auth0Exception;
import com.example.helloworld.models.Invites;
import com.example.helloworld.models.Members;
import com.example.helloworld.services.AccountManagementService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountManagementController {

    private final AccountManagementService accountManagementService;

    @PutMapping("/members")
    @PreAuthorize("hasAuthority('read:admin-messages')")
    public void approveMember(@RequestBody Members.Member member) throws Auth0Exception {
        accountManagementService.approveMember(member.getId());
    }

    @GetMapping("/pending-members")
    @PreAuthorize("hasAuthority('read:admin-messages')")
    public Members getPendingMembers() throws Auth0Exception {
        return accountManagementService.getPendingMembers();
    }


    @GetMapping("/members")
    @PreAuthorize("hasAuthority('read:admin-messages')")
    public Members getMembers() throws Auth0Exception {
        return accountManagementService.getMembers();
    }

    @DeleteMapping("/members")
    @PreAuthorize("hasAuthority('read:admin-messages')")
    public void deleteMember(@RequestBody Members.Member member) throws Auth0Exception {
        accountManagementService.deleteMember(member.getId());
    }
}