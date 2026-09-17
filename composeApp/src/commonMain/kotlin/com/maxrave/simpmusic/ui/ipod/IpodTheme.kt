package com.maxrave.simpmusic.ui.ipod

import androidx.compose.ui.graphics.Color

enum class HardwareFinish(val title: String) {
    GLOSSY_PLASTIC("Glossy Plastic"),
    PEARL_SATIN("Polished Pearl"),
    MATTE("Matte Finish"),
    BRUSHED_ALUMINUM("Brushed Aluminum"),
    POLISHED_METAL("Polished Metal"),
    TRANSPARENT("Transparent Glass")
}

enum class ThemeCategory(val title: String) {
    FASHION("Fashion Aesthetics"),
    FUNKY("Funky & Y2K"),
    PASTEL("Pastel Collection"),
    CLASSIC("Classic Hardware"),
    CYBER_NEON("Cyberpunk & Neon"),
    RETRO_NOSTALGIA("Retro 80s & 90s"),
    LUXURY("Luxury & Modern")
}

data class IpodHardwareTheme(
    val id: String,
    val name: String,
    val category: ThemeCategory,
    val bodyColor: Color,
    val bodyGradientBottom: Color? = null,
    val wheelColor: Color,
    val wheelTextColor: Color,
    val centerButtonColor: Color,
    val bezelColor: Color = Color(0xFF101012),
    val accentColor: Color = Color(0xFFFF1493),
    val finishStyle: HardwareFinish = HardwareFinish.GLOSSY_PLASTIC
)

object IpodPresets {

    // --- RETROPOD EXCLUSIVES & CLASSIC ---
    val RETROPOD_U2_EDITION = IpodHardwareTheme(
        id = "retropod_u2_edition",
        name = "RetroPod U2 Edition",
        category = ThemeCategory.CLASSIC,
        bodyColor = Color(0xFF101012),
        bodyGradientBottom = Color(0xFF000000),
        wheelColor = Color(0xFFB30000),
        wheelTextColor = Color(0xFFE5E5E5),
        centerButtonColor = Color(0xFF18181B),
        bezelColor = Color(0xFF000000),
        accentColor = Color(0xFFFF0000),
        finishStyle = HardwareFinish.MATTE
    )

    val RETRO_1ST_GEN = IpodHardwareTheme(
        id = "retro_1st_gen",
        name = "Original 2001 Scroll Wheel",
        category = ThemeCategory.RETRO_NOSTALGIA,
        bodyColor = Color(0xFFF8FAFC),
        bodyGradientBottom = Color(0xFFE2E8F0),
        wheelColor = Color(0xFFCBD5E1),
        wheelTextColor = Color(0xFF334155),
        centerButtonColor = Color(0xFFE2E8F0),
        bezelColor = Color(0xFF0F172A),
        accentColor = Color(0xFF0EA5E9),
        finishStyle = HardwareFinish.PEARL_SATIN
    )

    val PRODUCT_RED = IpodHardwareTheme(
        id = "product_red",
        name = "Special Edition PRODUCT (RED)",
        category = ThemeCategory.CLASSIC,
        bodyColor = Color(0xFFDC2626),
        bodyGradientBottom = Color(0xFF991B1B),
        wheelColor = Color(0xFF18181B),
        wheelTextColor = Color(0xFFF472B6),
        centerButtonColor = Color(0xFFB91C1C),
        bezelColor = Color(0xFF09090B),
        accentColor = Color(0xFFEF4444),
        finishStyle = HardwareFinish.BRUSHED_ALUMINUM
    )

    val SILVER = IpodHardwareTheme(
        id = "classic_silver",
        name = "Silver Aluminum",
        category = ThemeCategory.CLASSIC,
        bodyColor = Color(0xFFD8DCE0),
        bodyGradientBottom = Color(0xFFB5B9BE),
        wheelColor = Color(0xFFEBF0F3),
        wheelTextColor = Color(0xFF4B5563),
        centerButtonColor = Color(0xFFD3D7DC),
        bezelColor = Color(0xFF18181B),
        accentColor = Color(0xFF0284C7),
        finishStyle = HardwareFinish.BRUSHED_ALUMINUM
    )

