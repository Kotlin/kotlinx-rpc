# Audit-Style Ticket Scoping

Tickets framed as "Audit X", "Categorize Y", "Review Z", or "Audit and
reduce N" are a trap because the framing invites bundling multiple small
fixes. The primary deliverable is the **analysis** in the triage comment and
Agent Analysis section — not code. Bundling muddies the analysis and bloats
the diff without strengthening the finding.

For these tickets:

1. **Pick exactly ONE concrete code change** that reduces the audited surface
   area. Optimize for clarity and reviewability over breadth — a single,
   well-scoped reduction is easier to review and land than a pile of
   mechanically-similar edits.
2. **File follow-up issues** for every other finding — do not bundle them into
   the PR. The YT backlog is the right place for other findings.
3. **If you can't identify a single safe code change, the PR may be
   analysis-only.** Say so explicitly in the PR body. A ticket whose
   deliverable is "we audited this and found nothing worth fixing right now"
   is a legitimate outcome; document it that way.

If you re-scope the implementation more than twice in Phase B, stop and
re-read this section — scope thrashing on an audit is the symptom this rule
exists to prevent.
