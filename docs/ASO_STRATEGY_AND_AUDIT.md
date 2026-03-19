# App Store Optimization (ASO) Audit & Strategy

**Project:** Hymnal-CMP  
**Last Updated:** March 2026  
**Status:** Implementation Phase

---

## 1. Executive Summary
Hymnal-CMP currently has an ASO Health Score of **35/100**. The app suffers from generic metadata and a total lack of user ratings (0 reviews). However, the core product is high-quality, feature-rich, and localized, providing a strong foundation for rapid growth if optimized.

### Current Score Card
- **Title/Subtitle:** 5/10 (Generic, underutilized)
- **Keyword Field:** 2/10 (Likely default or missing)
- **Social Proof:** 0/10 (0 ratings — Major Blocker)
- **Visuals:** 7/10 (Clean UI, but captions need benefit-driven text)

---

## 2. Market Strategies

### A. Localized Strategy (Ghana Target)
*Focus: Dominance through local relevance and data-cost awareness.*

| Field | Recommended Content | Rationale |
|-------|---------------------|-----------|
| **App Title** | `Anglican Hymnal Ghana: A&M` | Includes "A&M" (Ancient & Modern) and local identifier. |
| **Subtitle** | `Ancient & Modern Hymns Offline` | High-intent terms; "Offline" solves data-cost concerns. |
| **Keywords** | `hymns,ghana,ancient,modern,church,liturgy,lyrics,offline,audio,twi,mhb,worship,praise,psalms,canticles` | Targets local search behavior (MHB, Twi). |

### B. Global Strategy (US/UK/International Target)
*Focus: Authority, tradition, and "Generous Freemium" model.*

| Field | Recommended Content | Rationale |
|-------|---------------------|-----------|
| **App Title** | `Anglican Hymnal: A&M & Psalms` | Emphasizes the "A&M" brand and liturgical completeness. |
| **Subtitle** | `Ancient & Modern Church Hymns` | Broadens search intent to general "Church Hymns" traffic. |
| **Keywords** | `hymns,ancient,modern,church,liturgy,lyrics,offline,audio,worship,praise,psalms,canticles,episcopal,christian,bcp,prayer` | Includes US-specific terms like "Episcopal" and "BCP". |

---

## 3. Visual Optimization (Screenshots)

To improve Conversion Rate (CVR), the first three screenshots must feature bold, benefit-driven captions:

1.  **Authority:** "991 Anglican Hymns" (The vast, definitive collection)
2.  **Trust:** "Ancient & Modern & Canticles" (Verification of liturgical standards)
3.  **Utility:** "100% Offline Access" (Solves connectivity/cost issues)

---

## 4. Technical Implementation: Social Proof (Ratings)

The lack of ratings is the primary barrier to ranking. We must implement the native iOS/Android rating prompts immediately.

### SKStoreReviewController Strategy (iOS)
We will use a "Moment of Joy" trigger to ask for a rating before the user encounters a "Moment of Friction" (the donation prompt).

**Trigger Logic (Smart Strategy):**
- **Fresh Engagement:** User must perform **5 *new* reads** or **3 *new* searches** since the last prompt.
- **Cooldown:** Strict **90-day limit** between requests (aligns with Apple/Google quotas).
- **Context:** The user has verified the app's value but hasn't yet reached the next donation prompt.

**See full strategy:** `docs/REVIEW_PROMPT_STRATEGY.md`

**Code Implementation Plan:**
1.  Update `UsageTrackingManager.kt` to track search success and hymn read milestones specifically for ratings.
2.  Create a `ReviewManager` interface in `commonMain` with platform-specific implementations.
3.  On iOS (`iosMain`), call `SKStoreReviewController.requestReviewInScene(windowScene)`.
4.  On Android (`androidMain`), use the Google Play In-App Review API.

---

## 5. Immediate Action Items

1.  [ ] **Metadata Update:** Apply the Global or Localized metadata to App Store Connect.
2.  [ ] **Rating Prompt:** Implement `UsageTrackingManager` updates and platform-specific Review Managers.
3.  [ ] **Screenshot Refresh:** Add text overlays to the current screenshot set.
4.  [ ] **Description Update:** Rewrite the first 3 lines of the App Store description to include the "Ancient & Modern" brand authority.
