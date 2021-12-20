package com.printtex.printtex_pos.service;

import org.springframework.ui.Model;

public interface ModelMessageService {
    void setModelMessage(Model model, String attributeName, Object attributeValue);

    void setEmptyResultMessage(Model model);
}