    val BLACK = IpodHardwareTheme(
        id = "classic_black",
        name = "Stealth Black",
        category = ThemeCategory.CLASSIC,
        bodyColor = Color(0xFF1C1D21),
        bodyGradientBottom = Color(0xFF111215),
        wheelColor = Color(0xFF28292E),
        wheelTextColor = Color(0xFF9CA3AF),
        centerButtonColor = Color(0xFF202125),
        bezelColor = Color(0xFF090A0C),
        accentColor = Color(0xFF38BDF8),
        finishStyle = HardwareFinish.MATTE
    )

    // --- CYBERPUNK & NEON ---
    val CYBERPUNK_2077 = IpodHardwareTheme(
        id = "cyber_neon_2077",
        name = "Neon Cyberpunk 2077",
        category = ThemeCategory.CYBER_NEON,
        bodyColor = Color(0xFF0F172A),
        bodyGradientBottom = Color(0xFF020617),
        wheelColor = Color(0xFF06B6D4),
        wheelTextColor = Color(0xFF020617),
        centerButtonColor = Color(0xFFF43F5E),
        bezelColor = Color(0xFF020617),
        accentColor = Color(0xFF38BDF8),
        finishStyle = HardwareFinish.POLISHED_METAL
    )

    val SYNTHWAVE_80S = IpodHardwareTheme(
        id = "synthwave_80s",
        name = "80s Synthwave Sunset",
        category = ThemeCategory.CYBER_NEON,
        bodyColor = Color(0xFF5B21B6),
        bodyGradientBottom = Color(0xFF9D174D),
        wheelColor = Color(0xFFFDE047),
        wheelTextColor = Color(0xFF831843),
        centerButtonColor = Color(0xFFF472B6),
        bezelColor = Color(0xFF1E1B4B),
        accentColor = Color(0xFFF43F5E),
        finishStyle = HardwareFinish.GLOSSY_PLASTIC
    )

    val Y2K_ATOMIC_PURPLE = IpodHardwareTheme(
        id = "y2k_atomic_purple",
        name = "Atomic Purple Translucent",
        category = ThemeCategory.RETRO_NOSTALGIA,
        bodyColor = Color(0xFF7E22CE),
        bodyGradientBottom = Color(0xFF581C87),
        wheelColor = Color(0xFF3B0764),
        wheelTextColor = Color(0xFFE9D5FF),
        centerButtonColor = Color(0xFFA855F7),
        bezelColor = Color(0xFF1E1B4B),
        accentColor = Color(0xFFC084FC),
        finishStyle = HardwareFinish.TRANSPARENT
    )

    // --- LUXURY & MODERN ---
    val CHAMPAGNE_GOLD = IpodHardwareTheme(
        id = "luxury_champagne_gold",
        name = "Champagne Gold Edition",
        category = ThemeCategory.LUXURY,
        bodyColor = Color(0xFFD97706),
        bodyGradientBottom = Color(0xFFB45309),
        wheelColor = Color(0xFFFEF3C7),
        wheelTextColor = Color(0xFF78350F),
        centerButtonColor = Color(0xFFF59E0B),
        bezelColor = Color(0xFF1C1917),
        accentColor = Color(0xFFF59E0B),
        finishStyle = HardwareFinish.POLISHED_METAL
    )

