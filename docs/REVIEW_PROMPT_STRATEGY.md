# In-App Review Prompt Strategy

## Overview
This document outlines the strategy for requesting in-app reviews from users. The goal is to maximize positive feedback while respecting user experience and platform guidelines (Apple/Google).

## Core Strategy
We use a **Fresh Engagement + Cooldown** strategy. We do not ask for reviews based on total lifetime usage, but rather on recent, meaningful interactions.

### 1. Fresh Engagement Rule
A user must demonstrate value from the *current* session or version before being prompted.
- **Requirement:** 5 Hymn Reads OR 3 Successful Searches.
- **Reset:** These counters (`reviewHymnsReadCount` and `reviewSearchCount`) are **reset to 0** immediately after a prompt is shown.
- **Implication:** A user must "earn" the next prompt by using the app again.

### 2. Cooldown Period
We enforce a strict time-based cooldown to prevent spam.
- **Duration:** 90 Days.
- **Logic:** `(CurrentTime - LastPromptTime) > 90 Days`.
- **Reasoning:** Aligns with Apple's limit of ~3 requests per year and ensures we capture feedback on new features over time (e.g., quarterly).

## Implementation Details

### Storage Keys (`PurchaseStorage`)
- `KEY_REVIEW_HYMNS_READ_COUNT` (Int): Tracks reads since last prompt.
- `KEY_REVIEW_SEARCH_COUNT` (Int): Tracks searches since last prompt.
- `KEY_LAST_REVIEW_PROMPT_TIMESTAMP` (Long): Timestamp of the last prompt.
- `KEY_HAS_SHOWN_REVIEW_PROMPT` (Boolean): **Deprecated** (legacy flag, ignored in new logic).

### Logic Flow (`UsageTrackingManager`)
```kotlin
fun shouldShowReviewPrompt(): Boolean {
    // 1. Check Cooldown
    if (now - lastPrompt < 90_DAYS) return false

    // 2. Check Engagement
    return freshReads >= 5 || freshSearches >= 3
}
```

## Migration Note
- **New vs. Existing Users:** The new counters (`reviewHymnsReadCount`, etc.) are new keys.
- **Effect:** Upon updating to the version containing this logic, **ALL** users (new and existing) start with counters at 0.
- **Benefit:** This prevents existing heavy users from seeing a prompt immediately upon update. They must perform 5 *new* reads to trigger the prompt.
