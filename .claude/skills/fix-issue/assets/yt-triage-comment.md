# YouTrack Triage Comment Template

Posted on the issue during Phase 2 (Analyze the Problem) after the agent has read the
issue and all comments. The triage comment communicates whether the issue needs human
triage before proceeding and provides the agent's initial assessment.

## Template — Triage Not Needed

```markdown
### Triage: not needed

**Assessment**: {{why the agent can proceed autonomously — e.g., "Clear bug report with
a reproducer and isolated scope", "Well-defined feature request with acceptance criteria",
"Task with explicit instructions"}}

**Plan**: {{1-2 sentence summary of what the agent will do}}
```

## Template — Triage Needed

```markdown
### Triage: needed

**Reason**: {{why human input is needed before the agent can proceed — e.g.,
"Ambiguous scope — could be interpreted as X or Y",
"Requires architectural decision between approaches A and B",
"Unclear if this is intended behavior or a bug",
"Impacts public API — needs maintainer sign-off on approach"}}

{{if-reproducer-exists-or-was-created}}
### Reproducer

{{code block or steps that demonstrate the issue — helps the triager make a faster
decision by seeing the problem firsthand}}

### Observed behavior

{{what happens}}

### Expected behavior

{{what the reporter expected or what would be correct}}
{{end-if}}

### Suggested next steps

{{what the agent recommends — e.g., "Clarify whether the fix should preserve backward
compat or is OK to break in the next major", "Decide which of the two approaches is
preferred: (A) ... or (B) ..."}}
```

## When to use each

**Not needed** — the agent posts this and immediately continues with Phase 3+:
- Bug with clear reproduction steps and narrow scope
- Feature request with well-defined acceptance criteria
- Task with explicit instructions (e.g., "rename X to Y", "bump version to Z")
- Documentation update

**Needed** — the agent posts this and **pauses**, reporting to the user that triage
is required before proceeding:
- Ambiguous or conflicting requirements
- Multiple valid approaches with different tradeoffs (especially if public API is involved)
- Issue that might be "working as intended"
- Scope is unclear — could be a small fix or a large refactor
- Issue references external context the agent can't access

## Guidelines

- Always post a triage comment, even when triage is not needed — it serves as an
  audit trail of the agent's initial assessment.
- The reproducer in the "triage needed" template is for the triager's benefit. If the
  issue already has a good reproducer, reference it instead of duplicating.
- Keep the assessment concise — the triager should be able to make a decision in
  under a minute of reading.
