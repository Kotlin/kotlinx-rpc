# Issue Description Templates

Templates for writing KRPC YouTrack issue descriptions. The team uses Markdown.

**Critical rule: Do NOT include proposed solutions, implementation approaches, or design
suggestions.** The agent's job is to **analyze and report** the problem clearly, not to solve
it. Leave the "how to fix" to the team. Only include an approach if the user explicitly
dictates one.

## Bug Reports

```markdown
## Problem

Clear description of what's broken and the user-visible impact.

## Steps to Reproduce

1. Version info (Kotlin, Gradle, kotlinx-rpc versions)
2. Configuration or setup steps
3. Code that triggers the bug (use fenced code blocks)

## Expected Behavior

What should happen instead.

## Actual Behavior

What actually happens (error messages, stack traces in code blocks).
```

If the bug originates from a GitHub issue, start the description with:
```
Original GitHub issue: <url>

---
```
Then include the reproduction details.

## Features / Tasks

Keep it concise -- state what needs to happen and why. Do not propose how to implement it.

```markdown
Brief description of the work and its motivation.

## Context

Why this matters -- what problem it solves or what it enables.
```

## Meta Issues

Just a brief paragraph describing the umbrella goal and motivation. Subtasks will carry the details.

## Usability / Performance Problems

Similar to bugs, but focus on the current behavior and why it's problematic rather than "broken":

```markdown
## Current Behavior

What happens today and why it's a problem.

## Desired Behavior

How it should work instead.

## Context

Who is affected and how frequently.
```
