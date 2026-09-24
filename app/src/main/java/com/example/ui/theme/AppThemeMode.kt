package com.example.ui.theme

enum class AppThemeMode(
    val title: String,
    val description: String
) {
    GREEN(
        title = "Green",
        description = "Sacred forest night and peaceful emerald Bodhi palette"
    ),
    LIGHT(
        title = "Light",
        description = "Calm ivory parchment and warm golden sunlight"
    ),
    AMOLED(
        title = "AMOLED",
        description = "Pure pitch black (#000000) with luminous copper & golden aura"
    );

    companion object {
        fun fromString(name: String?): AppThemeMode {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: GREEN
        }
    }
}
