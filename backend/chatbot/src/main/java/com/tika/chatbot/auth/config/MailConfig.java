package com.tika.chatbot.auth.config; // Adjust the package if it differs

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        // Mailtrap settings
        mailSender.setHost("sandbox.smtp.mailtrap.io");
        mailSender.setPort(2525);
        mailSender.setUsername("3374666b54e1b2");
        mailSender.setPassword("3728e8fb09d0cd");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true"); // This will print sending details to the console

        return mailSender;
    }
}