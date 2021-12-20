package com.printtex.printtex_pos.service;


public interface MailService {
    boolean sendMail(String email, String subject, String message);
}
