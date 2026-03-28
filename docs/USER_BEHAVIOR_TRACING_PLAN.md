# User Behavior Tracing: 10 Strategies and Implementation Plan

## Objective
Instrument high-value user behavior traces across core journeys (discovery, engagement, retention, support conversion) in the shared KMP app.

## Principles
- Track behavior, not personal identity.
- Keep payloads small and consistent across Android/iOS.
- Use one shared event taxonomy from `commonMain`.
- Add trace source context to every navigation/open event.

## 10 Trace Strategies

1. `start_screen_outcome`
- Why: Measures first-session dropoff and entry path quality.
- Trigger: User auto-navigates, taps CTA, or opens random hymn on start screen.
- Key params: `outcome` (`auto_home|cta_home|random_hymn_open`), `delay_ms`.
- Anchor: `composeApp/src/commonMain/kotlin/com/kobby/hymnal/start/StartScreen.kt`

2. `home_navigation_click`
- Why: Shows what users care about most from home.
- Trigger: Home actions (`search`, categories, favorites, more).
- Key params: `target`, `screen="home"`.
- Anchor: `composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/home/HomeScreen.kt`

3. `update_prompt_interaction`
- Why: Quantifies update friction and forced-update impact.
- Trigger: Update dialog shown and user action.
- Key params: `mandatory`, `latest_version`, `action` (`shown|update_now|later`).
- Anchor: `composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/home/HomeScreen.kt`

4. `search_behavior`
- Why: Measures search intent quality and utility.
- Trigger: Search executed (debounced query) and result open.
- Key params: `query_length`, `results_count`, `opened_hymn_id`, `opened_hymn_category`.
- Anchor: `composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/search/GlobalSearchScreen.kt`

5. `hymn_opened`
- Why: Primary engagement event for retention analysis.
- Trigger: Hymn detail screen load.
- Key params: `hymn_id`, `hymn_category`, `source`, `is_supporter`.
- Anchor: `composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/hymns/HymnDetailScreen.kt`

6. `hymn_detail_action`
- Why: Measures high-intent actions and feature value.
- Trigger: Favorite add/remove, share, font settings open.
- Key params: `action`, `hymn_id`, `hymn_category`.
- Anchor: `composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/hymns/HymnDetailScreen.kt`

7. `highlight_action`
- Why: Deep engagement signal; useful for power-user cohorts.
- Trigger: Highlight add, color change, remove.
- Key params: `action`, `hymn_id`, `selection_length`, `color_index`.
- Anchor: `composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/components/DetailScreen.kt`

8. `more_menu_navigation`
- Why: Indicates secondary feature adoption and support intent.
- Trigger: Item tapped in More menu.
- Key params: `item` (`Favorites|History|Highlights|Support Development`).
- Anchor: `composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/more/MoreScreen.kt`

9. `history_management`
- Why: Identifies content revisit patterns and cleanup behavior.
- Trigger: History item open and clear-history confirmation.
- Key params: `action` (`open_item|clear_all`), `history_count_before_clear`.
- Anchor: `composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/more/HistoryScreen.kt`

10. `support_funnel`
- Why: Core revenue funnel instrumentation.
- Trigger: Paywall viewed, plan selected, purchase/restore attempt and result.
- Key params: `entry_source`, `plan`, `price_available`, `result`, `error_code`.
- Anchor: `composeApp/src/commonMain/kotlin/com/kobby/hymnal/presentation/screens/settings/PayWallScreen.kt`

---

## Implementation Plan (Commenced)

### Phase 1: Shared Tracing Contract (Day 1)
1. Add `TraceManager` interface in `commonMain` with:
- `track(event: String, params: Map<String, String> = emptyMap())`
- optional helpers for strongly-typed events
2. Add DI module to provide `TraceManager` globally.
3. Add no-op default implementation for safety/fallback.

Proposed files:
- `composeApp/src/commonMain/kotlin/com/kobby/hymnal/core/trace/TraceManager.kt`
- `composeApp/src/commonMain/kotlin/com/kobby/hymnal/di/TraceModule.kt`

Acceptance:
- App compiles with tracing enabled/disabled.

### Phase 2: Platform Adapters (Day 1-2)
1. Android adapter to Firebase Analytics custom events.
2. iOS adapter to Firebase Analytics custom events.
3. Add event-name/parameter normalization helpers (snake_case, size limits).

Proposed files:
- `composeApp/src/androidMain/kotlin/com/kobby/hymnal/core/trace/TraceManager.android.kt`
- `composeApp/src/iosMain/kotlin/com/kobby/hymnal/core/trace/TraceManager.ios.kt`

