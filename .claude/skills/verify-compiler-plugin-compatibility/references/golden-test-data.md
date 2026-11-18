# Update the golden test data

The test data is in `compiler-plugin/src/commonTest/data`. When a change of the plugin
changes the FIR or IR output, the tests fail on the `.fir.txt` and `.ir.txt` files.
Update the files with the script:

| Command | Updates |
|---|---|
| `./scripts/update_test_data.sh BoxTest` | All box tests |
| `./scripts/update_test_data.sh DiagnosticTest` | All diagnostic tests |
| `./scripts/update_test_data.sh BoxTest mySpecificTest` | One test |

The script runs the tests of all Kotlin versions with the `-PupdateTestData` Gradle
argument. Don't use `additionalSystemProps` of the Gradle MCP for this. It sets
properties on the Gradle daemon, not on the test JVM.

After the update, check with `git diff` that the changes are the ones that you expect.

## Redundant files

[Expected outputs for different Kotlin versions](../../../../compiler-plugin/README.md#expected-outputs-for-different-kotlin-versions)
describes the files with a version in the name, for example `simple.240_dev_2631.ir.txt`.

A test fails with `No need for a separate dump file` when such a file is the same as the
file of the next higher version. The test deletes the file before it fails. On TeamCity,
the test doesn't delete the file.

The test tasks of the Kotlin versions run in parallel, so an update can write such a
file. After each update:

1. Run the tests without `-PupdateTestData`. Each test that fails with this message
   deletes its file.
2. Run the tests again.
