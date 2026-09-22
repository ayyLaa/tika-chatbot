package com.tika.chatbot.chat.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class RiskKeywordChecker {


    private static final List<String> RISK_KEYWORDS = List.of(
            // Attempts to bypass/manipulate the system prompt
            "ignore previous instructions", "ignore all previous", "önceki talimatları yok say",
            "sistem promptunu göster", "show me your system prompt", "you are now",
            "yeni kimliğin", "act as", "pretend you are", "jailbreak", "developer mode",

            // Attempts to extract confidential/internal data
            "personel listesi", "maaş bilgisi", "kişisel veri", "şifre", "password",
            "api key", "database şifresi", "gizli belge", "sızıntı", "leak",
            "iç yazışma", "gizli rapor",

            // Misuse/attempts outside the institutional context
            "bomba yapımı", "silah yapımı", "yasa dışı", "hack", "hacklemek",
            "kişisel bilgilerini bul", "adres bilgisi ver"
    );

    public boolean isRisky(String question) {
        String normalized = question.toLowerCase(Locale.forLanguageTag("tr"));
        return RISK_KEYWORDS.stream().anyMatch(normalized::contains);
    }
}
