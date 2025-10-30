package com.kobby.hymnal.core.sharing

import com.kobby.hymnal.composeApp.database.Hymn
import kotlin.test.*

class ShareContentFormatterTest {

    // Test Data Helpers
    private fun createHymn(
        id: Long = 1,
        number: Long = 100,
        title: String? = "Amazing Grace",
        category: String = "ancient_modern",
        content: String = "Amazing grace! How sweet the sound\nThat saved a wretch like me!",
        createdAt: Long = 1234567890
    ) = Hymn(
        id = id,
        number = number,
        title = title,
        category = category,
        content = content,
        created_at = createdAt
    )

    // Format Hymn for Sharing Tests
    @Test
    fun `formatHymnForSharing should include all required sections`() {
        val hymn = createHymn()
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.contains("🎵"))
        assertTrue(result.contains(hymn.content!!))
        assertTrue(result.contains(ShareConstants.APP_NAME))
        assertTrue(result.contains(ShareConstants.HASHTAGS.joinToString(" ")))
    }

    @Test
    fun `formatHymnForSharing for ancient_modern hymn should show A&M abbreviation`() {
        val hymn = createHymn(number = 123, category = "ancient_modern")
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.contains("🎵 A&M 123"))
    }

    @Test
    fun `formatHymnForSharing for supplementary hymn should show Supp abbreviation`() {
        val hymn = createHymn(number = 45, category = "supplementary")
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.contains("🎵 Supp 45"))
    }

    @Test
    fun `formatHymnForSharing for canticle should show title instead of number`() {
        val hymn = createHymn(
            number = 1001,
            title = "Venite",
            category = "canticles",
            content = "O come, let us sing unto the Lord"
        )
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.contains("🎵 Venite"))
        assertFalse(result.contains("1001"))
    }

    @Test
    fun `formatHymnForSharing for The Creed should show special formatting`() {
        val hymn = createHymn(
            number = 0,
            title = "The Creed",
            category = "creed",
            content = "I believe in God the Father Almighty"
        )
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.contains("🎵 The Creed"))
    }

    @Test
    fun `formatHymnForSharing should include full hymn content`() {
        val content = "Verse 1: Amazing grace\nVerse 2: 'Twas grace that taught"
        val hymn = createHymn(content = content)
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.contains(content))
    }

    @Test
    fun `formatHymnForSharing should include app promotion with both store links`() {
        val hymn = createHymn()
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.contains(ShareConstants.APP_NAME))
        assertTrue(result.contains(ShareConstants.APP_TAGLINE))
        assertTrue(result.contains(ShareConstants.ANDROID_PLAY_STORE_URL))
        assertTrue(result.contains(ShareConstants.IOS_APP_STORE_URL))
    }

    @Test
    fun `formatHymnForSharing should include all hashtags`() {
        val hymn = createHymn()
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        ShareConstants.HASHTAGS.forEach { hashtag ->
            assertTrue(result.contains(hashtag), "Should contain hashtag: $hashtag")
        }
    }

    @Test
    fun `formatHymnForSharing should handle null content gracefully`() {
        val hymn = createHymn(content = "")
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        // Should still have structure even with empty content
        assertTrue(result.contains("🎵"))
        assertTrue(result.contains(ShareConstants.APP_NAME))
    }

    @Test
    fun `formatHymnForSharing should handle null title for canticles gracefully`() {
        val hymn = createHymn(
            number = 1002,
            title = null,
            category = "canticles"
        )
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.contains("🎵 Untitled Canticle"))
    }

    @Test
    fun `formatHymnForSharing should handle unknown category`() {
        val hymn = createHymn(number = 99, category = "unknown")
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.contains("🎵 Hymn 99"))
    }

    // Category Abbreviation Tests (via output verification)
    @Test
    fun `ancient_modern category should produce A&M abbreviation`() {
        val hymn = createHymn(number = 500, category = "ancient_modern")
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.contains("A&M 500"))
    }

    @Test
    fun `supplementary category should produce Supp abbreviation`() {
        val hymn = createHymn(number = 10, category = "supplementary")
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.contains("Supp 10"))
    }

    @Test
    fun `canticles category should not show abbreviation`() {
        val hymn = createHymn(
            number = 1003,
            title = "Jubilate Deo",
            category = "canticles"
        )
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.contains("Jubilate Deo"))
        assertFalse(result.contains("A&M"))
        assertFalse(result.contains("Supp"))
    }

    @Test
    fun `creed category should show The abbreviation`() {
        val hymn = createHymn(number = 0, category = "creed")
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.contains("The Creed"))
    }

    // Output Format Structure Tests
    @Test
    fun `formatHymnForSharing should have correct section ordering`() {
        val hymn = createHymn()
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        val headerIndex = result.indexOf("🎵")
        val contentIndex = result.indexOf(hymn.content!!)
        val promoIndex = result.indexOf(ShareConstants.APP_NAME)
        val hashtagsIndex = result.indexOf(ShareConstants.HASHTAGS[0])

        assertTrue(headerIndex < contentIndex, "Header should come before content")
        assertTrue(contentIndex < promoIndex, "Content should come before promotion")
        assertTrue(promoIndex < hashtagsIndex, "Promotion should come before hashtags")
    }

    @Test
    fun `formatHymnForSharing should separate sections with blank lines`() {
        val hymn = createHymn()
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        // Should have double newlines separating sections
        assertTrue(result.contains("\n\n"))
    }

    @Test
    fun `formatHymnForSharing should include emoji in header`() {
        val hymn = createHymn()
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.startsWith("🎵"))
    }

    @Test
    fun `formatHymnForSharing should include both Android and iOS links in promotion`() {
        val hymn = createHymn()
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.contains("🤖"))
        assertTrue(result.contains("🍎"))
        assertTrue(result.contains("Android:"))
        assertTrue(result.contains("iOS:"))
    }

    // Edge Cases
    @Test
    fun `formatHymnForSharing should handle very long content`() {
        val longContent = "Amazing grace! ".repeat(100)
        val hymn = createHymn(content = longContent)
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.contains(longContent))
        assertTrue(result.contains(ShareConstants.APP_NAME))
    }

    @Test
    fun `formatHymnForSharing should handle special characters in content`() {
        val specialContent = "Glory to God! \"Hallelujah\" & 'Amen' - †"
        val hymn = createHymn(content = specialContent)
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.contains(specialContent))
    }

    @Test
    fun `formatHymnForSharing should handle multiline content with various line breaks`() {
        val multilineContent = "Line 1\nLine 2\n\nLine 4 (after blank)\rLine 5"
        val hymn = createHymn(content = multilineContent)
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.contains("Line 1"))
        assertTrue(result.contains("Line 2"))
        assertTrue(result.contains("Line 4"))
    }

    @Test
    fun `formatHymnForSharing should handle number zero for Creed`() {
        val hymn = createHymn(number = 0, title = "The Creed", category = "creed")
        val result = ShareContentFormatter.formatHymnForSharing(hymn)

        assertTrue(result.contains("The Creed"))
        assertFalse(result.contains("Creed 0"))
        assertFalse(result.contains("The 0"))
    }

    // Integration Tests
    @Test
    fun `formatHymnForSharing should produce valid shareable content for all category types`() {
        val categories = listOf(
            createHymn(number = 100, category = "ancient_modern"),
            createHymn(number = 20, category = "supplementary"),
            createHymn(number = 1001, title = "Venite", category = "canticles"),
            createHymn(number = 0, title = "The Creed", category = "creed")
        )

        categories.forEach { hymn ->
            val result = ShareContentFormatter.formatHymnForSharing(hymn)

            // All should have these basic elements
            assertTrue(result.isNotEmpty(), "Result should not be empty for ${hymn.category}")
            assertTrue(result.contains("🎵"), "Should contain emoji for ${hymn.category}")
            assertTrue(result.contains(ShareConstants.APP_NAME), "Should contain app name for ${hymn.category}")
            assertTrue(result.contains(hymn.content!!), "Should contain content for ${hymn.category}")
        }
    }

    @Test
    fun `formatHymnForSharing should produce consistent output for same hymn`() {
        val hymn = createHymn()

        val result1 = ShareContentFormatter.formatHymnForSharing(hymn)
        val result2 = ShareContentFormatter.formatHymnForSharing(hymn)

        assertEquals(result1, result2, "Same hymn should produce identical output")
    }

    // ShareConstants Verification Tests
    @Test
    fun `ShareConstants should have valid URLs`() {
        assertTrue(ShareConstants.ANDROID_PLAY_STORE_URL.startsWith("https://"))
        assertTrue(ShareConstants.IOS_APP_STORE_URL.startsWith("https://"))
        assertTrue(ShareConstants.LANDING_PAGE_URL.startsWith("https://"))
    }

    @Test
    fun `ShareConstants should have non-empty app information`() {
        assertTrue(ShareConstants.APP_NAME.isNotEmpty())
        assertTrue(ShareConstants.APP_TAGLINE.isNotEmpty())
        assertTrue(ShareConstants.APP_DESCRIPTION.isNotEmpty())
    }

    @Test
    fun `ShareConstants should have hashtags list with proper format`() {
        assertTrue(ShareConstants.HASHTAGS.isNotEmpty())
        ShareConstants.HASHTAGS.forEach { hashtag ->
            assertTrue(hashtag.startsWith("#"), "Hashtag should start with #: $hashtag")
        }
    }

    @Test
    fun `ShareConstants should have reasonable content limits`() {
        assertTrue(ShareConstants.MAX_CONTENT_LENGTH > 0)
        assertTrue(ShareConstants.MAX_TWEET_LENGTH == 280)
    }
}
