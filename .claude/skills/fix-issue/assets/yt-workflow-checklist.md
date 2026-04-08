# Workflow Checklist Comment Template

Posted on the issue as the **first comment** when the agent claims the issue (Step 1).
This checklist tracks progress through the workflow and ensures no steps are skipped.
Update checkboxes by editing this comment as each step completes.

## Template

```markdown
<details>
<summary><b>Workflow Checklist</b></summary>

- [ ] **Claim**
  - [ ] Assigned to AI Agent [Kxrpc]
  - [ ] State -> In Progress
  - [ ] Sprint set
  - [ ] Post checklist comment (template)
- [ ] **Analyze**
  - [ ] Read issue body and all comments
  - [ ] Reproducer comment posted (or N/A: _reason_)
  - [ ] Issue body updated with Agent Analysis (template)
- [ ] **Worktree**
  - [ ] Branch created: `{fix,feat,task}/KRPC-N`
- [ ] **Failing test** (or N/A: _reason_)
  - [ ] Test written/modified
  - [ ] Confirmed failure before fix
- [ ] **Fix applied**
  - [ ] Code change done
  - [ ] Tests passing
- [ ] **Documentation** (or N/A: _reason_)
  - [ ] KDoc updated
  - [ ] Writerside topics updated
- [ ] **Local verifications**
  - [ ] run-local-verifications skill invoked
  - [ ] All relevant checks passed
- [ ] **Code review**
  - [ ] 2+ agents spawned in parallel
  - [ ] Errors fixed
- [ ] **Committed**
  - [ ] Fix commit
  - [ ] Test commit (or N/A: _reason_)
  - [ ] Docs commit (or N/A: _reason_)
  - [ ] Other (ABI dumps, generated files) (or N/A: _reason_)
- [ ] **Draft PR**
  - [ ] Pushed
  - [ ] PR created with template body
  - [ ] Label(s) set
  - [ ] Internal review comments posted
- [ ] **CI**
  - [ ] GH Actions passing
  - [ ] TeamCity passing (or skipped: _reason_)
  - [ ] CI failures fixed and re-run
  - [ ] CI report comment posted on PR
- [ ] **PR finalized**
  - [ ] Rebased on latest main
  - [ ] Draft removed
  - [ ] Reviewer Mr3zee assigned
- [ ] **YouTrack update**
  - [ ] State -> Fixed in Branch
  - [ ] Body updated with any new findings
- [ ] **Done**

</details>
```

## Guidelines

- Post this as the **very first comment** on the issue, before the triage comment.
- Update checkboxes by editing this comment (not posting a new one) as you
  complete each step
- For steps marked N/A, replace the empty reason with a brief explanation
  (e.g., `N/A: test-only change, no docs needed`).
- The maintainer will check this comment to understand workflow progress.
