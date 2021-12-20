package com.printtex.printtex_pos.service;

import org.springframework.ui.Model;

public interface MorePaymentService {
    double getNewDue(Model model, double previousDue, double paidAmount);
}
