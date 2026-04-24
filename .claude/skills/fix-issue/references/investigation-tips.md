# Investigation Tips (Phase A.3)

Situational guidance for analyzing tickets. Read the relevant sections when
they apply — neither fires on every issue.

## Search for similar issues — MANDATORY

Before root cause or planning, check whether this problem has been seen before
or relates to other tracked work. Search **both** YouTrack and GitHub — prior
issues may contain root cause analysis, attempted fixes, or workarounds that
save significant time.

1. **YouTrack**: search by similar keywords, affected modules, or error
   messages. Look at both open and resolved issues — a resolved issue may
   contain the exact fix pattern you need; an open duplicate means link rather
   than duplicate effort.
2. **GitHub**: search issues and PRs via `gh-bot.sh search issues ...` and
   `gh-bot.sh search prs ...`. Closed PRs may contain relevant discussion or
   reverted approaches worth knowing about.

If you find related issues, **note them in the triage comment** and **link
them in YouTrack**. If you find an exact duplicate, stop and report rather
than doing redundant work.

## Dependency source investigation gotcha

When researching library internals via `search_dependency_sources`, FULL_TEXT
mode may return "no dependencies found with indices" for method-level searches
even when the class exists. Workaround: use DECLARATION mode to find the class
first, then `read_dependency_sources` with pagination to read the section you
need.
