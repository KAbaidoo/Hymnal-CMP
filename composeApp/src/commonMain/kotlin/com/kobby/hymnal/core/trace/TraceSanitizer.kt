package com.kobby.hymnal.core.trace

private const val MAX_EVENT_NAME_LENGTH = 40
private const val MAX_PARAM_KEY_LENGTH = 40
private const val MAX_PARAM_VALUE_LENGTH = 100
private const val MAX_PARAMS = 25

private val blockedExactKeys = setOf(
    "query",
    "search_query",
    "raw_query",
    "hymn_content",
    "content",
    "text",
    "message"
)

object TraceSanitizer {
    fun sanitizeEventName(raw: String): String {
        val sanitized = sanitizeToken(raw, MAX_EVENT_NAME_LENGTH)
        return if (sanitized.isBlank()) "unknown_event" else sanitized
    }

    fun sanitizeParams(raw: Map<String, String>): Map<String, String> {
        if (raw.isEmpty()) return emptyMap()

        val output = linkedMapOf<String, String>()

        for ((keyRaw, valueRaw) in raw) {
            if (output.size >= MAX_PARAMS) break

            val key = sanitizeToken(keyRaw, MAX_PARAM_KEY_LENGTH)
            if (key.isBlank()) continue
            if (isPrivacyBlockedKey(key)) continue

            val value = valueRaw.trim().take(MAX_PARAM_VALUE_LENGTH)
            if (value.isBlank()) continue

            output[key] = value
        }

        return output
    }

    private fun isPrivacyBlockedKey(key: String): Boolean {
        if (key in blockedExactKeys) return true
        if (key.contains("query") && key != "query_length") return true
        return false
    }

    private fun sanitizeToken(raw: String, maxLength: Int): String {
        val lowered = raw.trim().lowercase()
        if (lowered.isBlank()) return ""

        val replaced = buildString(lowered.length) {
            var previousUnderscore = false
            for (ch in lowered) {
                val out = when {
                    ch.isLetterOrDigit() -> ch
                    else -> '_'
                }
                if (out == '_') {
                    if (!previousUnderscore) {
                        append(out)
                        previousUnderscore = true
                    }
                } else {
                    append(out)
                    previousUnderscore = false
                }
                if (length >= maxLength) break
            }
        }
        return replaced.trim('_')
    }
}

