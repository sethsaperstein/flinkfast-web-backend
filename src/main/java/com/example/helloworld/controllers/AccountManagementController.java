package com.example.helloworld.controllers;

import com.example.helloworld.models.Invites;
import com.example.helloworld.models.Members;
import com.example.helloworld.services.AccountManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountManagementController {

    private final AccountManagementService accountManagementService;

    @GetMapping("/invites")
    @PreAuthorize("hasAuthority('read:admin-messages')")
    public Invites getInvites() {
        return accountManagementService.getInvites();
    }

    @DeleteMapping("/invites")
    @PreAuthorize("hasAuthority('read:admin-messages')")
    public void deleteInvite(String email) {
        accountManagementService.deleteInvite(email);
    }

    @PostMapping("/invites")
    @PreAuthorize("hasAuthority('read:admin-messages')")
    public void sendInvite(String email) {
        accountManagementService.sendInvite(email);
    }


    @GetMapping("/members")
    @PreAuthorize("hasAuthority('read:admin-messages')")
    public Members getMembers() {
        return accountManagementService.getMembers();
    }

    @DeleteMapping("/members")
    @PreAuthorize("hasAuthority('read:admin-messages')")
    public void deleteMember(String email) {
        accountManagementService.deleteMember(email);
    }
}