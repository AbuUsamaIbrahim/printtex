package com.printtex.printtex_pos.service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class ModelMessageServiceImpl implements ModelMessageService {
    @Override
    public void setModelMessage(Model model, String attributeName, Object attributeValue) {
        model.addAttribute(attributeName, attributeValue);
    }

    @Override
    public void setEmptyResultMessage(Model model) {
        setModelMessage(model, "result", "");
    }
}
