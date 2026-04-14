# Troubleshooting Verification Failures

If verifications fail unexpectedly:

1. **Clean and retry**: Run `clean` via `running_gradle_builds`, then stop daemons with `--stop`, then rerun
2. **Skip caches**: Add `--rerun-tasks --no-configuration-cache --no-build-cache` flags
3. **JS/WASM issues**: Delete `package-lock.json` + `build/{js,wasm}`, then run yarn lock upgrade tasks
