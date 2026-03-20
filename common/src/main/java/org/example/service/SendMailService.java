package org.example.service;

import jakarta.mail.MessagingException;

public interface SendMailService {

    void sendMail(String to, String subject, String content);

    void sendVerificationMailHtml(String to, String verifyCode) throws MessagingException;

}
