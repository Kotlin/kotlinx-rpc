# Release Build Configurations

Release builds publish to three repositories. IDs follow patterns:

- **Sonatype** (Maven Central): `Release_kRPC_ToSonatype_*`
- **Space EAP**: `Release_kRPC_ToSpace_*`
- **Space gRPC**: `Release_kRPC_ToGrpc_*`

A fourth set, `Release_kRPC_ToLocal_*`, releases nothing. See [Test the release configurations](#test-the-release-configurations).

## Key Release Configurations

| Build ID                                | Purpose                                                                 |
|-----------------------------------------|-------------------------------------------------------------------------|
| `Release_kRPC_ToSonatype_All`           | Assemble all artifacts to later release to Sonatype                     |
| `Release_kRPC_Upload_To_Central_Portal` | Upload assembled artifacts (from sonatype type build) to Central Portal |
| `Release_kRPC_ToSpace_All`              | Release everything to Space EAP                                         |
| `Release_kRPC_ToGrpc_All`               | Release everything to Space gRPC.                                       |
| `Release_kRPC_Produce`                  | Generate Dokka documentation                                            |

## Test the release configurations

The "Release, Local" project has the same configurations as the Sonatype project.
Its builds publish to the `build/repo` directory on the agent, so they release nothing.
Use them after a change of the publication setup or of the release configurations.

| Build ID                               | Purpose                                              |
|----------------------------------------|------------------------------------------------------|
| `Release_kRPC_ToLocal_All`             | Run all library, compiler plugin and Gradle plugin releases |
| `Release_kRPC_ToLocal_Compiler_Plugin` | Run only the compiler plugin release                 |
| `Release_Local_Gradle_Plugin`          | Run only the Gradle plugin release                   |

`Release_kRPC_ToLocal_All` needs a library version. Any value is correct:

```bash
teamcity run start Release_kRPC_ToLocal_All --branch main \
    -P reverse.dep.Publish_kRPC_ReleaseReference.env.LIBRARY_VERSION=0.0.0-test
```

Each build keeps the published files in the `publication_artifacts.zip` artifact.
