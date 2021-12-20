package com.printtex.printtex_pos.service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class MorePaymentServiceImpl implements MorePaymentService {
    private final ModelMessageServiceImpl modelMessageService;

    public MorePaymentServiceImpl(ModelMessageServiceImpl modelMessageService) {
        this.modelMessageService = modelMessageService;
    }

    @Override
    public double getNewDue(Model model, double previousDue, double paidAmount) {
        double newDue;
        if (paidAmount > previousDue) {
            newDue = paidAmount - previousDue;
            modelMessageService.setModelMessage(model, "return", newDue);
            newDue = 0;
        } else {
            newDue = previousDue - paidAmount;
            modelMessageService.setModelMessage(model, "return", "0");
        }
        return newDue;
    }
}
