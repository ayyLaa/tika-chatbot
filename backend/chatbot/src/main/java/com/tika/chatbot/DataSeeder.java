package com.tika.chatbot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    // Şifre şifreleyiciyi (PasswordEncoder) sisteme dahil ediyoruz
    public DataSeeder(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String email = "admin@tika.gov.tr";
        // Şifreyi Java'nın kendi güvenlik formatına çeviriyoruz
        String encodedPassword = passwordEncoder.encode("admin123");

        // Hesap var mı diye bak
        String checkSql = "SELECT count(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, email);

        System.out.println("\n=========================================================");
        if (count != null && count > 0) {
            // Eğer hesap zaten varsa, şifresini zorla admin123 olarak güncelle
            String updateSql = "UPDATE users SET password_hash = ? WHERE email = ?";
            jdbcTemplate.update(updateSql, encodedPassword, email);
            System.out.println("✅ ADMIN HESABI ZATEN VARDI, SIFRE 'admin123' OLARAK GUNCELLEDI! ✅");
        } else {
            // Hesap yoksa sıfırdan oluştur
            String insertSql = "INSERT INTO users (full_name, username, email, password_hash, department, user_role, is_active, email_verified) " +
                               "VALUES ('Sistem Yöneticisi', 'admin', ?, ?, 'IT', 'admin', true, true)";
            jdbcTemplate.update(insertSql, email, encodedPassword);
            System.out.println("✅ ADMIN HESABI YOKTU, OLUSTURULDU VE SIFRE 'admin123' YAPILDI! ✅");
        }
        System.out.println("=========================================================\n");
    }
}