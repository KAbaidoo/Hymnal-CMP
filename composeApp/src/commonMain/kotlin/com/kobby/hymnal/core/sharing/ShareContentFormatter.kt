package com.kobby.hymnal.core.sharing

import com.kobby.hymnal.composeApp.database.Hymn

object ShareContentFormatter {
    
    fun formatHymnForSharing(hymn: Hymn): String {
        val hymnHeader = buildHymnHeader(hymn)
        val fullContent = getFullContent(hymn)
        val appPromotion = buildAppPromotion()
        val hashtags = ShareConstants.HASHTAGS.joinToString(" ")
        
        return buildString {
            append("🎵 $hymnHeader")
            append("\n\n")
            append(fullContent)
            append("\n\n")
            append(appPromotion)
            append("\n\n")
            append(hashtags)
        }
    }
    
    private fun buildHymnHeader(hymn: Hymn): String {
        val categoryAbbrev = getCategoryAbbreviation(hymn.category)
        val hymnNumber = if (hymn.number == 0L) "Creed" else hymn.number.toString()
        
        return when {
            hymn.number == 0L -> "The Creed"
            hymn.category == "canticles" -> hymn.title ?: "Untitled Canticle"
            else -> "$categoryAbbrev $hymnNumber"
        }
    }
    
    private fun getFullContent(hymn: Hymn): String {
        return hymn.content
    }
    
    private fun buildAppPromotion(): String {
        return buildString {
            append("📱 Shared from ${ShareConstants.APP_NAME}")
            append("\n✨ ${ShareConstants.APP_TAGLINE}")
            append("\n🔗 ${ShareConstants.LANDING_PAGE_URL}")
        }
    }
    
    private fun getCategoryAbbreviation(category: String?): String {
        return when (category) {
            "ancient_modern" -> "A&M"
            "supplementary" -> "Supp"
            "canticles" -> ""
            "creed" -> "The"
            else -> "Hymn"
        }
    }
}