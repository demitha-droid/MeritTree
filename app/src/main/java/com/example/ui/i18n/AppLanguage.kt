package com.example.ui.i18n

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String
) {
    ENGLISH("en", "English", "English"),
    SINHALA("si", "Sinhala", "සිංහල");

    companion object {
        fun fromString(value: String?): AppLanguage {
            return entries.firstOrNull {
                it.name.equals(value, ignoreCase = true) || it.code.equals(value, ignoreCase = true)
            } ?: ENGLISH
        }
    }
}
