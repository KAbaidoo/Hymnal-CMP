package com.kobby.hymnal.core.sharing

import com.kobby.hymnal.composeApp.database.Hymn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ShareContentFormatterTest {

    private fun createTestHymn(
        id: Long = 1,
        number: Long = 1,
        title: String = "Amazing Grace",
        category: String = "ancient_modern",
        content: String = "Amazing grace, how sweet the sound",
        createdAt: String = "2024-01-01"
    ): Hymn {
        return Hymn(
            id = id,
            number = number,
            title = title,
            category = category,
            content = content,
            created_at = createdAt
        )
    }

    @Test
    fun `formatHymnForSharing includes hymn header`() {
        // Given
        val hymn = createTestHymn()

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains("🎵 A&M 1"))
    }

    @Test
    fun `formatHymnForSharing includes full content`() {
        // Given
        val hymn = createTestHymn(content = "Test hymn content")

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains("Test hymn content"))
    }

    @Test
    fun `formatHymnForSharing includes app promotion`() {
        // Given
        val hymn = createTestHymn()

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains("📱 Shared from ${ShareConstants.APP_NAME}"))
        assertTrue(formatted.contains("✨ ${ShareConstants.APP_TAGLINE}"))
        assertTrue(formatted.contains("📲 Download:"))
    }

    @Test
    fun `formatHymnForSharing includes Android Play Store URL`() {
        // Given
        val hymn = createTestHymn()

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains("🤖 Android: ${ShareConstants.ANDROID_PLAY_STORE_URL}"))
    }

    @Test
    fun `formatHymnForSharing includes iOS App Store URL`() {
        // Given
        val hymn = createTestHymn()

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains("🍎 iOS: ${ShareConstants.IOS_APP_STORE_URL}"))
    }

    @Test
    fun `formatHymnForSharing includes hashtags`() {
        // Given
        val hymn = createTestHymn()

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        ShareConstants.HASHTAGS.forEach { hashtag ->
            assertTrue(formatted.contains(hashtag))
        }
    }

    @Test
    fun `formatHymnForSharing for ancient_modern hymn uses correct abbreviation`() {
        // Given
        val hymn = createTestHymn(number = 123, category = "ancient_modern")

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains("🎵 A&M 123"))
    }

    @Test
    fun `formatHymnForSharing for supplementary hymn uses correct abbreviation`() {
        // Given
        val hymn = createTestHymn(number = 801, category = "supplementary")

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains("🎵 Supp 801"))
    }

    @Test
    fun `formatHymnForSharing for canticle uses title instead of number`() {
        // Given
        val hymn = createTestHymn(number = 0, title = "Te Deum", category = "canticles")

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains("🎵 Te Deum"))
    }

    @Test
    fun `formatHymnForSharing for creed uses special header`() {
        // Given
        val hymn = createTestHymn(number = 0, title = "The Creed", category = "creed")

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains("🎵 The Creed"))
    }

    @Test
    fun `formatHymnForSharing handles null content gracefully`() {
        // Given
        val hymn = createTestHymn(content = null)

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains("No content available"))
    }

    @Test
    fun `formatHymnForSharing handles empty content`() {
        // Given
        val hymn = createTestHymn(content = "")

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains(""))
    }

    @Test
    fun `formatHymnForSharing handles unknown category`() {
        // Given
        val hymn = createTestHymn(number = 100, category = "unknown")

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains("🎵 Hymn 100"))
    }

    @Test
    fun `formatHymnForSharing has proper section separation`() {
        // Given
        val hymn = createTestHymn()

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        // Check that sections are separated by double newlines
        val sections = formatted.split("\n\n")
        assertTrue(sections.size >= 4, "Expected at least 4 sections separated by double newlines")
    }

    @Test
    fun `formatHymnForSharing starts with hymn header icon`() {
        // Given
        val hymn = createTestHymn()

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.startsWith("🎵"))
    }

    @Test
    fun `formatHymnForSharing for canticle with null title shows default text`() {
        // Given
        val hymn = createTestHymn(number = 5, title = null, category = "canticles")

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains("🎵 Untitled Canticle"))
    }

    @Test
    fun `formatHymnForSharing contains all required emojis`() {
        // Given
        val hymn = createTestHymn()

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains("🎵"), "Should contain music note emoji")
        assertTrue(formatted.contains("📱"), "Should contain mobile phone emoji")
        assertTrue(formatted.contains("✨"), "Should contain sparkles emoji")
        assertTrue(formatted.contains("📲"), "Should contain download emoji")
        assertTrue(formatted.contains("🤖"), "Should contain Android emoji")
        assertTrue(formatted.contains("🍎"), "Should contain Apple emoji")
    }

    @Test
    fun `formatHymnForSharing for hymn with multiline content preserves structure`() {
        // Given
        val multilineContent = """
            First verse here
            Second verse here
            Third verse here
        """.trimIndent()
        val hymn = createTestHymn(content = multilineContent)

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains("First verse here"))
        assertTrue(formatted.contains("Second verse here"))
        assertTrue(formatted.contains("Third verse here"))
    }

    @Test
    fun `formatHymnForSharing for hymn with special characters in content`() {
        // Given
        val specialContent = "Lord's Prayer & Testament"
        val hymn = createTestHymn(content = specialContent)

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains(specialContent))
    }

    @Test
    fun `formatHymnForSharing for hymn with very long content includes everything`() {
        // Given
        val longContent = "A".repeat(1000)
        val hymn = createTestHymn(content = longContent)

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains(longContent))
    }

    @Test
    fun `formatHymnForSharing includes app tagline`() {
        // Given
        val hymn = createTestHymn()

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains(ShareConstants.APP_TAGLINE))
    }

    @Test
    fun `formatHymnForSharing includes app name`() {
        // Given
        val hymn = createTestHymn()

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains(ShareConstants.APP_NAME))
    }

    @Test
    fun `formatHymnForSharing for supplementary with high number`() {
        // Given
        val hymn = createTestHymn(number = 855, category = "supplementary")

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.contains("🎵 Supp 855"))
    }

    @Test
    fun `formatHymnForSharing produces non-empty output`() {
        // Given
        val hymn = createTestHymn()

        // When
        val formatted = ShareContentFormatter.formatHymnForSharing(hymn)

        // Then
        assertTrue(formatted.isNotEmpty())
        assertTrue(formatted.isNotBlank())
    }
}
