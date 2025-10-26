package com.kobby.hymnal.core.sharing

import com.kobby.hymnal.composeApp.database.Hymn

expect class ShareManager {
    fun shareHymn(hymn: Hymn)
}

object ShareConstants {
    // App Store URLs - Update these when publishing to stores
    const val ANDROID_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.kobby.hymnal"
    const val IOS_APP_STORE_URL = "https://apps.apple.com/app/anglican-hymnal/id123456789" // TODO: Replace with actual App Store ID
    
    // Fallback landing page
    const val LANDING_PAGE_URL = "https://github.com/KAbaidoo/Hymnal-CMP" // Current project repository
    
    // App branding
    const val APP_NAME = "Anglican Hymnal"
    const val APP_TAGLINE = "841 Anglican hymns always available offline"
    const val APP_DESCRIPTION = "Complete collection of Ancient & Modern, Supplementary hymns, and Canticles"
    
    // Social media hashtags
    val HASHTAGS = listOf("#Anglican", "#Hymns", "#Worship", "#OfflineHymnal", "#Liturgy")
    
    // Content limits for different platforms
    const val MAX_CONTENT_LENGTH = 200
    const val MAX_TWEET_LENGTH = 280
}