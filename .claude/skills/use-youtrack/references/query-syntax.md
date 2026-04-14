# YouTrack Query Syntax for KRPC

YouTrack has **no semantic search** — it matches exact keywords only. Always run multiple
queries with different keyword combinations to get good coverage.

## Quick reference

```
project: KRPC                          # All KRPC issues
project: KRPC State: Open             # Open issues
project: KRPC for: me                 # Assigned to current user
project: KRPC for: alexander.sysoev   # Assigned to specific user
project: KRPC reporter: me            # Reported by current user
project: KRPC tag: {kRPC 2.0}         # By tag (braces for multi-word)
project: KRPC Scope: {kRPC. Client}   # By scope (braces for values with dots/spaces)
project: KRPC Type: Bug State: Open   # Combine filters
project: KRPC created: {this week}    # Recent issues
project: KRPC updated: today          # Updated today
project: KRPC sort by: created desc   # Sort order
project: KRPC #resolved              # Resolved issues
project: KRPC #unresolved            # Unresolved issues
```

## Common search patterns

**Current sprint work** (use the latest planning period value):
```
project: KRPC [Kxrpc] Planning Period: {20.03-24.04 (2026)} sort by: updated desc
```
To find the current sprint, look for the most recent planning period that has issues
in progress. You can check with `mcp__youtrack__get_issue_fields_schema` — the last
non-checkmarked period in the enum is typically the active one.

**Backlog for a specific scope:**
```
project: KRPC tag: {Kxrpc Backlog} Scope: {kRPC}
```

**Recently closed:**
```
project: KRPC State: Fixed updated: {this week} sort by: updated desc
```

**Issues by sprint (Planning Period):**
```
project: KRPC [Kxrpc] Planning Period: {20.03-24.04 (2026)}
```

Use `mcp__youtrack__search_issues` for these queries. Request relevant custom fields via
`customFieldsToReturn` — the defaults are `["Type", "State", "Assignee"]` but you can add
`"Priority"`, `"Scope"`, `"[Kxrpc] Target Release"`, `"[Kxrpc] Planning Period"` (sprint) as needed.
