package com.maxrave.simpmusic.ui.ipod

import androidx.compose.ui.graphics.Color

enum class IpodFinish(
    val title: String,
    val bodyGradientTop: Color,
    val bodyGradientBottom: Color,
    val wheelColor: Color,
    val wheelTextColor: Color,
    val centerButtonColor: Color,
    val rimColor: Color
) {
    SILVER(
        title = "Silver Aluminum",
        bodyGradientTop = Color(0xFFE2E4E7),
        bodyGradientBottom = Color(0xFFB8BCBE),
        wheelColor = Color(0xFFF0F1F3),
        wheelTextColor = Color(0xFF6B7280),
        centerButtonColor = Color(0xFFE5E7EB),
        rimColor = Color(0xFF9CA3AF)
    ),
    STEALTH_BLACK(
        title = "Stealth Black",
        bodyGradientTop = Color(0xFF2C2D30),
        bodyGradientBottom = Color(0xFF161719),
        wheelColor = Color(0xFF232427),
        wheelTextColor = Color(0xFF9CA3AF),
        centerButtonColor = Color(0xFF1F2023),
        rimColor = Color(0xFF374151)
    ),
    U2_EDITION(
        title = "U2 Special Edition",
        bodyGradientTop = Color(0xFF1F1F21),
        bodyGradientBottom = Color(0xFF0D0D0E),
        wheelColor = Color(0xFFD32F2F),
        wheelTextColor = Color(0xFFFFFFFF),
        centerButtonColor = Color(0xFFB71C1C),
        rimColor = Color(0xFFE53935)
    ),
    RETRO_WHITE(
        title = "Retro 1st Gen White",
        bodyGradientTop = Color(0xFFFAFAFA),
        bodyGradientBottom = Color(0xFFE0E0E0),
        wheelColor = Color(0xFFEEEEEE),
        wheelTextColor = Color(0xFF757575),
        centerButtonColor = Color(0xFFE0E0E0),
        rimColor = Color(0xFFBDBDBD)
    )
}

enum class LcdTheme(
    val title: String,
    val headerBackground: Color,
    val headerTextColor: Color,
    val screenBackground: Color,
    val textColor: Color,
    val secondaryTextColor: Color,
    val highlightBackgroundTop: Color,
    val highlightBackgroundBottom: Color,
    val highlightTextColor: Color,
    val dividerColor: Color
) {
    CLASSIC_BLUE(
        title = "Classic Blue LCD",
        headerBackground = Color(0xFFB0C4DE),
        headerTextColor = Color(0xFF1E293B),
        screenBackground = Color(0xFFFFFFFF),
        textColor = Color(0xFF0F172A),
        secondaryTextColor = Color(0xFF64748B),
        highlightBackgroundTop = Color(0xFF38BDF8),
        highlightBackgroundBottom = Color(0xFF0284C7),
        highlightTextColor = Color(0xFFFFFFFF),
        dividerColor = Color(0xFFE2E8F0)
    ),
    MONOCHROME(
        title = "Retro Monochrome",
        headerBackground = Color(0xFF8B9B88),
        headerTextColor = Color(0xFF111827),
        screenBackground = Color(0xFF9EB09A),
        textColor = Color(0xFF111827),
        secondaryTextColor = Color(0xFF374151),
        highlightBackgroundTop = Color(0xFF2B3A28),
        highlightBackgroundBottom = Color(0xFF1C261A),
        highlightTextColor = Color(0xFF9EB09A),
        dividerColor = Color(0xFF7C8E79)
    ),
    OLED_DARK(
        title = "Modern OLED Dark",
        headerBackground = Color(0xFF1E1E24),
        headerTextColor = Color(0xFF38BDF8),
        screenBackground = Color(0xFF09090B),
        textColor = Color(0xFFF8FAFC),
        secondaryTextColor = Color(0xFF94A3B8),
        highlightBackgroundTop = Color(0xFF0EA5E9),
        highlightBackgroundBottom = Color(0xFF0284C7),
        highlightTextColor = Color(0xFFFFFFFF),
        dividerColor = Color(0xFF27272A)
    ),
    AMBER(
        title = "Retro Amber LCD",
        headerBackground = Color(0xFFD97706),
        headerTextColor = Color(0xFFFFFBEB),
        screenBackground = Color(0xFFFDE68A),
        textColor = Color(0xFF78350F),
        secondaryTextColor = Color(0xFF92400E),
        highlightBackgroundTop = Color(0xFFB45309),
        highlightBackgroundBottom = Color(0xFF78350F),
        highlightTextColor = Color(0xFFFEF3C7),
        dividerColor = Color(0xFFF59E0B)
    )
}
