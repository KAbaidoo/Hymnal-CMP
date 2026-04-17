# Monetization Strategy Plan: Value-Based Support (2026)

## 1. Problem Statement
The current "Pure Donation" model is underperforming due to:
*   **Low Utility:** Supporting only removes prompts; it provides no functional benefit.
*   **Excessive Generosity:** All features are free, leaving no incentive for power users to pay.
*   **Hidden Prompts:** The 150-hymn annual cap means the most active users see the prompt rarely.
*   **Low Pricing Anchor:** The GH₵ 10/20 tiers are perceived as "tips" rather than "premium value."

## 2. Proposed Model: "Value-Based Support" (Freemium)
Transition to a model where the core reading experience remains free, but "Power User" features are gated behind a one-time or recurring support payment.

### Key Metrics & Goals
*   **Target Conversion:** 3-5% (Up from <1%).
*   **Target ARPU:** GH₵ 0.50 - 1.00 per user.
*   **Model:** Freemium with Soft-Gates (Dismissible but persistent limits).

## 3. Core Tactics

### A. Strategic Friction (Soft Gates)
Introduce generous limits on features used primarily by dedicated users:
*   **Favorites Limit:** Limit free users to **20 favorites**. When the limit is reached, show the paywall to unlock unlimited favorites.
*   **History & Highlights:** Limit search history to 15 items and highlights to 10.
*   **Advanced Personalization:** Gate custom font families (beyond the standard 2) and extra themes (e.g., "Aged Paper," "Midnight") for supporters only.

### B. Intelligent Prompting
*   **Remove Usage Cap:** Eliminate the 150-hymn annual limit on prompts.
*   **Recurring Milestones:** Show the support prompt every 50 hymns read for non-supporters.
*   **Contextual Triggers:** Show the paywall after a "Success Event" (e.g., finding a hymn through search 3 times in one session).

### C. Supporter Recognition
*   **Supporter Badge:** Add a gold badge in the Settings/More menu for those who have contributed.
*   **Liturgical App Icons:** Unlock custom app icons in liturgical colors (Gold, Purple, Green, Red).

## 4. Implementation Roadmap

### Phase 1: The "Soft Gate" (Short Term)
1.  **Favorites Limit:** Update `HymnRepository` and `HymnDetailScreen` to enforce a 20-favorite limit for non-supporters.
2.  **Remove Cap:** Delete `PROMPT_CAP_THRESHOLD` from `PurchaseStorage` and `UsageTrackingManager`.
3.  **Paywall Refresh:** Update paywall copy to emphasize "Unlimited Favorites" and "Supporter Benefits."

### Phase 2: Incentives & Subscriptions (Medium Term)
1.  **Supporter Perks:** Add 3 exclusive font families and a "Supporter Badge."
2.  **Subscription Tiers:** Introduce a GH₵ 5/month recurring option alongside one-time donations.
3.  **Themes:** Implement a "Dark Mode Plus" (OLED Black) for supporters.

### Phase 3: Optimization (Long Term)
1.  **A/B Testing:** Test GH₵ 15/30 pricing vs. GH₵ 10/20.
2.  **Social Proof:** Add a counter on the paywall: *"Join 500+ supporters keeping this app alive."*

## 5. Success Validation
*   Track `purchase_conversion_rate` via Firebase/TraceManager.
*   Monitor `paywall_view_source` to see which gates (Favorites vs. Usage) are most effective.
*   Survey users who reach the Favorites limit but don't pay to refine the limit number.
