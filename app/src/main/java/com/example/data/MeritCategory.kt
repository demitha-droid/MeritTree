package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.R

enum class MeritCategory(
    val title: String,
    val paliName: String,
    val description: String,
    val defaultDrawableRes: Int,
    val leafColor: Color,
    val accentColor: Color
) {
    DANA(
        title = "Generosity",
        paliName = "Dāna",
        description = "Giving, charity, offering alms, helping those in need",
        defaultDrawableRes = R.drawable.ic_merit_dana,
        leafColor = Color(0xFFF9A825), // Golden amber
        accentColor = Color(0xFFFFF176)
    ),
    SILA(
        title = "Virtue",
        paliName = "Sīla",
        description = "Moral conduct, harmlessness, honesty, right action",
        defaultDrawableRes = R.drawable.ic_merit_lotus,
        leafColor = Color(0xFF43A047), // Fresh vibrant emerald
        accentColor = Color(0xFFA5D6A7)
    ),
    BHAVANA(
        title = "Meditation",
        paliName = "Bhāvanā",
        description = "Mindfulness, contemplation, inner peace, chanting",
        defaultDrawableRes = R.drawable.ic_merit_meditation,
        leafColor = Color(0xFF00ACC1), // Cyan turquoise wisdom
        accentColor = Color(0xFF80DEEA)
    ),
    KINDNESS(
        title = "Loving-Kindness",
        paliName = "Mettā",
        description = "Caring for animals, comforting others, forgiveness",
        defaultDrawableRes = R.drawable.ic_merit_kindness,
        leafColor = Color(0xFFE91E63), // Compassion rose
        accentColor = Color(0xFFF48FB1)
    ),
    TEMPLE(
        title = "Devotion & Offering",
        paliName = "Pūjā",
        description = "Visiting sacred sites, lighting lamps, offering flowers",
        defaultDrawableRes = R.drawable.ic_merit_stupa,
        leafColor = Color(0xFF8E24AA), // Spiritual purple
        accentColor = Color(0xFFCE93D8)
    ),
    DEDICATION(
        title = "Sharing Merit",
        paliName = "Pattidāna",
        description = "Rejoicing in good, dedicating merits to all beings",
        defaultDrawableRes = R.drawable.ic_merit_water,
        leafColor = Color(0xFF1E88E5), // Blessing celestial blue
        accentColor = Color(0xFF90CAF9)
    );

    companion object {
        fun fromString(value: String): MeritCategory {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: DANA
        }
    }
}
