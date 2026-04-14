---
name: update-doc
description: >
  Update kotlinx-rpc documentation — version bumps, content edits, Writerside topics,
  KDoc, README, skills, and internal workflows. Use this skill whenever the user wants to
  bump the Kotlin version in docs, bump the library version, update the changelog, edit
  documentation topics, add a migration guide, update the version switcher, fix version
  references, write or update KDoc on public APIs, update the README, update Claude skills,
  or modify CLAUDE.md. Also trigger when the user mentions "update docs", "doc version",
  "release docs", "Writerside", "v.list", "changelog", "KDoc", "Dokka", "README",
  "skill", "CLAUDE.md", or wants to sync documentation with a new release.
---

# Updating kotlinx-rpc Documentation

**Gradle tasks:** Always use the `running_gradle_builds` skill (or `running_gradle_tests`
for test tasks) to execute Gradle. Never run `./gradlew` directly via shell.

This project has several documentation surfaces:
- **Writerside** — User-facing docs at `docs/pages/kotlinx-rpc/`
- **KDoc + Dokka** — API reference generated from source code
- **README.md** — Project overview and quick start
- **Internal workflows** — `CLAUDE.md`, `.claude/skills/`, `docs/workflow.md`
- **Human-only** — `docs/environment.md`, `docs/workflow.md`, `docs/troubleshooting.md` (edit when asked, but do not use as a reference for agent work)

## Reference Files

Read the appropriate reference when performing these tasks:

| Task | Reference to read |
|---|---|
| Bumping Kotlin version in docs | `references/bump-kotlin-version.md` |
| Releasing / bumping library version | `references/bump-library-version.md` |
| Writing KDoc, configuring Dokka, API docs | `references/kdoc-and-dokka.md` |

For general doc edits, Writerside topics, README updates, or internal workflow changes,
the guidance below is sufficient — no reference file needed.

---

## Documentation Structure

```
docs/pages/kotlinx-rpc/
  writerside.cfg          # Main Writerside config (instance version lives here)
  rpc.tree                # Navigation tree (TOC)
  v.list                  # Version variables used across all topics
  c.list                  # Categories (empty)
  help-versions.json      # Version switcher for the published site
  cfg/buildprofiles.xml   # Build profiles (Algolia, styling)
  topics/                 # 35+ .topic and .md files
  images/                 # Image assets
```

### Version Variables (`v.list`)

The file `docs/pages/kotlinx-rpc/v.list` defines variables substituted throughout every
topic using `%variable-name%` syntax:

```xml
<var name="kotlinx-rpc-version" value="<LIBRARY_VERSION>"/>
<var name="kotlin-version" value="<KOTLIN_VERSION>"/>
```

Topics reference these as `%kotlinx-rpc-version%` and `%kotlin-version%`, so updating
`v.list` propagates the change everywhere automatically.

### Key Topics

| Topic | What it contains |
|---|---|
| `versions.topic` | Supported Kotlin versions list, compiler plugin versioning scheme |
| `grpc-versions.topic` | Bundled gRPC/Protobuf/Buf dependency versions table |
| `changelog.md` | Auto-generated from root `CHANGELOG.md` via Gradle task |
| `get-started.topic` | Quick-start guide with version-dependent code samples |
| `plugins.topic` | Gradle plugin configuration examples |

---

## Writerside Guidelines

1. Topics use Writerside XML format (`.topic` extension) with `<code-block>` for code.
   The changelog is the exception — it's Markdown.
2. Always use `%kotlinx-rpc-version%` and `%kotlin-version%` variables in code examples
   instead of hardcoding version strings.
3. The navigation tree is defined in `rpc.tree` — add new topics there.
4. To sync the docs changelog from the root `CHANGELOG.md`, run the
   `updateDocsChangelog` Gradle task (use the `running_gradle_builds` skill).
   CI at `.github/workflows/changelog.yml` verifies this is up to date on every PR.
5. When adding a new migration guide, create a `.topic` file in `topics/` (naming
   convention: `0-X-0.topic`) and add a `<toc-element>` entry under "Migration guides"
   in `rpc.tree`.

### Local Writerside Testing

The CI uses Docker image `registry.jetbrains.team/p/writerside/builder` with version
from `DOCKER_VERSION` in `.github/workflows/docs.yml`. To replicate:

```bash
docker run --rm \
  -v "$(pwd)/docs/pages/kotlinx-rpc:/docs" \
  -v "$(pwd)/artifacts:/artifacts" \
  registry.jetbrains.team/p/writerside/builder:243.22562

unzip -q artifacts/webHelpRPC2-all.zip -d __docs_preview
open __docs_preview/index.html
```

CI also runs `JetBrains/writerside-checker-action@v1` which validates links, references,
and document structure.

---

## README.md

The root `README.md` contains the project overview, quick-start code, Kotlin
compatibility table, supported platforms table, and Gradle setup examples.

**Version references:** Kotlin versions list, library version in Gradle examples, gRPC
dev version badge.

**Update rule:** CI (`.github/workflows/readme.yml`) enforces that `README.md` changes
targeting `main` are only allowed from `release-*` branches. Feature branch PRs that
touch README will fail CI.

---

## Internal Workflows: CLAUDE.md and Skills

### CLAUDE.md

Update root `CLAUDE.md` when: module structure changes, build commands change,
conventions change, or new protocols/major features are added. Keep it concise and
factual — current state, not aspirational plans.

### Skills (`.claude/skills/`)

Skills live in `.claude/skills/<skill-name>/SKILL.md` with YAML frontmatter (`name`,
`description`) and Markdown instructions. Update when: a workflow changes, new
tools/commands become available, instructions lead to incorrect results, or a new
repeatable workflow emerges. The `description` field triggers the skill — make it
specific and include relevant keywords. When editing, verify referenced file paths and
commands still exist.