Acceptance:
- Test event appears from Android + iOS on debug builds.

### Phase 3: Instrument 10 Strategies (Day 2-4)
1. Add events at the exact handler points listed above.
2. Ensure every hymn open carries `source`.
3. Add support funnel results for success/failure/restore.

Acceptance:
- All 10 events fire with expected params in smoke test.

### Phase 4: Validation and Guardrails (Day 4)
1. Add lightweight unit tests for event mapping helpers.
2. Add debug logger mode to print event payloads locally.
3. Add privacy audit: confirm no raw hymn text/query text sent.

Acceptance:
- Test checklist passes on Android/iOS.

### Phase 5: Reporting Layer (Day 5)
1. Define KPI views:
- Start -> Home conversion
- Search -> Hymn open conversion
- Hymn open -> Detail actions
- Paywall view -> Purchase success
2. Add docs for interpretation and alert thresholds.

Acceptance:
- Team can read weekly funnel trends from traces.

---

## Event Naming and Payload Conventions
- Event names: lowercase snake_case.
- Param keys: lowercase snake_case.
- Param values: strings only in shared layer.
- Avoid PII and freeform user text.
- Never send full hymn content.

## Risk Controls
- Sampling: keep full coverage initially, revisit if event volume is too high.
- Cardinality: avoid unbounded fields except numeric IDs where necessary.
- Duplication: prevent double-firing during recomposition (use action handlers / guarded effects).

## Rollout Strategy
1. Release with debug validation first.
2. Enable in production with full event set.
3. Review data quality after 7 days.
4. Iterate event payloads only with versioned changes.

## Delivery Checklist
- [x] Shared `TraceManager` added and wired in DI
- [x] Android/iOS adapters implemented
- [x] 10 trace strategies instrumented
- [x] Privacy and payload audit completed
- [x] QA smoke test on Android and iOS
- [x] KPI dashboard definitions documented

## Phase 4 Completion Notes
- Added shared sanitizer: `TraceSanitizer` to normalize event/param names and enforce payload limits.
- Added privacy guardrails to block free-form sensitive keys (`query`, `search_query`, `content`, `text`, `message`, and non-length query variants).
- Added debug trace logging:
  - Android: `Log.d` in debug builds only.
  - iOS: `NSLog` controlled by `debugLoggingEnabled` from `iOSApp.swift`.
- Added unit tests for sanitization and privacy filtering:
  - `composeApp/src/commonTest/kotlin/com/kobby/hymnal/core/trace/TraceSanitizerTest.kt`.

### Privacy Audit Result (Current Instrumentation)
- No trace event currently sends raw search query text.
- No trace event currently sends hymn content or highlighted text payload.
- Traces use metadata only (ids, categories, lengths, actions, results).

## KPI Dashboard Definitions

### Dashboard A: Acquisition and Entry
- KPI: `start_to_home_conversion_rate`
- Formula: users with `start_screen_outcome` in (`auto_home`,`cta_home`) / users with any `start_screen_outcome`
- Slice by: `platform`, `app_version`, `outcome`
- Alert hint: investigate if drops > 15% WoW

### Dashboard B: Home Intent
- KPI: `home_nav_ctr_by_target`
- Formula: count of `home_navigation_click` by `target` / active users that day
- Slice by: `target`, `platform`
- Alert hint: sudden drop in `search` or category targets suggests discoverability regression

### Dashboard C: Search Quality
- KPI: `search_to_open_rate`
- Formula: count(`search_behavior` where `action=result_opened`) / count(`search_behavior` where `action=performed`)
- Supporting KPI: `avg_search_results_count` from `results_count`
- Slice by: `query_length`, `platform`, `app_version`
- Alert hint: low conversion + low results may indicate ranking/index issues

### Dashboard D: Content Engagement
- KPI: `hymn_open_rate`
- Formula: count(`hymn_opened`) / DAU
- Supporting KPIs:
  - `favorites_action_rate` = count(`hymn_detail_action` where action in `favorite_add`,`favorite_remove`) / count(`hymn_opened`)
  - `share_rate` = count(`hymn_detail_action` where `action=share`) / count(`hymn_opened`)
  - `font_settings_rate` = count(`hymn_detail_action` where `action=font_settings_open`) / count(`hymn_opened`)
- Slice by: `source`, `hymn_category`, `is_supporter`