    val SPACE_BLACK_TITANIUM = IpodHardwareTheme(
        id = "space_black_titanium",
        name = "Space Black Titanium",
        category = ThemeCategory.LUXURY,
        bodyColor = Color(0xFF27272A),
        bodyGradientBottom = Color(0xFF18181B),
        wheelColor = Color(0xFF3F3F46),
        wheelTextColor = Color(0xFFE4E4E7),
        centerButtonColor = Color(0xFF27272A),
        bezelColor = Color(0xFF09090B),
        accentColor = Color(0xFF60A5FA),
        finishStyle = HardwareFinish.BRUSHED_ALUMINUM
    )

    val EMERALD_MONSTERA = IpodHardwareTheme(
        id = "emerald_monstera",
        name = "Forest Emerald & Gold",
        category = ThemeCategory.LUXURY,
        bodyColor = Color(0xFF064E3B),
        bodyGradientBottom = Color(0xFF022C22),
        wheelColor = Color(0xFFFEF3C7),
        wheelTextColor = Color(0xFF064E3B),
        centerButtonColor = Color(0xFFFDE68A),
        bezelColor = Color(0xFF022C22),
        accentColor = Color(0xFF10B981),
        finishStyle = HardwareFinish.PEARL_SATIN
    )

    val MIDNIGHT_AMETHYST = IpodHardwareTheme(
        id = "midnight_amethyst",
        name = "Midnight Amethyst",
        category = ThemeCategory.LUXURY,
        bodyColor = Color(0xFF3B0764),
        bodyGradientBottom = Color(0xFF1E1B4B),
        wheelColor = Color(0xFFEDE9FE),
        wheelTextColor = Color(0xFF4C1D95),
        centerButtonColor = Color(0xFF7C3AED),
        bezelColor = Color(0xFF0F172A),
        accentColor = Color(0xFFA855F7),
        finishStyle = HardwareFinish.POLISHED_METAL
    )

    val PACIFIC_TEAL = IpodHardwareTheme(
        id = "pacific_teal",
        name = "Pacific Teal & Ice",
        category = ThemeCategory.LUXURY,
        bodyColor = Color(0xFF0F766E),
        bodyGradientBottom = Color(0xFF115E59),
        wheelColor = Color(0xFFE0F2FE),
        wheelTextColor = Color(0xFF0F766E),
        centerButtonColor = Color(0xFF99F6E4),
        bezelColor = Color(0xFF042F2E),
        accentColor = Color(0xFF14B8A6),
        finishStyle = HardwareFinish.BRUSHED_ALUMINUM
    )

    // --- FASHION & AESTHETIC ---
    val FASHION_BARBIE = IpodHardwareTheme(
        id = "fashion_barbie",
        name = "Glam Barbie Pink",
        category = ThemeCategory.FASHION,
        bodyColor = Color(0xFFFF007F),
        bodyGradientBottom = Color(0xFFC71585),
        wheelColor = Color(0xFFFFC0CB),
        wheelTextColor = Color(0xFF8B008B),
        centerButtonColor = Color(0xFFFFFFFF),
        bezelColor = Color(0xFF4A0033),
        accentColor = Color(0xFFFF007F),
        finishStyle = HardwareFinish.GLOSSY_PLASTIC
    )

    val AESTHETIC_SWAN_BARBIE = IpodHardwareTheme(
        id = "aesthetic_swan_barbie",
        name = "Barbie of Swan Lake",
        category = ThemeCategory.FASHION,
        bodyColor = Color(0xFFE0F2FE),
        bodyGradientBottom = Color(0xFFFCE7F3),
        wheelColor = Color(0xFFFFFFFF),
        wheelTextColor = Color(0xFF0284C7),
        centerButtonColor = Color(0xFFF0F9FF),
        bezelColor = Color(0xFF0C4A6E),
        accentColor = Color(0xFFEC4899),
        finishStyle = HardwareFinish.PEARL_SATIN
    )

