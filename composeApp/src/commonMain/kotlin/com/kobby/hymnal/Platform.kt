package com.kobby.hymnal

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform