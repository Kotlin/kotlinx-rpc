# YouTrack Reproducer Comment Template

Posted on the issue during Step 2 (Analyze the Problem) when the agent determines
a reproducer is needed. If no reproducer is needed, post the explanation why.

## Template

If reproducer is needed:
```markdown
### Reproducer

{{code block or step-by-step reproduction}}

**Observed**: {{what happens}}
**Expected**: {{what should happen}}
```

If no reproducer is needed:
```markdown
### Reproducer (not needed)

{{why no reproducer is needed}}
```

## Guidelines

- If the issue already has a good reproducer, reference it instead of duplicating.
- Keep it minimal — smallest code/steps that demonstrate the problem.