    val AESTHETIC_LANA = IpodHardwareTheme(
        id = "aesthetic_lana",
        name = "Lana Del Rey Vintage",
        category = ThemeCategory.FASHION,
        bodyColor = Color(0xFF581825),
        bodyGradientBottom = Color(0xFF2C0B12),
        wheelColor = Color(0xFFE2D4C9),
        wheelTextColor = Color(0xFF581825),
        centerButtonColor = Color(0xFFF5EBE6),
        bezelColor = Color(0xFF1F050A),
        accentColor = Color(0xFFC97A7E),
        finishStyle = HardwareFinish.MATTE
    )

    val FASHION_SWAN_LAKE = IpodHardwareTheme(
        id = "fashion_swan_lake",
        name = "Swan Pearl",
        category = ThemeCategory.FASHION,
        bodyColor = Color(0xFFFBFBFA),
        bodyGradientBottom = Color(0xFFE2E8F0),
        wheelColor = Color(0xFFFFFFFF),
        wheelTextColor = Color(0xFF475569),
        centerButtonColor = Color(0xFFF1F5F9),
        bezelColor = Color(0xFF1E293B),
        accentColor = Color(0xFF64748B),
        finishStyle = HardwareFinish.PEARL_SATIN
    )

    val FASHION_CHERRY = IpodHardwareTheme(
        id = "fashion_cherry",
        name = "Glossy Cherry",
        category = ThemeCategory.FASHION,
        bodyColor = Color(0xFFB91C1C),
        bodyGradientBottom = Color(0xFF7F1D1D),
        wheelColor = Color(0xFFFFFFFF),
        wheelTextColor = Color(0xFF991B1B),
        centerButtonColor = Color(0xFFFCA5A5),
        bezelColor = Color(0xFF450A0A),
        accentColor = Color(0xFFDC2626),
        finishStyle = HardwareFinish.GLOSSY_PLASTIC
    )

    // --- FUNKY & Y2K ---
    val FUNKY_ACID = IpodHardwareTheme(
        id = "funky_acid",
        name = "Y2K Acid Lime",
        category = ThemeCategory.FUNKY,
        bodyColor = Color(0xFFA3E635),
        bodyGradientBottom = Color(0xFF65A30D),
        wheelColor = Color(0xFFE9D5FF),
        wheelTextColor = Color(0xFF581C87),
        centerButtonColor = Color(0xFFF3E8FF),
        bezelColor = Color(0xFF1A2E05),
        accentColor = Color(0xFFA855F7),
        finishStyle = HardwareFinish.GLOSSY_PLASTIC
    )

    val FUNKY_COTTON_CANDY = IpodHardwareTheme(
        id = "funky_cotton_candy",
        name = "Cotton Candy Dream",
        category = ThemeCategory.FUNKY,
        bodyColor = Color(0xFFF472B6),
        bodyGradientBottom = Color(0xFF38BDF8),
        wheelColor = Color(0xFFF8FAFC),
        wheelTextColor = Color(0xFF7E22CE),
        centerButtonColor = Color(0xFFF0F9FF),
        bezelColor = Color(0xFF312E81),
        accentColor = Color(0xFFEC4899),
        finishStyle = HardwareFinish.PEARL_SATIN
    )

    val Y2K_AQUA = IpodHardwareTheme(
        id = "y2k_aqua",
        name = "Y2K Cyber Aqua",
        category = ThemeCategory.FUNKY,
        bodyColor = Color(0xFF06B6D4),
        bodyGradientBottom = Color(0xFF0891B2),
        wheelColor = Color(0xFFE0F2FE),
        wheelTextColor = Color(0xFF0369A1),
        centerButtonColor = Color(0xFFBAE6FD),
        bezelColor = Color(0xFF0C4A6E),
        accentColor = Color(0xFF0284C7),
        finishStyle = HardwareFinish.TRANSPARENT
    )

