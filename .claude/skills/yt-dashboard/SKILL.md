---
name: yt-dashboard
description: >
  Manage the KRPC YouTrack dashboard via REST API. Use this skill whenever the user wants to
  add, remove, update, or inspect widgets on the KRPC project dashboard, create YouTrack reports
  for KRPC, or query dashboard state. Triggers on mentions of: dashboard, widgets, YouTrack
  dashboard, KRPC dashboard, dashboard widget, add widget, remove widget, report widget,
  issue list widget. Also use when the user references the KRPC YouTrack dashboard URL or
  asks about dashboard layout.
---

# KRPC YouTrack Dashboard Management

This skill provides the complete API knowledge needed to manage the KRPC project's YouTrack
dashboard programmatically via REST API using `curl`.

## Authentication

All API calls use Bearer token auth from `$YOUTRACK_TOKEN` env var:

```bash
curl -s -H "Authorization: Bearer $YOUTRACK_TOKEN" "<url>"
```

## Key IDs

| Entity | ID | Notes |
|---|---|---|
| KRPC Dashboard | `656-576042` | https://youtrack.jetbrains.com/dashboard?id=656-576042 |
| KRPC Project | `22-1434` | shortName: `KRPC`, name: `kotlinx.rpc` |
| KRPC Team | `673-452` | name: `kotlinx.rpc Team` |
| YT Service ID | `869c54ca-878c-4f0a-870d-a081021bcdbd` | Used in widget settings |
| jetbrains-team group | `10-3` | For report visibility |

### KRPC Custom Fields

| Field | CustomField ID | ProjectCustomField ID | Type |
|---|---|---|---|
| Type | `112-23` | `123-8752` | `enum[1]` |
| Priority | `112-24` | `123-8753` | `enum[1]` |
| State | `112-25` | `123-8754` | `state[1]` |
| Scope | `112-3802` | `123-10421` | `enum[*]` |
| Target Release | `112-3803` | `123-10422` | `version[1]` |
| Security Severity | `112-3306` | `123-10424` | `enum[1]` |
| Assignee | `112-30` | `133-1535` | `user[1]` |

## Dashboard API

Base URL: `https://youtrack.jetbrains.com/api`

### Read dashboard widgets

```bash
curl -s -H "Authorization: Bearer $YOUTRACK_TOKEN" \
  "$API/dashboards/656-576042/widgets?fields=id,key,\$type,widget(id,\$type),x,y,width,height,settings&\$top=100"
```

### Widget Types

There are 4 widget types used on the dashboard, identified by their `widget.id`:

| Widget ID | Type | Purpose |
|---|---|---|
| `432-50` | Section Header | Divider with a title |
| `432-54` | Issue List | Shows issues matching a search query |
| `432-72` | Report (legacy) | Displays a YT report via `report` object in settings |
| `432-55` | Report (new) | Displays a YT report via `reportId` in settings |

### Adding a Widget

Every widget requires: `$type`, `widget.id`, `key` (unique string), `x`, `y`, `width`, `height`, and `settings`.

The grid is 8 columns wide. Heights vary by content.

```bash
curl -s -X POST -H "Authorization: Bearer $YOUTRACK_TOKEN" -H "Content-Type: application/json" \
  "$API/dashboards/656-576042/widgets?fields=id,key" \
  -d '{
    "$type": "DashboardWidgetEmbedding",
    "widget": {"id": "<widget-type-id>"},
    "key": "<unique-key>",
    "x": 0, "y": 0, "width": 8, "height": 1,
    "settings": "<json-string>"
  }'
```

### Removing a Widget

```bash
curl -s -X DELETE -H "Authorization: Bearer $YOUTRACK_TOKEN" \
  "$API/dashboards/656-576042/widgets/<widget-id>"
```

### Updating a Widget

```bash
curl -s -X POST -H "Authorization: Bearer $YOUTRACK_TOKEN" -H "Content-Type: application/json" \
  "$API/dashboards/656-576042/widgets/<widget-id>?fields=id,key" \
  -d '{ "x": 0, "y": 5, "width": 4, "height": 3, "settings": "<new-settings>" }'
```

## Settings Format

Settings is a JSON string containing a `customWidgetConfig` key whose value is another JSON string.
Use Python to build these safely:

```python
import json
inner = json.dumps({...config...})
outer = json.dumps({"customWidgetConfig": inner})
# outer is what goes into the "settings" field (as a JSON-encoded string in the request body)
```

### Section Header Settings (432-50)

```json
{"petId": "random-dog", "title": "Section Title Here"}
```

### Issue List Settings (432-54)

