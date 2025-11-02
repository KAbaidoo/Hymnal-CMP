package com.kobby.hymnal.core.settings

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FontSettingsManagerTest {

    private fun createTestSettings(): MapSettings {
        return MapSettings()
    }

    @Test
    fun `initial font settings have default values`() = runTest {
        // Given
        val settings = createTestSettings()
        val fontSettingsManager = FontSettingsManager(settings)

        // When
        val fontSettings = fontSettingsManager.fontSettings.first()

        // Then
        assertEquals("Onest", fontSettings.fontFamily)
        assertEquals(16f, fontSettings.fontSize)
    }

    @Test
    fun `initial font settings load from existing preferences`() = runTest {
        // Given
        val settings = createTestSettings()
        settings.putString("font_family", "CustomFont")
        settings.putFloat("font_size", 20f)
        val fontSettingsManager = FontSettingsManager(settings)

        // When
        val fontSettings = fontSettingsManager.fontSettings.first()

        // Then
        assertEquals("CustomFont", fontSettings.fontFamily)
        assertEquals(20f, fontSettings.fontSize)
    }

    @Test
    fun `updateFontFamily changes font family`() = runTest {
        // Given
        val settings = createTestSettings()
        val fontSettingsManager = FontSettingsManager(settings)

        // When
        fontSettingsManager.updateFontFamily("NewFont")
        val fontSettings = fontSettingsManager.fontSettings.first()

        // Then
        assertEquals("NewFont", fontSettings.fontFamily)
        assertEquals("NewFont", settings.getString("font_family", ""))
    }

    @Test
    fun `updateFontFamily persists to settings`() = runTest {
        // Given
        val settings = createTestSettings()
        val fontSettingsManager = FontSettingsManager(settings)

        // When
        fontSettingsManager.updateFontFamily("Arial")

        // Then
        assertEquals("Arial", settings.getString("font_family", ""))
    }

    @Test
    fun `updateFontSize increases font size`() = runTest {
        // Given
        val settings = createTestSettings()
        val fontSettingsManager = FontSettingsManager(settings)

        // When
        fontSettingsManager.updateFontSize(2f)
        val fontSettings = fontSettingsManager.fontSettings.first()

        // Then
        assertEquals(18f, fontSettings.fontSize)
    }

    @Test
    fun `updateFontSize decreases font size`() = runTest {
        // Given
        val settings = createTestSettings()
        val fontSettingsManager = FontSettingsManager(settings)

        // When
        fontSettingsManager.updateFontSize(-2f)
        val fontSettings = fontSettingsManager.fontSettings.first()

        // Then
        assertEquals(14f, fontSettings.fontSize)
    }

    @Test
    fun `updateFontSize respects minimum limit of 12f`() = runTest {
        // Given
        val settings = createTestSettings()
        val fontSettingsManager = FontSettingsManager(settings)

        // When
        fontSettingsManager.updateFontSize(-10f)
        val fontSettings = fontSettingsManager.fontSettings.first()

        // Then
        assertEquals(12f, fontSettings.fontSize)
    }

    @Test
    fun `updateFontSize respects maximum limit of 24f`() = runTest {
        // Given
        val settings = createTestSettings()
        val fontSettingsManager = FontSettingsManager(settings)

        // When
        fontSettingsManager.updateFontSize(10f)
        val fontSettings = fontSettingsManager.fontSettings.first()

        // Then
        assertEquals(24f, fontSettings.fontSize)
    }

    @Test
    fun `updateFontSize persists to settings`() = runTest {
        // Given
        val settings = createTestSettings()
        val fontSettingsManager = FontSettingsManager(settings)

        // When
        fontSettingsManager.updateFontSize(4f)

        // Then
        assertEquals(20f, settings.getFloat("font_size", 0f))
    }

    @Test
    fun `multiple updateFontSize calls accumulate correctly`() = runTest {
        // Given
        val settings = createTestSettings()
        val fontSettingsManager = FontSettingsManager(settings)

        // When
        fontSettingsManager.updateFontSize(2f)
        fontSettingsManager.updateFontSize(2f)
        val fontSettings = fontSettingsManager.fontSettings.first()

        // Then
        assertEquals(20f, fontSettings.fontSize)
    }

    @Test
    fun `updateFontSize with boundary value exactly at minimum`() = runTest {
        // Given
        val settings = createTestSettings()
        settings.putFloat("font_size", 12f)
        val fontSettingsManager = FontSettingsManager(settings)

        // When
        fontSettingsManager.updateFontSize(-1f)
        val fontSettings = fontSettingsManager.fontSettings.first()

        // Then
        assertEquals(12f, fontSettings.fontSize)
    }

    @Test
    fun `updateFontSize with boundary value exactly at maximum`() = runTest {
        // Given
        val settings = createTestSettings()
        settings.putFloat("font_size", 24f)
        val fontSettingsManager = FontSettingsManager(settings)

        // When
        fontSettingsManager.updateFontSize(1f)
        val fontSettings = fontSettingsManager.fontSettings.first()

        // Then
        assertEquals(24f, fontSettings.fontSize)
    }

    @Test
    fun `fontSettings flow emits updated values`() = runTest {
        // Given
        val settings = createTestSettings()
        val fontSettingsManager = FontSettingsManager(settings)

        // When
        fontSettingsManager.updateFontFamily("TestFont")
        fontSettingsManager.updateFontSize(4f)
        val fontSettings = fontSettingsManager.fontSettings.first()

        // Then
        assertEquals("TestFont", fontSettings.fontFamily)
        assertEquals(20f, fontSettings.fontSize)
    }

    @Test
    fun `changing only font family preserves font size`() = runTest {
        // Given
        val settings = createTestSettings()
        settings.putFloat("font_size", 18f)
        val fontSettingsManager = FontSettingsManager(settings)

        // When
        fontSettingsManager.updateFontFamily("NewFamily")
        val fontSettings = fontSettingsManager.fontSettings.first()

        // Then
        assertEquals("NewFamily", fontSettings.fontFamily)
        assertEquals(18f, fontSettings.fontSize)
    }

    @Test
    fun `changing only font size preserves font family`() = runTest {
        // Given
        val settings = createTestSettings()
        settings.putString("font_family", "CustomFamily")
        val fontSettingsManager = FontSettingsManager(settings)

        // When
        fontSettingsManager.updateFontSize(2f)
        val fontSettings = fontSettingsManager.fontSettings.first()

        // Then
        assertEquals("CustomFamily", fontSettings.fontFamily)
        assertEquals(18f, fontSettings.fontSize)
    }

    @Test
    fun `font size stays within bounds after multiple increases`() = runTest {
        // Given
        val settings = createTestSettings()
        val fontSettingsManager = FontSettingsManager(settings)

        // When
        repeat(10) {
            fontSettingsManager.updateFontSize(2f)
        }
        val fontSettings = fontSettingsManager.fontSettings.first()

        // Then
        assertTrue(fontSettings.fontSize <= 24f)
        assertEquals(24f, fontSettings.fontSize)
    }

    @Test
    fun `font size stays within bounds after multiple decreases`() = runTest {
        // Given
        val settings = createTestSettings()
        val fontSettingsManager = FontSettingsManager(settings)

        // When
        repeat(10) {
            fontSettingsManager.updateFontSize(-2f)
        }
        val fontSettings = fontSettingsManager.fontSettings.first()

        // Then
        assertTrue(fontSettings.fontSize >= 12f)
        assertEquals(12f, fontSettings.fontSize)
    }

    @Test
    fun `zero size change keeps font size unchanged`() = runTest {
        // Given
        val settings = createTestSettings()
        val fontSettingsManager = FontSettingsManager(settings)
        val initialSize = fontSettingsManager.fontSettings.first().fontSize

        // When
        fontSettingsManager.updateFontSize(0f)
        val fontSettings = fontSettingsManager.fontSettings.first()

        // Then
        assertEquals(initialSize, fontSettings.fontSize)
    }
}
