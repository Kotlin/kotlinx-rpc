# PR Review Comment Format

Reference for posting and maintaining the internal code review comment on GitHub PRs.

## Initial comment (after Phase 10)

Post skipped warnings and nits from Phase 8 as a single PR comment with checkboxes,
grouped by reviewer agent. Use `gh pr comment <pr-number> --body "..."`.

If all issues were fixed during code review, skip this — no comment needed.

### Template

```markdown
## Internal code review — unaddressed items

The following issues were flagged during the automated code review but not fixed in
this PR. Check the ones you'd like me to address:

**Kotlin style**
- [ ] `path/to/File.kt:42` — (warning) Missing KDoc on public `FooBar` class
- [ ] `path/to/File.kt:87` — (nit) Could use `also` instead of explicit variable

**Concurrency**
- [ ] `path/to/Other.kt:15` — (nit) Consider using `Dispatchers.Default` instead of `Dispatchers.IO` for CPU-bound work
```

## Parsing checkboxes (when addressing PR comments)

Read the comment body and check the markdown:
- `- [x]` → user wants this fixed
- `- [ ]` → user chose to skip for now (may check later in a future turn)

## Updating the comment after fixes

After fixing checked items, edit the comment via `gh api` to restructure it into
two sections. Move fixed items to "Fixed" with strikethrough and commit link:

```markdown
## Internal code review — unaddressed items

Check the ones you'd like me to address:

**Kotlin style**
- [ ] `path/to/File.kt:87` — (nit) Could use `also` instead of explicit variable

---

### Fixed
- ~~`path/to/File.kt:42` — (warning) Missing KDoc on public `FooBar` class~~ (fixed in abc1234)
```

Leave unchecked items in the top section so the user can check them in a future turn.
This keeps the comment a living document across multiple review rounds.
