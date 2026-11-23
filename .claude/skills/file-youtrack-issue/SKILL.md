---
name: file-youtrack-issue
description: Creates a YouTrack issue in the kotlinx-rpc project (KXRPC), for example a bug report, a feature request, or a task. Gives the type, scope, priority, tags, and description format. For existing issues, use use-youtrack.
---

# File a YouTrack issue

The project key is `KXRPC`. An issue URL is `https://youtrack.jetbrains.com/issue/KXRPC-<N>`.
Issues with the old key `KRPC` resolve to the same issues.

Use the `mcp__youtrack__*` tools. For an operation that the tools don't have (attachments, bulk changes), use the
[REST API](https://www.jetbrains.com/help/youtrack/devportal/api-reference.html):

```bash
curl -s -H "Authorization: Bearer $YOUTRACK_TOKEN" \
  "https://youtrack.jetbrains.com/api/issues?query=project:KXRPC&fields=id,summary"
```

## Steps

1. Search for duplicates. YouTrack matches exact keywords only, so run 3 to 5 queries with different keywords,
   synonyms, and a scope filter: `project: KXRPC flow hang`, `project: KXRPC Scope: {kRPC. Client}`.
   If a possible duplicate exists, show it to the user. The user decides.
2. Show the summary, type, scope, and description to the user, and wait for a confirmation.
   The team sees a new issue immediately.
3. Create the issue with `mcp__youtrack__create_issue`:

   ```
   project: "KXRPC"
   summary: "<summary>"
   description: "<Markdown, see references/description-templates.md>"
   customFields: { "Type": "<type>", "Priority": "Normal", "Scope": ["<scope>"] }
   ```
4. Add the `Vibe-report` tag with `mcp__youtrack__manage_issue_tags`. Each issue that an AI agent creates must have it.
5. Report the issue ID, the URL, and the tags.

## Rules

- `references/krpc-fields.md` has the scopes, types, priorities, and tags.
- Each issue has at least one scope. `Scope` is a JSON array, also for one value.
- A text field takes real newlines. YouTrack shows a literal `\n` as the two characters.
- The description states the problem. Don't propose a solution or an implementation, unless the user dictates one.
- kRPC and gRPC are protocols. Never call them transports in an issue.
- Summary: a noun phrase or an imperative, specific, under about 80 characters.
  Examples: "KLIB unique_name collision", "Add context7.json".
- For an issue that comes from a GitHub issue:
  - Start the summary with `[GitHub]`.
  - Start the description with the `Original GitHub issue: <url>` line.
  - The type is `Bug` unless the user gives another one.
  - Don't add the `github-issue` tag. The import adds it.
- To set a parent issue, pass `parentIssue`.
