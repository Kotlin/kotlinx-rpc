# Version bumps in the docs

Change `README.md` only on a `release-*` branch.

## Kotlin version

| File | Change |
|---|---|
| `versions-root/libs.versions.toml` | `kotlin-lang`. The build version can be lower than the version in the docs: the docs give the version for users. |
| `docs/pages/kotlinx-rpc/v.list` | `kotlin-version` |
| `docs/pages/kotlinx-rpc/topics/versions.topic` | The list of supported Kotlin versions, one `<li>` for each major release. The project supports the last three major releases, so remove the oldest one when you add a new one. Check that the text around the list is still correct. |
| `docs/pages/kotlinx-rpc/topics/grpc-versions.topic` | Only if the `grpc`, `protobuf`, or `buf-tool` versions in the catalog changed: the versions table and the `grpc-netty` example. |
| `README.md` | The Kotlin badge and the Kotlin compatibility list. The badge URL has the range with `--` between the versions: `kotlin-2.2.0--2.4.20`. |
| `docs/pages/kotlinx-rpc/topics/platforms.topic` | Only if the platform support changed: run `dumpPlatformTable --no-configuration-cache`. |

## Library version

| File | Change |
|---|---|
| `versions-root/libs.versions.toml` | `kotlinx-rpc = "0.X.Y"` for the release, then the next `-SNAPSHOT` version after the release. For a gRPC dev release, also `grpc-dev = "0.X.Y-grpc-NNN"`. |
| `docs/pages/kotlinx-rpc/v.list` | `kotlinx-rpc-version`. Releases only: the docs show the latest stable release. |
| `docs/pages/kotlinx-rpc/writerside.cfg` | `version` of `<instance>` |
| `docs/pages/kotlinx-rpc/help-versions.json` | `{"version":"0.X.Y","url":"/kotlinx-rpc/0.X.Y/","isCurrent":true}`. An earlier version that stays has `"isCurrent":false`. |
| `.github/workflows/docs.yml` | `CONFIG_JSON_VERSION`, the version of the Algolia search index |
| `README.md` | The library version in the Gradle examples and the plugin version. The Kotlin badge, if the range changed. |
| `docs/pages/kotlinx-rpc/topics/platforms.topic` | Only if the platform support changed: run `dumpPlatformTable --no-configuration-cache`. |
| `docs/pages/kotlinx-rpc/topics/0-X-0.topic`, `rpc.tree` | Only for a release with breaking changes: a migration guide in the form of the existing ones, for example `0-10-0.topic`. |
| `CLAUDE.md`, `.claude/skills/` | Only if modules, commands, or conventions changed. |

## Check

Use the Writerside plugin of IntelliJ IDEA or the Docker build from `SKILL.md`.
`.github/workflows/docs.yml` builds and checks the docs on each PR.
