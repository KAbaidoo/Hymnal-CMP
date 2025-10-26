package com.kobby.hymnal.core.settings

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FontSettings(
    val fontFamily: String = "Onest",
    val fontSize: Float = 16f
)

class FontSettingsManager(private val settings: Settings) {
    
    private val _fontSettings = MutableStateFlow(
        FontSettings(
            fontFamily = settings.getString("font_family", "Onest"),
            fontSize = settings.getFloat("font_size", 16f)
        )
    )
    
    val fontSettings: StateFlow<FontSettings> = _fontSettings.asStateFlow()
    
    fun updateFontFamily(fontFamily: String) {
        settings.putString("font_family", fontFamily)
        _fontSettings.value = _fontSettings.value.copy(fontFamily = fontFamily)
    }
    
    fun updateFontSize(sizeChange: Float) {
        val currentSize = _fontSettings.value.fontSize
        val newSize = (currentSize + sizeChange).coerceIn(12f, 24f)
        settings.putFloat("font_size", newSize)
        _fontSettings.value = _fontSettings.value.copy(fontSize = newSize)
    }
    
}