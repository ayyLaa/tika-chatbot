package com.tika.chatbot.auth.service;

import com.tika.chatbot.auth.exception.EmailSendException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailException;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${mail.username}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendInviteEmail(String toEmail, String token) {
        String inviteLink = baseUrl + "/accept-invite?token=" + token;

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

        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new EmailSendException("Davetiye " + toEmail + " adresine gönderilemedi.");
        }
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
        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new EmailSendException("Davetiye " + toEmail + " adresine gönderilemedi.");
        }
    }

    public void sendChatShareEmail(String toEmail, String senderName, String link, String note) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("TİKA AI - Sohbet Paylaşımı");

        String body = "Sayın kullanıcı,\n\n" +
                senderName + " sizinle bir sohbet paylaştı.\n\n" +
                "Sohbeti görüntülemek için aşağıdaki bağlantıya tıklayınız:\n" +
                link + "\n\n";

        if (note != null && !note.trim().isEmpty()) {
            body += "Not: " + note + "\n\n";
        }

        message.setText(body);

        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.err.println("Error sending share email to " + toEmail + ": " + e.getMessage());
        }
    }
}