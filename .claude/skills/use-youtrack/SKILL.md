---
name: use-youtrack
description: Searches, reads, updates, comments on, links, and tags existing YouTrack issues of kotlinx-rpc (KXRPC-NNN, old key KRPC-NNN), including sprint and backlog queries. To create an issue, use file-youtrack-issue.
---

# YouTrack for kotlinx-rpc

The project key is `KXRPC`. An issue URL is `https://youtrack.jetbrains.com/issue/KXRPC-<N>`.
Issues with the old key `KRPC` resolve to the same issues.

`../file-youtrack-issue/references/krpc-fields.md` has the scopes, types, states, priorities, and tags.

## Access

Use the tools of the `youtrack` MCP server. For an operation that the tools don't have (attachments, bulk changes), use the
[REST API](https://www.jetbrains.com/help/youtrack/devportal/api-reference.html):

```bash
curl -s -H "Authorization: Bearer $YOUTRACK_TOKEN" \
  "https://youtrack.jetbrains.com/api/<endpoint>?fields=<fields>"
```

## Search

YouTrack matches exact keywords only. Run several `search_issues` queries with different keywords.

```
project: KXRPC State: Open Type: Bug
project: KXRPC for: me                          # or for: <login>, reporter: me
project: KXRPC tag: {Kxrpc Backlog} Scope: {kRPC. Client}
project: KXRPC State: Fixed updated: {this week} sort by: updated desc
project: KXRPC [Kxrpc] Planning Period: {20.03-24.04 (2026)}
project: KXRPC #unresolved created: {this week}
```

- Put a value with a space or a dot in braces.
- The current sprint is usually the last planning period without a checkmark in `get_issue_fields_schema`.
- `customFieldsToReturn` is `["Type", "State", "Assignee"]` by default.
  Add `"Priority"`, `"Scope"`, `"[Kxrpc] Target Release"`, or `"[Kxrpc] Planning Period"` when you need them.

## Read and update

| Operation | Tool |
|---|---|
| Read an issue with its fields and recent comments | `get_issue` (`recentCommentsCount`) |
| Read all comments | `get_issue_comments` |
| Change the summary, description, or custom fields | `update_issue` |
| Change the assignee | `change_issue_assignee`. Find the login with `find_user`. |
| Add or remove tags | `manage_issue_tags` |
| Comment (Markdown) | `add_issue_comment` |
| Link issues | `link_issues`: `relates to`, `subtask of`, `parent for`, `depends on`, `is required for`, `duplicates` |
| Log work | `log_work` |
| Knowledge base articles | `search_articles`, `get_article`, `create_article`, `update_article` |

- `Scope` is a JSON array, also for one value: `["Housekeeping", "Infra"]`.
- A text field takes real newlines. YouTrack shows a literal `\n` as the two characters.
- When you add tags to an issue, add `Vibe-report` too.
- kRPC and gRPC are protocols. Never call them transports in an issue or a comment.