### Dashboard E: Highlight Adoption
- KPI: `highlight_add_rate`
- Formula: count(`highlight_action` where `action=add`) / count(`hymn_opened`)
- Supporting KPI: `highlight_edit_mix` = distribution of `add|color_change|remove`
- Slice by: `hymn_category`, `platform`

### Dashboard F: Library Utility (More + History)
- KPI: `more_menu_distribution`
- Formula: count(`more_menu_navigation`) by `item`
- KPI: `history_clear_rate`
- Formula: count(`history_management` where `action=clear_all`) / users with `history_management` where `action=open_item`
- Slice by: `platform`, `app_version`

### Dashboard G: Support Funnel
- KPI: `paywall_to_purchase_rate`
- Formula: count(`support_funnel` where `action=purchase_result` and `result=success`) / count(`support_funnel` where `action=paywall_viewed`)
- Supporting KPIs:
  - `plan_selection_mix` from `support_funnel` where `action=plan_selected`
  - `purchase_failure_rate` = failed purchase_result / purchase_attempted
  - `restore_success_rate` = restore_result success / restore_attempted
- Slice by: `entry_source`, `plan`, `platform`, `app_version`
- Alert hint: monitor by source (`usage_prompt` vs `more_menu`) to tune prompts

### Dashboard H: Update Friction
- KPI: `update_acceptance_rate`
- Formula: count(`update_prompt_interaction` where `action=update_now`) / count(`update_prompt_interaction` where `action=shown`)
- Supporting KPI: `update_deferral_rate` similarly for `action=later`
- Slice by: `mandatory`, `latest_version`, `platform`

## Dashboard Cadence
- Daily: anomaly checks on search conversion and support funnel.
- Weekly: trend review on engagement and update friction.
- Monthly: source-level cohort comparison (`start_random`, `search`, `favorites`, `history`, `highlights`).

## Creating KPI Dashboards in Firebase (GA4)

### Important Context
- Firebase Analytics visual dashboards are managed in the linked GA4 property.
- Use Firebase for instrumentation and GA4 for reporting/exploration.

### Option 1: Fast Path (No SQL)
1. Open Firebase Console -> Analytics -> `View more in Google Analytics`.
2. In GA4, go to `Reports` -> `Library` (Editor/Admin role required).
3. Create or edit an Overview report and publish it to `Reports snapshot`.
4. Add cards for key events:
   - `start_screen_outcome`
   - `home_navigation_click`
   - `search_behavior`
   - `hymn_opened`
   - `hymn_detail_action`
   - `highlight_action`
   - `more_menu_navigation`
   - `history_management`
   - `support_funnel`
   - `update_prompt_interaction`
5. Build funnels in `Explore` -> `Funnel exploration`:
   - Start -> Home conversion
   - Search performed -> Result opened
   - Paywall viewed -> Purchase success
6. Save explorations and share with the team.

### Option 2: Scalable Path (Recommended for KPI Governance)
1. Enable Firebase Analytics export to BigQuery:
   - Firebase Console -> Project Settings -> Integrations -> BigQuery.
2. Create BigQuery views for normalized KPI logic (conversion denominators, failure rates, source splits).
3. Connect BigQuery to Looker Studio.
4. Create dashboard pages per KPI group:
   - Acquisition & Entry
   - Search Quality
   - Content Engagement
   - Support Funnel
   - Update Friction
5. Add global filters:
   - Date range
   - `platform`
   - `app_version`
   - `entry_source`
   - `plan`
   - `hymn_category`

### KPI-to-Event Mapping (Initial)
1. `start_to_home_conversion_rate`
- Numerator: `start_screen_outcome` with `outcome in (auto_home, cta_home)`
- Denominator: all `start_screen_outcome`

2. `search_to_open_rate`
- Numerator: `search_behavior` where `action=result_opened`
- Denominator: `search_behavior` where `action=performed`

3. `paywall_to_purchase_rate`
- Numerator: `support_funnel` where `action=purchase_result` and `result=success`
- Denominator: `support_funnel` where `action=paywall_viewed`

4. `favorites_action_rate`
- Numerator: `hymn_detail_action` where `action in (favorite_add, favorite_remove)`
- Denominator: `hymn_opened`

5. `highlight_add_rate`
- Numerator: `highlight_action` where `action=add`
- Denominator: `hymn_opened`

### Data Quality Checklist (Before Publishing KPI Dashboard)
- Verify event names appear exactly in lowercase snake_case.
- Confirm no raw query/content payloads are present.
- Confirm parameter cardinality is stable (`source`, `plan`, `entry_source`).
- Validate iOS and Android parity for same actions.
- Validate first 7 days for missing or duplicate events.