    // --- PASTEL ---
    val PASTEL_STRAWBERRY = IpodHardwareTheme(
        id = "pastel_strawberry",
        name = "Strawberry Milk",
        category = ThemeCategory.PASTEL,
        bodyColor = Color(0xFFFBCFE8),
        bodyGradientBottom = Color(0xFFF472B6),
        wheelColor = Color(0xFFFFF1F2),
        wheelTextColor = Color(0xFF9D174D),
        centerButtonColor = Color(0xFFFCE7F3),
        bezelColor = Color(0xFF500724),
        accentColor = Color(0xFFDB2777),
        finishStyle = HardwareFinish.PEARL_SATIN
    )

    val PASTEL_MATCHA = IpodHardwareTheme(
        id = "pastel_matcha",
        name = "Matcha Latte",
        category = ThemeCategory.PASTEL,
        bodyColor = Color(0xFFBEF264),
        bodyGradientBottom = Color(0xFF84CC16),
        wheelColor = Color(0xFFFEF08A),
        wheelTextColor = Color(0xFF3F6212),
        centerButtonColor = Color(0xFFFEF9C3),
        bezelColor = Color(0xFF14532D),
        accentColor = Color(0xFF65A30D),
        finishStyle = HardwareFinish.PEARL_SATIN
    )

    val PASTEL_LAVENDER = IpodHardwareTheme(
        id = "pastel_lavender",
        name = "Lavender Dream",
        category = ThemeCategory.PASTEL,
        bodyColor = Color(0xFFDDD6FE),
        bodyGradientBottom = Color(0xFFC4B5FD),
        wheelColor = Color(0xFFF5F3FF),
        wheelTextColor = Color(0xFF5B21B6),
        centerButtonColor = Color(0xFFEDE9FE),
        bezelColor = Color(0xFF2E1065),
        accentColor = Color(0xFF7C3AED),
        finishStyle = HardwareFinish.PEARL_SATIN
    )

    val PASTEL_MILK_TEA = IpodHardwareTheme(
        id = "pastel_milk_tea",
        name = "Milk Tea Nude",
        category = ThemeCategory.PASTEL,
        bodyColor = Color(0xFFE7D5C7),
        bodyGradientBottom = Color(0xFFD0B8A8),
        wheelColor = Color(0xFFFDF8F5),
        wheelTextColor = Color(0xFF5C3D2E),
        centerButtonColor = Color(0xFFF8EEDD),
        bezelColor = Color(0xFF2C1912),
        accentColor = Color(0xFF8D5B4C),
        finishStyle = HardwareFinish.PEARL_SATIN
    )

