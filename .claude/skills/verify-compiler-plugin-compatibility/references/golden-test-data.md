# Updating Golden Test Data

After fixing CSM templates, the compiler plugin's generated IR/FIR output may change,
causing golden file mismatches in `:tests:compiler-plugin-tests`. Update them with:

```bash
./updateTestData.sh <TestRunner> [testMethod]
```

Examples:
- `./updateTestData.sh BoxTest` -- update all box test golden files
- `./updateTestData.sh DiagnosticTest` -- update all diagnostic test golden files
- `./updateTestData.sh BoxTest mySpecificTest` -- update a single test's golden file

The script passes `-Pkotlin.test.update.test.data=true` to Gradle, which the build script
forwards to the test JVM as a system property. The test framework then overwrites golden
files (`.fir.txt`, `.fir.ir.txt`) with actual output instead of asserting against them.

**Important**: The Gradle MCP's `additionalSystemProps` sets properties on the Gradle
daemon, NOT on the forked test JVM. Do not try to pass `kotlin.test.update.test.data`
that way -- it will have no effect. The property must go through `-P` (Gradle project
property), which the build script at `tests/compiler-plugin-tests/build.gradle.kts:117-139`
explicitly reads and forwards via `systemProperty()`.

If you need to run this through the `running_gradle_tests` skill instead of the shell
script, pass the property as a Gradle `-P` flag in the additional arguments:
`-Pkotlin.test.update.test.data=true`

After updating, review the golden file diffs with `git diff` to confirm the changes match
your expectations, then commit the updated files alongside your CSM template changes.