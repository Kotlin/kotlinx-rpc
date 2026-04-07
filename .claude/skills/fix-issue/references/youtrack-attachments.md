# YouTrack File Attachments via REST API

Attach files to issues when the MCP server doesn't support attachments directly.
Uses the `youtrack-agent` credentials per the fix-issue skill's auth rules.

## Attach a file to an issue

```bash
curl -s \
  -H "Authorization: Bearer $YOUTRACK_AGENT_TOKEN" \
  -F "file=@<local-file-path>" \
  "https://youtrack.jetbrains.com/api/issues/<issue-id>/attachments?fields=id,name,url"
```

**Example** — attach a plan document to KRPC-540:
```bash
curl -s \
  -H "Authorization: Bearer $YOUTRACK_AGENT_TOKEN" \
  -F "file=@/tmp/KRPC-540-plan.md" \
  "https://youtrack.jetbrains.com/api/issues/KRPC-540/attachments?fields=id,name,url"
```

**Response** (success):
```json
{
  "id": "attachment-id",
  "name": "KRPC-540-plan.md",
  "url": "/api/files/..."
}
```

## Notes

- The `-F` flag sends the file as `multipart/form-data` — this is what the YT API expects.
- The `file` field name is required. Other field names will be ignored.
- Max file size depends on the YouTrack instance configuration (typically 100 MB).
- The `fields` query parameter controls what's returned. Use `id,name,url` to confirm
  the upload succeeded and get a link.
- If the endpoint returns 404, verify the issue ID exists. If 401/403, the token may
  have insufficient permissions.

## Official docs

[YouTrack REST API — Attach Files to an Issue](https://www.jetbrains.com/help/youtrack/devportal/api-usecase-attach-files.html)