    val ALL_PRESETS = listOf(
        RETROPOD_U2_EDITION,
        RETRO_1ST_GEN,
        PRODUCT_RED,
        SILVER,
        BLACK,
        CYBERPUNK_2077,
        SYNTHWAVE_80S,
        Y2K_ATOMIC_PURPLE,
        CHAMPAGNE_GOLD,
        SPACE_BLACK_TITANIUM,
        EMERALD_MONSTERA,
        MIDNIGHT_AMETHYST,
        PACIFIC_TEAL,
        FASHION_BARBIE,
        AESTHETIC_SWAN_BARBIE,
        AESTHETIC_LANA,
        FASHION_SWAN_LAKE,
        FASHION_CHERRY,
        FUNKY_ACID,
        FUNKY_COTTON_CANDY,
        Y2K_AQUA,
        PASTEL_STRAWBERRY,
        PASTEL_MATCHA,
        PASTEL_LAVENDER,
        PASTEL_MILK_TEA
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
    RETRO_MONOCHROME(
        title = "2001 Monochrome LCD",
        headerBackground = Color(0xFFC2C9AD),
        headerTextColor = Color(0xFF1F2417),
        screenBackground = Color(0xFFD3DAC2),
        textColor = Color(0xFF1A2012),
        secondaryTextColor = Color(0xFF424C34),
        highlightBackgroundTop = Color(0xFF1A2012),
        highlightBackgroundBottom = Color(0xFF2C3520),
        highlightTextColor = Color(0xFFD3DAC2),
        dividerColor = Color(0xFFB8C0A4)
    ),
    GLAM_BARBIE(
        title = "Glam Barbie Pink",
        headerBackground = Color(0xFFFFB6C1),
        headerTextColor = Color(0xFF8B008B),
        screenBackground = Color(0xFFFFF0F5),
        textColor = Color(0xFF4A0033),
        secondaryTextColor = Color(0xFF912368),
        highlightBackgroundTop = Color(0xFFFF69B4),
        highlightBackgroundBottom = Color(0xFFFF1493),
        highlightTextColor = Color(0xFFFFFFFF),
        dividerColor = Color(0xFFFFC0CB)
    ),
    SWAN_BARBIE(
        title = "Barbie Swan Lake",
        headerBackground = Color(0xFFBAE6FD),
        headerTextColor = Color(0xFF0369A1),
        screenBackground = Color(0xFFF0F9FF),
        textColor = Color(0xFF0C4A6E),
        secondaryTextColor = Color(0xFF0284C7),
        highlightBackgroundTop = Color(0xFF38BDF8),
        highlightBackgroundBottom = Color(0xFFF472B6),
        highlightTextColor = Color(0xFFFFFFFF),
        dividerColor = Color(0xFFE0F2FE)
    ),
    LANA_VINTAGE(
        title = "Lana Del Rey Vintage",
        headerBackground = Color(0xFFD5C3B8),
        headerTextColor = Color(0xFF3D1219),
        screenBackground = Color(0xFFFAF5F0),
        textColor = Color(0xFF3D1219),
        secondaryTextColor = Color(0xFF7A4A43),
        highlightBackgroundTop = Color(0xFF8C3843),
        highlightBackgroundBottom = Color(0xFF581825),
        highlightTextColor = Color(0xFFF5EBE6),
        dividerColor = Color(0xFFE5D7CE)
    ),
    AMBER_GLOW_80S(
        title = "1980s Amber CRT",
        headerBackground = Color(0xFF3D2000),
        headerTextColor = Color(0xFFFFB000),
        screenBackground = Color(0xFF1A0D00),
        textColor = Color(0xFFFFB000),
        secondaryTextColor = Color(0xFFCC8800),
        highlightBackgroundTop = Color(0xFFFFB000),
        highlightBackgroundBottom = Color(0xFFD99400),
        highlightTextColor = Color(0xFF1A0D00),
        dividerColor = Color(0xFF4D2800)
    ),
    EMERALD_MATRIX(
        title = "Hacker Emerald Matrix",
        headerBackground = Color(0xFF022C22),
        headerTextColor = Color(0xFF4ADE80),
        screenBackground = Color(0xFF011711),
        textColor = Color(0xFF22C55E),
        secondaryTextColor = Color(0xFF15803D),
        highlightBackgroundTop = Color(0xFF22C55E),
        highlightBackgroundBottom = Color(0xFF16A34A),
        highlightTextColor = Color(0xFF011711),
        dividerColor = Color(0xFF065F46)
    ),
    SYNTHWAVE_NEON(
        title = "Synthwave Sunset LCD",
        headerBackground = Color(0xFF3B0764),
        headerTextColor = Color(0xFFFDE047),
        screenBackground = Color(0xFF18042B),
        textColor = Color(0xFFF472B6),
        secondaryTextColor = Color(0xFFC084FC),
        highlightBackgroundTop = Color(0xFFF43F5E),
        highlightBackgroundBottom = Color(0xFFD946EF),
        highlightTextColor = Color(0xFFFFFFFF),
        dividerColor = Color(0xFF581C87)
    ),
    CYBER_ACID(
        title = "Y2K Cyber Acid",
        headerBackground = Color(0xFFBEF264),
        headerTextColor = Color(0xFF14532D),
        screenBackground = Color(0xFF022C22),
        textColor = Color(0xFFA3E635),
        secondaryTextColor = Color(0xFF4ADE80),
        highlightBackgroundTop = Color(0xFF84CC16),
        highlightBackgroundBottom = Color(0xFF65A30D),
        highlightTextColor = Color(0xFF022C22),
        dividerColor = Color(0xFF15803D)
    ),
    AQUA_MATRIX(
        title = "Y2K Cyber Aqua LCD",
        headerBackground = Color(0xFF083344),
        headerTextColor = Color(0xFF67E8F9),
        screenBackground = Color(0xFF021824),
        textColor = Color(0xFF22D3EE),
        secondaryTextColor = Color(0xFF0284C7),
        highlightBackgroundTop = Color(0xFF06B6D4),
        highlightBackgroundBottom = Color(0xFF0284C7),
        highlightTextColor = Color(0xFF021824),
        dividerColor = Color(0xFF0E7490)
    ),
    COTTON_CANDY(
        title = "Cotton Candy Dream",
        headerBackground = Color(0xFFBAE6FD),
        headerTextColor = Color(0xFF6B21A8),
        screenBackground = Color(0xFFFAF5FF),
        textColor = Color(0xFF3B0764),
        secondaryTextColor = Color(0xFF7E22CE),
        highlightBackgroundTop = Color(0xFFF472B6),
        highlightBackgroundBottom = Color(0xFFEC4899),
        highlightTextColor = Color(0xFFFFFFFF),
        dividerColor = Color(0xFFE9D5FF)
    ),
    SWAN_PEARL(
        title = "Swan Pearl",
        headerBackground = Color(0xFFE2E8F0),
        headerTextColor = Color(0xFF334155),
        screenBackground = Color(0xFFF8FAFC),
        textColor = Color(0xFF1E293B),
        secondaryTextColor = Color(0xFF64748B),
        highlightBackgroundTop = Color(0xFF94A3B8),
        highlightBackgroundBottom = Color(0xFF64748B),
        highlightTextColor = Color(0xFFFFFFFF),
        dividerColor = Color(0xFFE2E8F0)
    ),
    CLASSIC_BLUE(
        title = "Classic Blue LCD",
        headerBackground = Color(0xFFD2DCED),
        headerTextColor = Color(0xFF0F172A),
        screenBackground = Color(0xFFFFFFFF),
        textColor = Color(0xFF0F172A),
        secondaryTextColor = Color(0xFF64748B),
        highlightBackgroundTop = Color(0xFF38BDF8),
        highlightBackgroundBottom = Color(0xFF0284C7),
        highlightTextColor = Color(0xFFFFFFFF),
        dividerColor = Color(0xFFE2E8F0)
    ),
    VINTAGE_PARCHMENT(
        title = "Vintage Parchment E-Ink",
        headerBackground = Color(0xFFE7E0D3),
        headerTextColor = Color(0xFF26231F),
        screenBackground = Color(0xFFF5F0E6),
        textColor = Color(0xFF26231F),
        secondaryTextColor = Color(0xFF6B5F52),
        highlightBackgroundTop = Color(0xFF4A4237),
        highlightBackgroundBottom = Color(0xFF332D25),
        highlightTextColor = Color(0xFFF5F0E6),
        dividerColor = Color(0xFFD9D0C1)
    ),
    MIDNIGHT_AMETHYST_LCD(
        title = "Midnight Amethyst LCD",
        headerBackground = Color(0xFF2E1065),
        headerTextColor = Color(0xFFE9D5FF),
        screenBackground = Color(0xFF0F0728),
        textColor = Color(0xFFDDD6FE),
        secondaryTextColor = Color(0xFFA78BFA),
        highlightBackgroundTop = Color(0xFF8B5CF6),
        highlightBackgroundBottom = Color(0xFF6D28D9),
        highlightTextColor = Color(0xFFFFFFFF),
        dividerColor = Color(0xFF3B0764)
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
    )
}
