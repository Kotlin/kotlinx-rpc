# Workflow: Bumping Library Version

Use this when releasing a new version of kotlinx-rpc (either a release or moving to the
next snapshot). This is more involved because the version appears in several places
beyond just the variable file.

## Release Checklist

### 1. Version catalog

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

### 2. Writerside version variable

**File:** `docs/pages/kotlinx-rpc/v.list`

Update the `kotlinx-rpc-version` variable to match the release:
```xml
<var name="kotlinx-rpc-version" value="0.X.Y"/>
```

This is only done for actual releases (not snapshots) — the docs always show the
latest stable release version to users.

### 3. Writerside instance version

**File:** `docs/pages/kotlinx-rpc/writerside.cfg`

Update the `version` attribute on the `<instance>` element:
```xml
<instance src="rpc.tree" version="0.X.Y" web-path="/kotlinx-rpc/"/>
```

### 4. Version switcher

**File:** `docs/pages/kotlinx-rpc/help-versions.json`

Update or add the new version entry. Mark it as current and update the URL path:
```json
[
  {"version":"0.X.Y","url":"/kotlinx-rpc/0.X.Y/","isCurrent":true}
]
```

If keeping previous versions accessible, add them to the array with `"isCurrent":false`.

### 5. CI docs workflow

**File:** `.github/workflows/docs.yml`

Update the `CONFIG_JSON_VERSION` environment variable:
```yaml
CONFIG_JSON_VERSION: '0.X.Y'
```

This controls the Algolia search index version.

### 6. README.md

Update library version in all Gradle code examples and the plugin version reference.
Also update the Kotlin versions badge if the supported range changed (see the
bump-kotlin-version reference for badge URL format). README changes to `main` must go
through a `release-*` branch (enforced by CI).

### 7. Platforms table (if platform support changed)

Run the `dumpPlatformTable` Gradle task with `--no-configuration-cache` (use the
`running_gradle_builds` skill) to regenerate the platform support table in
`docs/pages/kotlinx-rpc/topics/platforms.topic`. CI checks this via
`.github/workflows/platforms.yml`.

### 8. Migration guide (if needed)

For releases with breaking changes, create a migration guide:

1. Create `docs/pages/kotlinx-rpc/topics/0-X-0.topic` following the pattern of existing
   migration guides (e.g., `0-10-0.topic`).
2. Add it to `rpc.tree` under the "Migration guides" section:
   ```xml
   <toc-element topic="0-X-0.topic"/>
   ```

### 9. Internal workflows (if needed)

If the release introduces new modules, changes build commands, or alters conventions:
- Update `CLAUDE.md` Module Map and any affected sections
- Update relevant `.claude/skills/` if their referenced paths or commands changed

## Quick Reference: All Files for a Library Release

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