```json
{
  "search": "#KRPC #Unresolved sort by: Priority",
  "title": "Widget Title",
  "refreshPeriod": 600,
  "youTrack": {
    "id": "869c54ca-878c-4f0a-870d-a081021bcdbd",
    "homeUrl": "https://youtrack.jetbrains.com"
  }
}
```

The `search` field uses standard YouTrack query syntax. Always scope queries with `#KRPC` or
`project: KRPC`. Use `{kotlinx.rpc Team}` for team references.

### Report Widget Settings (432-72)

```json
{
  "service": {"id": "869c54ca-878c-4f0a-870d-a081021bcdbd", "text": "https://youtrack.jetbrains.com"},
  "created": "2026-03-17T00:00:00.000Z",
  "report": {
    "id": "<report-id>",
    "name": "<report-name>",
    "type": "<flatDistribution|issueDistribution|fixRate|cumulativeFlow|issuePerAssignee>",
    "own": true,
    "pinned": true
  }
}
```

## Creating Reports via API

Only some report types can be created via the REST API. The public API does NOT expose axis
configuration for FlatDistributionReport or MatrixReport, so those cannot be created
programmatically. Use issue list widgets as alternatives for those.

### IssuePerAssigneeReport (works)

```bash
curl -s -X POST -H "Authorization: Bearer $YOUTRACK_TOKEN" -H "Content-Type: application/json" \
  "$API/reports?fields=id,name,\$type" \
  -d '{
    "$type": "IssuePerAssigneeReport",
    "name": "[KRPC] Report Name",
    "projects": [{"id": "22-1434"}],
    "query": "State: Open or State: {In Progress}",
    "presentation": "PIE",
    "visibleTo": {"id": "10-3"}
  }'
```

### FixRateReport (works, needs range)

```bash
curl -s -X POST -H "Authorization: Bearer $YOUTRACK_TOKEN" -H "Content-Type: application/json" \
  "$API/reports?fields=id,name,\$type" \
  -d '{
    "$type": "FixRateReport",
    "name": "[KRPC] Report Name",
    "projects": [{"id": "22-1434"}],
    "query": "#Bug #{Security Problem} #Feature",
    "visibleTo": {"id": "10-3"},
    "range": {"$type": "NamedTimeRange", "range": {"$type": "NamedRange", "id": "THIS_YEAR"}}
  }'
```

Available NamedRange IDs: `THIS_WEEK`, `THIS_MONTH`, `THIS_YEAR`.

### FlatDistributionReport / MatrixReport (NOT supported)

The REST API requires an axis field but does not expose the property name for setting it.
All attempts with `xAxis`, `yAxis`, `axis`, `field`, `groupByField`, etc. fail with
"Axis field is required". Use issue list widgets (432-54) as alternatives.

### Deleting a Report

```bash
curl -s -X DELETE -H "Authorization: Bearer $YOUTRACK_TOKEN" "$API/reports/<report-id>"
```

## Existing KRPC Reports

| Report ID | Name | Type |
|---|---|---|
| `209-669` | [KRPC] Fixed vs Reported Bugs and Features | FixRateReport |
| `210-2081` | [KRPC] Open or In Progress Assignees | IssuePerAssigneeReport |

## Helper: Add an Issue List Widget

This is the most common operation. Here's a complete pattern using Python to build settings safely:

```bash
DASHBOARD_ID="656-576042"
YT_URL="https://youtrack.jetbrains.com"

SETTINGS=$(python3 -c "
import json
inner = json.dumps({
    'search': '#KRPC tag: example #Unresolved',
    'title': 'My Widget Title',
    'refreshPeriod': 600,
    'youTrack': {'id': '869c54ca-878c-4f0a-870d-a081021bcdbd', 'homeUrl': 'https://youtrack.jetbrains.com'}
})
outer = json.dumps({'customWidgetConfig': inner})
print(json.dumps(outer))
")

curl -s -X POST -H "Authorization: Bearer $YOUTRACK_TOKEN" -H "Content-Type: application/json" \
  "$YT_URL/api/dashboards/$DASHBOARD_ID/widgets?fields=id,key" \
  -d "{
    \"\$type\": \"DashboardWidgetEmbedding\",
    \"widget\": {\"id\": \"432-54\"},
    \"key\": \"unique_key\",
    \"x\": 0, \"y\": 44, \"width\": 4, \"height\": 3,
    \"settings\": $SETTINGS
  }"
```

## Positioning Tips

- Check current layout first to find open y-positions (sort widgets by y, then x)
- Full-width widgets: `x=0, width=8`
- Half-width side-by-side: `x=0, width=4` and `x=4, width=4`
- Section headers are typically `height=1`
- Issue lists are typically `height=2-3`
- Report widgets are typically `height=3`
