---
name: update-doc
description: Updates the kotlinx-rpc documentation, such as Writerside topics, KDoc and Dokka, README, changelog, migration guides, CLAUDE.md, and skills. Has the checklists for a Kotlin version bump and a library release in the docs.
---

# Update the documentation

| Docs | Location | Notes |
|---|---|---|
| User docs | `docs/pages/kotlinx-rpc/` | Writerside |
| API reference | KDoc in the sources | Dokka writes `docs/pages/api/`. Never edit that directory. |
| `README.md` | Repository root | A PR to `main` can change it only from a `release-*` branch. `.github/workflows/readme.yml` fails other PRs. |
| Agent docs | `CLAUDE.md`, `.claude/skills/` | |
| Docs for people | `docs/environment.md`, `docs/workflow.md`, `docs/troubleshooting.md` | Edit them on request. Don't use them as a source for agent work. |

For a Kotlin version bump or a library release, follow `references/version-bumps.md`.

## Writerside

| File in `docs/pages/kotlinx-rpc/` | Content |
|---|---|
| `topics/` | The topics. XML `.topic` files with `<code-block>`. Only `changelog.md` is Markdown. |
| `rpc.tree` | The navigation tree. Add each new topic here. |
| `v.list` | Variables for all topics: `%kotlinx-rpc-version%`, `%kotlin-version%` |
| `writerside.cfg` | The instance version |
| `help-versions.json` | The version switcher of the published website |

- In a code example, use `%kotlinx-rpc-version%` and `%kotlin-version%`, never a literal version.
- `topics/changelog.md` is generated. Change the root `CHANGELOG.md` and run `updateDocsChangelog`.
  `.github/workflows/changelog.yml` checks it.
- `topics/platforms.topic` has a generated table. Run `dumpPlatformTable --no-configuration-cache`.
  `.github/workflows/platforms.yml` checks it.
- A migration guide is the topic `topics/0-X-0.topic`, with a `<toc-element>` under "Migration guides" in `rpc.tree`.
- `topics/versions.topic` lists the supported Kotlin versions.
  `topics/grpc-versions.topic` lists the versions of the gRPC, Protobuf, and Buf dependencies.

To build the docs as the CI does, use the `DOCKER_VERSION` from `.github/workflows/docs.yml`:

```bash
docker run --rm \
  -v "$(pwd)/docs/pages/kotlinx-rpc:/docs" \
  -v "$(pwd)/artifacts:/artifacts" \
  registry.jetbrains.team/p/writerside/builder:<DOCKER_VERSION>

unzip -q artifacts/webHelpRPC2-all.zip -d __docs_preview
open __docs_preview/index.html
```

The CI also checks links and structure with `JetBrains/writerside-checker-action`.

## KDoc and Dokka

`CLAUDE.md` gives the rule for what gets KDoc. For public API:

- Dokka fails the build on a KDoc warning (`failOnWarning = true`), for public and protected declarations.
- An `@ExperimentalRpcApi` declaration is in the API reference, so it needs KDoc.
  The Dokka plugin in `dokka-plugin/` removes `@InternalRpcApi` declarations.
- `gradle-conventions/src/main/kotlin/conventions-dokka-spec.gradle.kts` has the Dokka configuration.
- To generate the API reference, run `dokkaGenerate`.

## Agent docs

- Update `CLAUDE.md` when modules, build commands, or conventions change. State the current facts only.
- Update a skill when its workflow, a path, or a command changes, or when its instructions gave a wrong result.
  Check that each path and command in the skill exists.
