# Develocity Usage Tips

## Prefer the `use-develocity` skill

When you need build-scan data, cache hit rates, task timing, or flaky-test
statistics from `ge.jetbrains.com`, invoke the `use-develocity` skill rather
than calling `mcp__develocity__*` tools directly. The skill knows the right
pagination and category selection for the KRPC project.

## Direct `mcp__develocity__*` calls — avoid chat overflow

If you must call `mcp__develocity__get_builds` directly, request **one category
at a time** (`additionalDataToInclude` with a single entry) and start with
`maxBuilds: 10` to survey the response shape before widening. A 50-build query
with `["attributes", "caching", "build_performance"]` produces ~180k characters
in a single JSON line and overflows the chat — it has to be written to a file
and slice-read, costing minutes per query.
