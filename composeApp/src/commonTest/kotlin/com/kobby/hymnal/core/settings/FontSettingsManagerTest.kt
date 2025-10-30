package com.kobby.hymnal.core.settings

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class FontSettingsManagerTest {

    private lateinit var settings: Settings
    private lateinit var fontSettingsManager: FontSettingsManager

    @BeforeTest
    fun setup() {
        settings = Settings()
        fontSettingsManager = FontSettingsManager(settings)
    }

    @AfterTest
    fun tearDown() {
        settings.clear()
    }

    // Default Settings Tests
    @Test
    fun `initial font settings should have default values`() = runTest {
        val fontSettings = fontSettingsManager.fontSettings.first()

        assertEquals("Onest", fontSettings.fontFamily)
        assertEquals(16f, fontSettings.fontSize)
    }

    @Test
    fun `should load existing font family from settings`() = runTest {
        // Pre-populate settings
        settings.putString("font_family", "Roboto")
        settings.putFloat("font_size", 18f)

        // Create manager after settings are populated
        val manager = FontSettingsManager(settings)
        val fontSettings = manager.fontSettings.first()

        assertEquals("Roboto", fontSettings.fontFamily)
        assertEquals(18f, fontSettings.fontSize)
    }

    // Font Family Update Tests
    @Test
    fun `updateFontFamily should update font family in settings and flow`() = runTest {
        fontSettingsManager.updateFontFamily("Arial")

        val fontSettings = fontSettingsManager.fontSettings.first()
        assertEquals("Arial", fontSettings.fontFamily)
        assertEquals("Arial", settings.getString("font_family", ""))
    }

    @Test
    fun `updateFontFamily should preserve font size`() = runTest {
        fontSettingsManager.updateFontSize(2f) // Change to 18f
        fontSettingsManager.updateFontFamily("Times New Roman")

        val fontSettings = fontSettingsManager.fontSettings.first()
        assertEquals("Times New Roman", fontSettings.fontFamily)
        assertEquals(18f, fontSettings.fontSize)
    }

    @Test
    fun `updateFontFamily with empty string should be allowed`() = runTest {
        fontSettingsManager.updateFontFamily("")

        val fontSettings = fontSettingsManager.fontSettings.first()
        assertEquals("", fontSettings.fontFamily)
    }

    // Font Size Update Tests
    @Test
    fun `updateFontSize should increase font size within bounds`() = runTest {
        fontSettingsManager.updateFontSize(2f)

        val fontSettings = fontSettingsManager.fontSettings.first()
        assertEquals(18f, fontSettings.fontSize)
        assertEquals(18f, settings.getFloat("font_size", 0f))
    }

    @Test
    fun `updateFontSize should decrease font size within bounds`() = runTest {
        fontSettingsManager.updateFontSize(-2f)

        val fontSettings = fontSettingsManager.fontSettings.first()
        assertEquals(14f, fontSettings.fontSize)
        assertEquals(14f, settings.getFloat("font_size", 0f))
    }

    @Test
    fun `updateFontSize should not exceed maximum size of 24f`() = runTest {
        fontSettingsManager.updateFontSize(10f) // Try to set to 26f

        val fontSettings = fontSettingsManager.fontSettings.first()
        assertEquals(24f, fontSettings.fontSize)
    }

    @Test
    fun `updateFontSize should not go below minimum size of 12f`() = runTest {
        fontSettingsManager.updateFontSize(-10f) // Try to set to 6f

        val fontSettings = fontSettingsManager.fontSettings.first()
        assertEquals(12f, fontSettings.fontSize)
    }

    @Test
    fun `updateFontSize with zero should keep current size`() = runTest {
        fontSettingsManager.updateFontSize(0f)

        val fontSettings = fontSettingsManager.fontSettings.first()
        assertEquals(16f, fontSettings.fontSize)
    }

    @Test
    fun `multiple updateFontSize calls should be cumulative`() = runTest {
        fontSettingsManager.updateFontSize(2f) // 16 -> 18
        fontSettingsManager.updateFontSize(2f) // 18 -> 20
        fontSettingsManager.updateFontSize(-1f) // 20 -> 19

        val fontSettings = fontSettingsManager.fontSettings.first()
        assertEquals(19f, fontSettings.fontSize)
    }

    @Test
    fun `updateFontSize should preserve font family`() = runTest {
        fontSettingsManager.updateFontFamily("Georgia")
        fontSettingsManager.updateFontSize(4f)

        val fontSettings = fontSettingsManager.fontSettings.first()
        assertEquals("Georgia", fontSettings.fontFamily)
        assertEquals(20f, fontSettings.fontSize)
    }

    // Edge Cases
    @Test
    fun `font size at maximum boundary should not increase further`() = runTest {
        fontSettingsManager.updateFontSize(8f) // Set to 24f (max)
        fontSettingsManager.updateFontSize(1f) // Try to increase beyond max

        val fontSettings = fontSettingsManager.fontSettings.first()
        assertEquals(24f, fontSettings.fontSize)
    }

    @Test
    fun `font size at minimum boundary should not decrease further`() = runTest {
        fontSettingsManager.updateFontSize(-4f) // Set to 12f (min)
        fontSettingsManager.updateFontSize(-1f) // Try to decrease below min

        val fontSettings = fontSettingsManager.fontSettings.first()
        assertEquals(12f, fontSettings.fontSize)
    }

    @Test
    fun `updating both font family and size should work correctly`() = runTest {
        fontSettingsManager.updateFontFamily("Verdana")
        fontSettingsManager.updateFontSize(3f)

        val fontSettings = fontSettingsManager.fontSettings.first()
        assertEquals("Verdana", fontSettings.fontFamily)
        assertEquals(19f, fontSettings.fontSize)

        // Verify persistence
        assertEquals("Verdana", settings.getString("font_family", ""))
        assertEquals(19f, settings.getFloat("font_size", 0f))
    }

    // StateFlow Behavior Tests
    @Test
    fun `fontSettings flow should emit updated values`() = runTest {
        val initialSettings = fontSettingsManager.fontSettings.first()
        assertEquals("Onest", initialSettings.fontFamily)

        fontSettingsManager.updateFontFamily("Courier")

        val updatedSettings = fontSettingsManager.fontSettings.first()
        assertEquals("Courier", updatedSettings.fontFamily)
    }

    @Test
    fun `fontSettings should be a StateFlow with immediate value`() = runTest {
        // StateFlow should provide immediate value without suspending indefinitely
        val fontSettings = fontSettingsManager.fontSettings.value

        assertNotNull(fontSettings)
        assertEquals("Onest", fontSettings.fontFamily)
        assertEquals(16f, fontSettings.fontSize)
    }

    // Data Class Tests
    @Test
    fun `FontSettings data class should support copy`() {
        val original = FontSettings(fontFamily = "Arial", fontSize = 18f)
        val copied = original.copy(fontFamily = "Times")

        assertEquals("Times", copied.fontFamily)
        assertEquals(18f, copied.fontSize)
    }

    @Test
    fun `FontSettings equality should work correctly`() {
        val settings1 = FontSettings(fontFamily = "Arial", fontSize = 18f)
        val settings2 = FontSettings(fontFamily = "Arial", fontSize = 18f)
        val settings3 = FontSettings(fontFamily = "Times", fontSize = 18f)

        assertEquals(settings1, settings2)
        assertNotEquals(settings1, settings3)
    }
}
