package com.example.ui.theme

enum class AppThemeMode(
    val title: String,
    val description: String
) {
    BROWN(
        title = "Brown",
        description = "Sacred sandalwood, warm teak wood and golden monastery earth"
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
            if (name.equals("GREEN", ignoreCase = true)) return BROWN
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: BROWN
        }
    }
}
