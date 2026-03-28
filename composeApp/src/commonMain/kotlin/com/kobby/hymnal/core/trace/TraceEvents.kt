package com.kobby.hymnal.core.trace

object TraceEvents {
    const val START_SCREEN_OUTCOME = "start_screen_outcome"
    const val HOME_NAVIGATION_CLICK = "home_navigation_click"
    const val UPDATE_PROMPT_INTERACTION = "update_prompt_interaction"
    const val SEARCH_BEHAVIOR = "search_behavior"
    const val HYMN_OPENED = "hymn_opened"
    const val HYMN_DETAIL_ACTION = "hymn_detail_action"
    const val HIGHLIGHT_ACTION = "highlight_action"
    const val MORE_MENU_NAVIGATION = "more_menu_navigation"
    const val HISTORY_MANAGEMENT = "history_management"
    const val SUPPORT_FUNNEL = "support_funnel"
}

fun traceParams(vararg pairs: Pair<String, Any?>): Map<String, String> {
    return buildMap {
        for ((key, value) in pairs) {
            if (value != null) {
                put(key, value.toString())
            }
        }
    }
}

