package com.tika.chatbot.auth.dto;

import java.util.List;

public record AnalyticsResponse(
        Long monthlyTokens,
        Double avgResponseTimeSeconds,
        long activeUsers,
        Double satisfactionPct,
        List<DailyUsageDto> dailyUsage
) {}