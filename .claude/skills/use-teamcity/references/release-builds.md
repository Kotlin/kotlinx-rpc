# Release Build Configurations

Release builds publish to three repositories. IDs follow patterns:

- **Sonatype** (Maven Central): `Release_kRPC_ToSonatype_*`
- **Space EAP**: `Release_kRPC_ToSpace_*`
- **Space gRPC**: `Release_kRPC_ToGrpc_*`

The `Release_kRPC_ToLocal_*` builds don't release anything: [Test the release configurations](#test-the-release-configurations).

## Key Release Configurations

| Build ID                                | Purpose                                                                 |
|-----------------------------------------|-------------------------------------------------------------------------|
| `Release_kRPC_ToSonatype_All`           | Assemble all artifacts to later release to Sonatype                     |
| `Release_kRPC_Upload_To_Central_Portal` | Upload assembled artifacts (from sonatype type build) to Central Portal |
| `Release_kRPC_ToSpace_All`              | Release everything to Space EAP                                         |
| `Release_kRPC_ToGrpc_All`               | Release everything to Space gRPC.                                       |
| `Release_kRPC_Produce`                  | Generate Dokka documentation                                            |

## Test the release configurations

The `Release_kRPC_ToLocal_*` builds have the steps of the Sonatype builds, but publish to the `build/repo` directory on the agent.
Run them after a change of the publication setup or of the release configurations.

| Build ID                               | Runs                                                     |
|----------------------------------------|----------------------------------------------------------|
| `Release_kRPC_ToLocal_All`             | All library, compiler plugin, and Gradle plugin releases |
| `Release_kRPC_ToLocal_Compiler_Plugin` | The compiler plugin release                              |
| `Release_Local_Gradle_Plugin`          | The Gradle plugin release                                |

`Release_kRPC_ToLocal_All` needs a library version. Any value is correct:

```bash
teamcity run start Release_kRPC_ToLocal_All --branch main \
    -P reverse.dep.Publish_kRPC_ReleaseReference.env.LIBRARY_VERSION=0.0.0-test
```

Each build has the published files in the `publication_artifacts.zip` artifact.
