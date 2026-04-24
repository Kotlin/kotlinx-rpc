# Addressing PR Comments

Triggered when the user asks you to address PR feedback after the PR is up. May
take multiple turns. Each turn:

1. **Read all PR comments** — human reviews + the internal checkbox comment.
2. **Prioritize human reviewer comments**. For the internal checkbox comment,
   `[x]` = fix, `[ ]` = skip.
3. **Invoke `superpowers:receiving-code-review`** skill. The skill enforces verification:
   - Verify the reviewer's claim against the actual codebase — don't assume
     they're correct.
   - If a suggestion would break existing functionality, violate YAGNI, or
     conflict with project conventions, push back with technical reasoning in a
     PR comment reply.
   - Implement in priority order: blocking issues first, then simple fixes, then
     complex.
4. **Re-run tests/verifications**, commit, push, re-trigger CI if needed.
5. **Mark comments as handled on GitHub**:
   - Code review comments that were addressed → **resolve the conversation**.
   - Regular (non-code-review) comments with addressed feedback → add a
     **thumbs-up reaction** (👍).
6. **Update the review checkbox comment** per `assets/gh-code-review-comment.md`.
7. **Update the CI checkbox comment** per `assets/gh-ci-report-comment.md`.
8. **Update the issue body** per `assets/yt-issue-update.md` if new information
   was discovered.
9. **Re-request review from all reviewers** — after all fixes are pushed and
   comments resolved, re-request review so reviewers are notified the PR is
   ready for another look.

"Address the feedback" always means **all of the above** — implementing the fixes,
resolving/reacting to comments, updating the checkbox comments, AND re-requesting
review. Never treat the checkboxes or re-request as optional when the user asks
to address feedback.
