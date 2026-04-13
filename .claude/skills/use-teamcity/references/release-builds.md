# Release Build Configurations

Release builds publish to three repositories. IDs follow patterns:

- **Sonatype** (Maven Central): `Release_kRPC_ToSonatype_*`
- **Space EAP**: `Release_kRPC_ToSpace_*`
- **Space gRPC**: `Release_kRPC_ToGrpc_*`

## Key Release Configurations

| Build ID                                | Purpose                                                                 |
|-----------------------------------------|-------------------------------------------------------------------------|
| `Release_kRPC_ToSonatype_All`           | Assemble all artifacts to later release to Sonatype                     |
| `Release_kRPC_Upload_To_Central_Portal` | Upload assembled artifacts (from sonatype type build) to Central Portal |
| `Release_kRPC_ToSpace_All`              | Release everything to Space EAP                                         |
| `Release_kRPC_ToGrpc_All`               | Release everything to Space gRPC.                                       |
| `Release_kRPC_Produce`                  | Generate Dokka documentation                                            |
