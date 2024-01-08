package com.example.helloworld.controllers;

import com.example.helloworld.models.BillingSummary;
import com.example.helloworld.models.Message;
import com.example.helloworld.services.BillingService;
import com.example.helloworld.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('read:admin-messages')")
    public BillingSummary getPublic() {
        return billingService.getBillingSummary();
    }
}
