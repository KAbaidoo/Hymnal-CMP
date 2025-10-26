package com.kobby.hymnal.core.sharing

import android.content.Context
import android.content.Intent
import com.kobby.hymnal.composeApp.database.Hymn

actual class ShareManager(private val context: Context) {
    
    actual fun shareHymn(hymn: Hymn) {
        val shareContent = ShareContentFormatter.formatHymnForSharing(hymn)
        val hymnTitle = hymn.title ?: "Anglican Hymn"
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareContent)
            putExtra(Intent.EXTRA_SUBJECT, "🎵 $hymnTitle - Anglican Hymnal")
        }
        
        val chooserIntent = Intent.createChooser(shareIntent, "Share Hymn")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        
        try {
            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            // Handle case where no sharing apps are available
            // Could show a toast or log the error
            e.printStackTrace()
        }
    }
}