# Workflow: Bumping Kotlin Version

Use this when a new Kotlin release is supported by kotlinx-rpc and the documentation
needs to reflect the new default/latest Kotlin version.

## Files to Update

### 1. Version catalog (build system source of truth)

**File:** `versions-root/libs.versions.toml`

Update the `kotlin-lang` version:
```toml
kotlin-lang = "<NEW_KOTLIN_VERSION>"
```

This is the build system source of truth. The docs may reference a different Kotlin
version (e.g., the latest stable patch like `2.3.20` while the build uses `2.3.0`).
That's expected — the docs show what users should use, the build uses what the library
compiles against.

### 2. Writerside version variable

**File:** `docs/pages/kotlinx-rpc/v.list`

Update the `kotlin-version` variable:
```xml
<var name="kotlin-version" value="<NEW_KOTLIN_VERSION>"/>
```

This propagates to every `%kotlin-version%` reference across all topics.

### 3. Supported versions list

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

### 4. gRPC versions table (if gRPC dependencies changed)

**File:** `docs/pages/kotlinx-rpc/topics/grpc-versions.topic`

If the Kotlin bump also updated gRPC, Protobuf, or Buf versions, update the versions
in the bundled versions table and in the `grpc-netty` code example. Cross-reference
against `versions-root/libs.versions.toml` entries for `grpc`, `protobuf`, and `buf-tool`.

### 5. README.md

Update the Kotlin versions badge near the top of the file. The badge URL encodes the
supported range directly — double-dash `--` is the range separator:
```markdown
[![Kotlin](https://img.shields.io/badge/kotlin-<MIN>--<MAX>-blue.svg?logo=kotlin)](http://kotlinlang.org)
```
For example, `kotlin-2.1.0--2.3.0` means support from 2.1.0 through 2.3.0.

Also update the Kotlin compatibility list in the body of the README.

Remember: README changes to `main` must go through a `release-*` branch.

### 6. Platforms table (if platform support changed)

The platforms table in `docs/pages/kotlinx-rpc/topics/platforms.topic` is auto-generated.
Run the `dumpPlatformTable` Gradle task with `--no-configuration-cache` (use the
`running_gradle_builds` skill) to regenerate it. This introspects all public modules'
Gradle configurations and updates the table between `PLATFORMS_TABLE_START` and
`PLATFORMS_TABLE_END` markers. CI verifies this is up to date via
`.github/workflows/platforms.yml`.

## Verification

After making changes, verify using one or more of:
- **IDEA Writerside plugin** — live preview catches broken references
- **Docker build** — see "Local Writerside Testing" in the main skill
- **CI** — the `.github/workflows/docs.yml` workflow builds and checks on PR
