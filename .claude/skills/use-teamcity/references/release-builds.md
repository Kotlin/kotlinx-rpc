# Release builds

| Build ID pattern | Publishes to |
|---|---|
| `Release_kRPC_ToSonatype_*` | Maven Central, through Sonatype |
| `Release_kRPC_ToSpace_*` | Space EAP |
| `Release_kRPC_ToGrpc_*` | Space gRPC |
| `Release_kRPC_ToLocal_*` | Nothing. See [Test the release builds](#test-the-release-builds). |

| Build ID | Effect |
|---|---|
| `Release_kRPC_ToSonatype_All` | Assembles all artifacts for Sonatype |
| `Release_kRPC_Upload_To_Central_Portal` | Uploads the artifacts of the Sonatype build to the Central Portal |
| `Release_kRPC_ToSpace_All` | Releases all artifacts to Space EAP |
| `Release_kRPC_ToGrpc_All` | Releases all artifacts to Space gRPC |
| `Release_kRPC_Produce` | Generates the Dokka documentation |

## Test the release builds

The `ToLocal` builds have the steps of the Sonatype builds, but publish to the `build/repo` directory on the agent.
Run them after a change of the publication setup or of the release builds.
Each build has the published files in the `publication_artifacts.zip` artifact.

| Build ID | Runs |
|---|---|
| `Release_kRPC_ToLocal_All` | All library, compiler plugin, and Gradle plugin releases |
| `Release_kRPC_ToLocal_Compiler_Plugin` | The compiler plugin release |
| `Release_Local_Gradle_Plugin` | The Gradle plugin release |

`Release_kRPC_ToLocal_All` needs a library version. Any value is correct:

```bash
teamcity run start Release_kRPC_ToLocal_All --branch main \
    -P reverse.dep.Publish_kRPC_ReleaseReference.env.LIBRARY_VERSION=0.0.0-test
```
