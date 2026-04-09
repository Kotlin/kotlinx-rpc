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

The file `docs/pages/kotlinx-rpc/v.list` defines variables that are substituted
throughout every topic using `%variable-name%` syntax:

```xml
<var name="kotlinx-rpc-version" value="<LIBRARY_VERSION>"/>
<var name="kotlin-version" value="<KOTLIN_VERSION>"/>
```

Topics reference these as `%kotlinx-rpc-version%` and `%kotlin-version%` in code blocks
and prose, so updating `v.list` propagates the change everywhere automatically.

### Key Topics to Know About

| Topic | What it contains |
|---|---|
| `versions.topic` | Supported Kotlin versions list, compiler plugin versioning scheme |
| `grpc-versions.topic` | Bundled gRPC/Protobuf/Buf dependency versions table |
| `changelog.md` | Auto-generated from root `CHANGELOG.md` via Gradle task |
| `get-started.topic` | Quick-start guide with version-dependent code samples |
| `plugins.topic` | Gradle plugin configuration examples |

## General Writerside Guidelines

1. Topics use Writerside XML format (`.topic` extension) with `<code-block>` for code.
   The changelog is the exception — it's Markdown.
2. Always use `%kotlinx-rpc-version%` and `%kotlin-version%` variables in code examples
   instead of hardcoding version strings.
3. The navigation tree is defined in `rpc.tree` — add new topics there.
4. To sync the docs changelog from the root `CHANGELOG.md`, run the
   `updateDocsChangelog` Gradle task (use the `running_gradle_builds` skill).
   This transforms the root changelog into Writerside-compatible Markdown and writes it
   to `docs/pages/kotlinx-rpc/topics/changelog.md`. The CI workflow at
   `.github/workflows/changelog.yml` verifies this is up to date on every PR.
5. When adding a new migration guide, create a new `.topic` file in `topics/` (naming
   convention: `0-X-0.topic` for version 0.X.0) and add a `<toc-element>` entry under
   the "Migration guides" section in `rpc.tree`.

## Local Writerside Testing

The CI uses Docker image `registry.jetbrains.team/p/writerside/builder` with version
from `DOCKER_VERSION` in `.github/workflows/docs.yml`. To replicate the CI build:

```bash
# Build the docs (same as CI)
docker run --rm \
  -v "$(pwd)/docs/pages/kotlinx-rpc:/docs" \
  -v "$(pwd)/artifacts:/artifacts" \
  registry.jetbrains.team/p/writerside/builder:243.22562

# Extract and view the result
unzip -q artifacts/webHelpRPC2-all.zip -d __docs_preview
open __docs_preview/index.html
```

The CI also runs `JetBrains/writerside-checker-action@v1` which validates links,
references, and document structure against the built artifacts. This catches broken
cross-references and invalid topic IDs.

---

## KDoc and Dokka

### KDoc Conventions

All public types in published modules require KDoc (per CLAUDE.md conventions). The
project follows standard Kotlin KDoc patterns:

- First line: concise summary of what the declaration does
- `@param` for each parameter (including type parameters like `@param T`)
- `@return` for non-Unit return types
- `@see` for cross-references to related APIs
- Code examples in triple-backtick fenced blocks with `kotlin` language tag
- `@property` tags for data class properties when documented at class level

**Example pattern** (from `core/src/commonMain/kotlin/kotlinx/rpc/RpcClient.kt`):
```kotlin
/**
 * Performs a unary RPC call.
 *
 * @param T The expected return type of the call.
 * @param call The [RpcCall] describing the method to invoke.
 * @return The result of the RPC invocation.
 */
```

### Visibility Annotations

- `@InternalRpcApi` — marks internal APIs. The custom Dokka plugin
  (`dokka-plugin/src/main/kotlin/kotlinx/rpc/dokka/HideInternalRpcApiTransformer.kt`)
  strips these from generated API docs. No KDoc required.
- `@ExperimentalRpcApi` — marks experimental APIs. These still appear in docs and
  need KDoc.

### Dokka Build

Dokka is configured via convention plugins in `gradle-conventions/`:
- `conventions-dokka-spec.gradle.kts` — core Dokka config for public modules
- `conventions-dokka-public.gradle.kts` — applied to modules using `includePublic()`

Key settings:
- `failOnWarning = true` — KDoc warnings fail the build (this is the enforcement)
- `documentedVisibilities = {Public, Protected}`
- `suppressObviousFunctions = true`
- Source links point to GitHub at the tagged version

Generate API docs by running the `dokkaGenerate` Gradle task (use the
`running_gradle_builds` skill). Output goes to `docs/pages/api/` — don't edit by hand.

### Generated documentation files — Do not edit manually

