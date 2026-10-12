# GitHub issue sync

The `kxrpc-github-issue-sync` app stores each imported issue's original GitHub URL in the `githubIssueUrl` extension property.
It reads the `Original GitHub issue:` line at the start of the description.
The stored URL doesn't change when the description changes.

When a canonical YouTrack issue becomes resolved, the app closes its linked GitHub issues.
Duplicates only move URL associations to the canonical issue's attributes.
An hourly rule retries failed closure requests.
Successful closures leave the retry queue, so a manual GitHub reopen doesn't cause another closure.
The app only closes issues in `Kotlin/kotlinx-rpc`.

The app uses these extension properties:

| Property | Value |
|---|---|
| `githubIssueUrl` | The original source URL on the imported issue. |
| `githubIssueUrls` | A JSON array of all GitHub URLs associated with the canonical issue. |
| `githubIssueTarget` | The canonical YouTrack issue for the source URL. |
| `githubIssueSources` | Imported source issues associated with the canonical issue. |
| `githubIssueKeepOpen` | An explicit override on the source issue that prevents automatic GitHub closure. |
| `githubPendingClosureUrls` | A JSON array of failed closure requests to retry. |

## Deploy

1. Make a ZIP with the contents of `github-issue-sync/`. Put `manifest.json` at the archive root.
2. Upload the ZIP in YouTrack under **Administration > Apps**.
3. Attach the app to the `KXRPC` project.
4. Set the secret `githubToken` project setting to a GitHub token with permission to write repository issues.

The gRPC source issue `KXRPC-428` has `githubIssueKeepOpen` set to `true`.
This per-issue override follows the source after duplicate relinking.
Other duplicate URLs close when their canonical YouTrack issue becomes resolved.

To change a source issue's override, send an authenticated `POST` request to:

```text
/api/issues/<issueId>/extensionEndpoints/kxrpc-github-issue-sync/issue-settings/keep-open
```

Set the JSON body to `{"keepOpen": true}` to prevent automatic closure, or `{"keepOpen": false}` to allow it.
The request requires permission to update the issue.

Keep the app name and extension properties during app changes.
When you remove the app or property, YouTrack deletes its extension data.

## Check

From the repository root, run:

```bash
node --test .youtrack/github-issue-sync.test.cjs
```
