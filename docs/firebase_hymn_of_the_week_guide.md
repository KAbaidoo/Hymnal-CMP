# Firebase Remote Config Guide: Hymn of the Week

This guide explains how to use Firebase Remote Config to manually override the randomly generated "Hymn of the Week" on the Hymnal-CMP Home Screen.

## Overview

By default, the app calculates a deterministic "Hymn of the Week" based on the current year and week number. This guarantees that every user sees the same hymn, and it changes automatically every Monday without any server intervention.

However, if you want to feature a specific hymn for holidays (like Christmas or Easter) or special events, you can provide an override map in Firebase Remote Config using the parameter key `hymn_of_the_week_map`.

## Remote Config Parameter

- **Parameter Key**: `hymn_of_the_week_map`
- **Data Type**: String
- **Default Value**: `""` (Empty String)

## Format syntax

The string is a comma-separated list of key-value pairs.
Each pair is formatted as `YYYY-WW:HymnID`.

- **`YYYY`**: The 4-digit year.
- **`WW`**: The 2-digit week of the year (01 to 53). Note that week numbers are zero-padded (e.g. `05` for week 5).
- **`HymnID`**: The exact ID of the hymn you want to feature (1 to 991).

### Examples

**Single Week Override**
To override the 14th week of 2026 to show Hymn #150:
```text
2026-14:150
```

**Multiple Week Overrides**
To override the 14th week to show #150, and the 51st week (Christmas) to show #55:
```text
2026-14:150, 2026-51:55
```

> [!TIP]
> **Week Calculation**: The week number is calculated mathematically by taking `(DayOfYear - 1)` divided by `7`, plus `1`. For example, January 1st to 7th is week `01`, January 8th to 14th is week `02`, etc.

## Fallback Behavior

If the map string is empty, if the formatting is broken, or if the current week is not explicitly listed in your map, the app will gracefully ignore the Firebase string and simply calculate the fallback deterministic hymn. This means it is 100% safe to leave the parameter empty or unconfigured for most of the year!