Several documentation files are machine-generated. **Never edit them by hand** — run the
regeneration task and commit the output.

The custom Dokka plugin at `dokka-plugin/` also adds version badges to gRPC/protobuf
module pages, configured via `rpc-dokka-config.properties` (auto-generated at build time
with `coreVersion` and `grpcDevVersion`).

---

## README.md

The root `README.md` contains the project overview, quick-start code, Kotlin
compatibility table, supported platforms table, and Gradle setup examples.

### Version references in README

- Kotlin versions list (e.g., "2.1.0, 2.1.10, ... 2.3.0")
- Library version in Gradle plugin and dependency examples (e.g., "0.10.2")
- gRPC dev version badge

### README update rules

The CI workflow `.github/workflows/readme.yml` enforces that `README.md` changes
targeting the `main` branch are **only allowed from `release-*` branches**. Feature
branch PRs that touch README will fail CI. This means README updates happen as part
of the release process, not ad-hoc.

When updating README during a release, keep the same sections and update:
1. Kotlin version badges and compatibility list
2. Library version in all code examples
3. Supported platforms table (if platform support changed)
4. gRPC dev version reference (if applicable)

---

## Internal Workflows: CLAUDE.md and Skills

### CLAUDE.md

The root `CLAUDE.md` is the project instruction file for Claude Code. Update it when:
- Module structure changes (add/remove/rename modules in the Module Map)
- Build commands change
- Conventions change (e.g., new annotation requirements)
- New protocols or major features are added

Keep it concise and factual. It should reflect the current state of the project, not
aspirational plans.

### Skills (`.claude/skills/`)

Skills live in `.claude/skills/<skill-name>/SKILL.md`. Each skill has YAML frontmatter
with `name` and `description`, followed by Markdown instructions. Update skills when:

- A workflow the skill describes changes (e.g., new files involved in version bumping)
- New tools or commands become available
- The skill's instructions lead to incorrect results
- A new repeatable workflow emerges that would benefit from a skill

The `description` field is what triggers the skill — make it specific about when to use
it and include relevant keywords. When editing a skill, verify the file paths and
commands it references still exist in the project.

---

## Workflow: Bumping Kotlin Version

Use this when a new Kotlin release is supported by kotlinx-rpc and the documentation
needs to reflect the new default/latest Kotlin version.

### Files to Update

#### 1. Version catalog (build system source of truth)

**File:** `versions-root/libs.versions.toml`

Update the `kotlin-lang` version:
```toml
kotlin-lang = "<NEW_KOTLIN_VERSION>"
```

This is the build system source of truth. The docs may reference a different Kotlin
version (e.g., the latest stable patch like `2.3.20` while the build uses `2.3.0`).
That's expected — the docs show what users should use, the build uses what the library
compiles against.

#### 2. Writerside version variable

**File:** `docs/pages/kotlinx-rpc/v.list`

Update the `kotlin-version` variable:
```xml
<var name="kotlin-version" value="<NEW_KOTLIN_VERSION>"/>
```

This propagates to every `%kotlin-version%` reference across all topics.

#### 3. Supported versions list

**File:** `docs/pages/kotlinx-rpc/topics/versions.topic`

Update the hardcoded list of supported Kotlin versions. The project supports the last
three major Kotlin releases. When a new major version is added, drop the oldest major
from the list.

For example, if adding Kotlin 2.4.0 support:
```xml
<list>
    <li>2.2.0, 2.2.10, 2.2.20, 2.2.21</li>
    <li>2.3.0, 2.3.20</li>
    <li>2.4.0</li>
</list>
```

