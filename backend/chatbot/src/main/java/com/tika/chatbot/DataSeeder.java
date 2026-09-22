package com.tika.chatbot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    // We wire the password encoder (PasswordEncoder) into the system
    public DataSeeder(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String email = "admin@tika.gov.tr";
        // We convert the password to Java's own secure format
        String encodedPassword = passwordEncoder.encode("admin123");

        // Check whether the account exists
        String checkSql = "SELECT count(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, email);

        System.out.println("\n=========================================================");
        if (count != null && count > 0) {
            // If the account already exists, forcibly update its password to admin123
            String updateSql = "UPDATE users SET password_hash = ? WHERE email = ?";
            jdbcTemplate.update(updateSql, encodedPassword, email);
            System.out.println("✅ ADMIN ACCOUNT ALREADY EXISTED, PASSWORD UPDATED TO 'admin123'! ✅");
        } else {
            // If the account doesn't exist, create it from scratch
            String insertSql = "INSERT INTO users (full_name, username, email, password_hash, department, user_role, is_active, email_verified) " +
                               "VALUES ('Sistem Yöneticisi', 'admin', ?, ?, 'IT', 'admin', true, true)";
            jdbcTemplate.update(insertSql, email, encodedPassword);
            System.out.println("✅ ADMIN ACCOUNT DID NOT EXIST, CREATED AND PASSWORD SET TO 'admin123'! ✅");
        }
        System.out.println("=========================================================\n");
    }
}