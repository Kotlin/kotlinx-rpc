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
- `simple.2321.ir.txt` is the output of Kotlin 2.3.21, and of the lower versions down to
  the next file with a version.
- A test fails with `No need for a separate dump file` when a file with a version is the
  same as the file of the next higher version. Delete the file.

The test tasks of the versions run in parallel, so one update run can write a file with a
version that a higher version makes redundant. Run the tests again without
`-PupdateTestData` and delete the files that the tests report.

After the update, review the diffs with `git diff` to confirm that the changes match your
expectations.
