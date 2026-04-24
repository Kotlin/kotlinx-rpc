# Investigation Tips (Phase A.3)

Situational guidance for analyzing tickets. Read the relevant sections when
they apply — neither fires on every issue.

## When a "Bug" ticket is really a usability problem

Not everything filed as a bug is a bug in the `superpowers:systematic-debugging`
sense. Usability problems — confusing behavior, poor defaults, rough
ergonomics — often have an **obvious** root cause (the default is wrong; the
error message is misleading; the API nudges users into a footgun). There's no
"trace backward from incorrect state" to do; the incorrect state is visible in
the design, not hidden in a runtime bug.

Treat these like tasks: document the observation and proposed change in the
triage comment, follow the Standard path in B.2, skip the reproducer. You only
need `systematic-debugging` when you don't yet know **why** the code behaves as
it does. If you already know why and only disagree with the choice, that's a
design change, not a debugging session.

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
