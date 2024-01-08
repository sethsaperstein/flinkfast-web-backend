package com.example.helloworld.services;

import com.example.helloworld.models.BillingSummary;
import com.example.helloworld.models.Message;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BillingService {

    public BillingSummary getBillingSummary() {
        return BillingSummary.from(new BigDecimal("3024.00"), 15, 1020);
    }
}
