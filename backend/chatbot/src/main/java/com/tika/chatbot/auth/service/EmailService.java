package com.tika.chatbot.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendInviteEmail(String toEmail, String token) {
        String inviteLink = baseUrl + "/accept-invite?token=" + token;  // frontend ruta, ne backend

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("Kayıt Daveti - Dahili Asistan");
        message.setText(
                "Sayın kullanıcı,\n\n" +
                        "TİKA dahili asistan sistemine davet edildiniz. Hesabınızı oluşturmak için aşağıdaki bağlantıya tıklayınız:\n\n" +
                        inviteLink + "\n\n" +
                        "Bağlantının geçerlilik süresi 7 gündür.\n\n" +
                        "Bu daveti siz talep etmediyseniz, lütfen sistem yöneticinizle iletişime geçiniz."
        );
        mailSender.send(message);
    }
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetLink = baseUrl + "/auth/reset-password?token=" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("Şifre Sıfırlama");
        message.setText(
                "Sayın kullanıcı,\n\n" +
                        "Şifre sıfırlama talebinde bulunuldu. Aşağıdaki bağlantıya tıklayınız:\n\n" +
                        resetLink + "\n\n" +
                        "Bağlantının geçerlilik süresi 1 saattir.\n\n" +
                        "Eğer bu talebi siz yapmadıysanız, bu e-postayı dikkate almayınız."
        );
        mailSender.send(message);
    }
}