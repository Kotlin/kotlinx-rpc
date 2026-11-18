# Updating Golden Test Data

The test data is in `compiler-plugin/src/commonTest/data`. A change of the plugin can
change the FIR or IR output, and then the tests fail on the `.fir.txt` and `.ir.txt`
files. Update them with:

```bash
./scripts/update_test_data.sh <TestRunner> [testMethod]
```

Examples:
- `./scripts/update_test_data.sh BoxTest` -- update all box test golden files
- `./scripts/update_test_data.sh DiagnosticTest` -- update all diagnostic test golden files
- `./scripts/update_test_data.sh BoxTest mySpecificTest` -- update a single test's golden file

The script runs the tests of all Kotlin versions with `-PupdateTestData`. The DevKit sets
the `kotlin.test.update.test.data` system property of the test JVM from this Gradle
property.

**Important**: The Gradle MCP's `additionalSystemProps` sets properties on the Gradle
daemon, NOT on the forked test JVM. Pass `-PupdateTestData` as a Gradle argument.

## Files for different Kotlin versions

- `simple.ir.txt` is the output of the highest supported Kotlin version.
- `simple.240_dev_2631.ir.txt` is the output of the compiler 2.4.0-dev-2631, and of the lower
  versions down to the next file with a version. `grpc.2321.fir.txt` is the same for Kotlin 2.3.21.
- A test fails with `No need for a separate dump file` when a file with a version is the
  same as the file of the next higher version. The test deletes the file before it fails,
  so the next run passes. On TeamCity the test doesn't delete the file.

The test tasks of the versions run in parallel, so one update run can write a file with a
version that a higher version makes redundant. Run the tests again without
`-PupdateTestData`. The tests that report such a file delete it. Then run the tests one more time.

After the update, review the diffs with `git diff` to confirm that the changes match your
expectations.