Also verify the surrounding prose still makes sense (it says "the last three major
Kotlin releases" with `%kotlin-version%` as the latest).

#### 4. gRPC versions table (if gRPC dependencies changed)

**File:** `docs/pages/kotlinx-rpc/topics/grpc-versions.topic`

If the Kotlin bump also updated gRPC, Protobuf, or Buf versions, update the versions
in the bundled versions table and in the `grpc-netty` code example. Cross-reference
against `versions-root/libs.versions.toml` entries for `grpc`, `protobuf`, and `buf-tool`.

#### 5. README.md

Update the Kotlin versions badge near the top of the file. The badge URL encodes the
supported range directly — double-dash `--` is the range separator:
```markdown
[![Kotlin](https://img.shields.io/badge/kotlin-<MIN>--<MAX>-blue.svg?logo=kotlin)](http://kotlinlang.org)
```
For example, `kotlin-2.1.0--2.3.0` means support from 2.1.0 through 2.3.0.

Also update the Kotlin compatibility list in the body of the README.

Remember: README changes to `main` must go through a `release-*` branch.

#### 6. Platforms table (if platform support changed)

The platforms table in `docs/pages/kotlinx-rpc/topics/platforms.topic` is auto-generated.
Run the `dumpPlatformTable` Gradle task with `--no-configuration-cache` (use the
`running_gradle_builds` skill) to regenerate it. This introspects all public modules'
Gradle configurations and updates the table between `PLATFORMS_TABLE_START` and
`PLATFORMS_TABLE_END` markers. CI verifies this is up to date via
`.github/workflows/platforms.yml`.

### Verification

After making changes, verify using one or more of:
- **IDEA Writerside plugin** — live preview catches broken references
- **Docker build** — see "Local Writerside Testing" above
- **CI** — the `.github/workflows/docs.yml` workflow builds and checks on PR

---

## Workflow: Bumping Library Version

Use this when releasing a new version of kotlinx-rpc (either a release or moving to the
next snapshot). This is more involved because the version appears in several places
beyond just the variable file.

### Release Checklist

#### 1. Version catalog

**File:** `versions-root/libs.versions.toml`

For a release, change the version from snapshot to release:
```toml
kotlinx-rpc = "0.X.Y"          # was "0.X.Y-SNAPSHOT"
```

After release, bump to the next snapshot:
```toml
kotlinx-rpc = "0.X+1.0-SNAPSHOT"
```

For gRPC dev releases, also update:
```toml
grpc-dev = "0.X.Y-grpc-NNN"
```

#### 2. Writerside version variable

**File:** `docs/pages/kotlinx-rpc/v.list`

Update the `kotlinx-rpc-version` variable to match the release:
```xml
<var name="kotlinx-rpc-version" value="0.X.Y"/>
```

This is only done for actual releases (not snapshots) — the docs always show the
latest stable release version to users.

#### 3. Writerside instance version

**File:** `docs/pages/kotlinx-rpc/writerside.cfg`

Update the `version` attribute on the `<instance>` element:
```xml
<instance src="rpc.tree" version="0.X.Y" web-path="/kotlinx-rpc/"/>
```

#### 4. Version switcher

**File:** `docs/pages/kotlinx-rpc/help-versions.json`

Update or add the new version entry. Mark it as current and update the URL path:
```json
[
  {"version":"0.X.Y","url":"/kotlinx-rpc/0.X.Y/","isCurrent":true}
]
```

If keeping previous versions accessible, add them to the array with `"isCurrent":false`.

#### 5. CI docs workflow

**File:** `.github/workflows/docs.yml`

Update the `CONFIG_JSON_VERSION` environment variable:
```yaml
CONFIG_JSON_VERSION: '0.X.Y'
```

This controls the Algolia search index version.

#### 6. README.md

Update library version in all Gradle code examples and the plugin version reference.
Also update the Kotlin versions badge if the supported range changed (see Kotlin
version workflow for badge URL format). README changes to `main` must go through a
`release-*` branch (enforced by CI).

#### 7. Platforms table (if platform support changed)

Run the `dumpPlatformTable` Gradle task with `--no-configuration-cache` (use the
`running_gradle_builds` skill) to regenerate the platform support table in
`docs/pages/kotlinx-rpc/topics/platforms.topic`. CI checks this via
`.github/workflows/platforms.yml`.

#### 8. Migration guide (if needed)

For releases with breaking changes, create a migration guide:

1. Create `docs/pages/kotlinx-rpc/topics/0-X-0.topic` following the pattern of existing
   migration guides (e.g., `0-10-0.topic`).
2. Add it to `rpc.tree` under the "Migration guides" section:
   ```xml
   <toc-element topic="0-X-0.topic"/>
   ```

#### 9. Internal workflows (if needed)

If the release introduces new modules, changes build commands, or alters conventions:
- Update `CLAUDE.md` Module Map and any affected sections
- Update relevant `.claude/skills/` if their referenced paths or commands changed

### Quick Reference: All Files for a Library Release

| File | What to update |
|---|---|
| `versions-root/libs.versions.toml` | `kotlinx-rpc` version |
| `docs/pages/kotlinx-rpc/v.list` | `kotlinx-rpc-version` value |
| `docs/pages/kotlinx-rpc/writerside.cfg` | `version` attribute on `<instance>` |
| `docs/pages/kotlinx-rpc/help-versions.json` | Version entry + `isCurrent` flag |
| `.github/workflows/docs.yml` | `CONFIG_JSON_VERSION` env var |
| `README.md` | Versions badge, code examples, compat list (release branch only) |
| `dumpPlatformTable` Gradle task | Regenerate platforms table (if platform support changed) |
| `CLAUDE.md` | Module map, commands, conventions (if changed) |
| `.claude/skills/` | Any skills referencing changed paths/commands |
| (optional) `topics/0-X-0.topic` + `rpc.tree` | Migration guide for breaking changes |
