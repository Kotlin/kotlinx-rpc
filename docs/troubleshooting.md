# Troubleshooting

> [!NOTE]
> This document is for **human developers only**.
> AI agents and LLMs should use `CLAUDE.md` and skills for project guidelines instead.

For environment setup instructions, see [environment](../docs/environment.md).
For development workflows, see [workflow](../docs/workflow.md).

Nothing works? Well, you are onto a journey!

Here is a 'simple' guide for solving problems:

- Gradle (he he)
    - Next commands in order from simpler to harder problems.
        - `./gradlew clean`
        - `./gradle --stop`
        - `./gradle <task> --rerun-tasks --no-configuration-cache --no-build-cache`
        - `mv ~/.gradle/gradle.properties ~/gradle.properties.temp && rm -rf ~/.gradle && mkdir ~/.gradle && mv ~/gradle.properties.temp ~/.gradle/gradle.properties` -
        use only as a last resort. Probably never needed, as the actual problem is in your code in 99.999 times out of 100.
    - Can't find dependency in repositories? Check dependencies' coordinates, check proxies, check original repo
      downtime.
    - Take a good look into your changes (or recent changes from remote).
- IDEA
    - Most problems with highlighting: `Menubar` -> `File` -> `Cache Recovery` -> `Repair IDE` and follow instructions
      in popups.
    - Other problems:
        - Check code, maybe you are wrong
        - Resync Gradle (maybe more than once)
        - Remove all code in the file and bring it back
        - Save all local changes and do `git clean -xfd` (Google the command effect before executing)
        - Restart your machine
        - Try different IDE version (last resort)
- Docker
    - `Cannot connect to the Docker daemon` - open `Docker Desktop`
- Kotlin/Js or Kotlin/Wasm
  - `kotlinUpgradeYarnLock` or `kotlinWasmUpgradeYarnLock` (and also `kotlinNpmInstall` or `kotlinWasmNpmInstall`)
  have a funny tendency to fail sometimes, and you don't know why. 
    
    I'll tell you! 
    
    We use proxy repos, and sometimes dependencies get downloaded from the wrong source.
    Make sure ALL urls in `package-lock.json` files start with `https://packages.jetbrains.team/npm/p/krpc/build-deps/`.
    
    If something doesn't work, your steps are:
    - Delete `package-lock.json` file
    - Delete `<REPO_ROOT>/build/js` / `<REPO_ROOT>/build/wasm`
    - Run `kotlinUpgradeYarnLock` / `kotlinWasmUpgradeYarnLock`
    - If the problem persists:
      - Check that `<REPO_ROOT>/build/<target>/.npmrc` AND `<REPO_ROOT>/build/<target>/.yarnrc` are present
      - Check that `.yarnrc` contains one line: `registry: "https://packages.jetbrains.team/npm/p/krpc/build-deps/"`
      - Check that `.npmrc` contains the following lines:
        - `registry="https://packages.jetbrains.team/npm/p/krpc/build-deps/"`
        - `always-auth=true`
        - `save-exact=true`
        - `//packages.jetbrains.team/npm/p/krpc/build-deps/:_authToken=<your_auth_token>`, 
          where `<your_auth_token>` is from the [proxy-repositories.md](proxy-repositories.md) guide.
      - Check that `<USER_HOME>/.npmrc` / `<USER_HOME>/.yarnrc` don't interfere
      command to debug. Replace versions of tools if needed.
  - When you get the following error — `puppeteer` failed to run the installation script. 
    Reasons vary, try updating the version to a newer one, 
    check the [.puppeteerrc.cjs](../.puppeteerrc.cjs) and [chrome_bin.js](../karma/chrome_bin.js) files if they are valid js.
  
        For (2), check out our guide on configuring puppeteer at https://pptr.dev/guides/configuration.
        at ChromeLauncher.resolveExecutablePath (/rpc/build/js/packages/kotlinx-rpc-utils-test/node_modules/puppeteer-core/lib/cjs/puppeteer/node/ProductLauncher.js:295:27)

  - When the previous error is gone, you may get the next one.
         
        Errors occurred during launch of browser for testing.
            - ChromeHeadless
            Please make sure that you have installed browsers.
            Or change it via
            browser {
                testTask {
                    useKarma {
                        useFirefox()
                        useChrome()
                        useSafari()
                    }
                }
            }
    This means the `puppeteer` failed to locate Chrome.
    Either the cache dir is wrong (check [.puppeteerrc.cjs](../.puppeteerrc.cjs) file) or it really isn't there.
    
    Reasons again vary.
    
    - When `npm` installs `puppeteer`, it should execute script to install the browser too
    (On CI to the `<ROOT_DIR>/.puppeteer/browsers` directory).
    This absence may be caused by the `--ignore-scripts` flag. 
    
      Check the clean installation (`rm -rf build && ./gradlew clean cleanJsBrowserTest`) with `--debug` flag.
      (Something like `./gradlew jsBrowserTest --debug`).
    
      The property is set in [npm.kt](../gradle-conventions/src/main/kotlin/util/tasks/npm.kt), see `ignoreScripts`, 
      it should be `false`. 
   
      **IMPORTANT: run in docker with `TEAMCITY_VERSION` env var set, if you are chasing a CI fail**.
    
    - If this is not the case, check the debug log for other `node`-related issues.
      Try installing browsers manually: 
    
          ~/.gradle/nodejs/node-v22.0.0-linux-x64/bin/node build/js/node_modules/puppeteer/install.mjs
    
      If this works — problem is somewhere in KGP and probably your configs. 
    Check that your config (like ones with `ignoreScript`) are actually applied, 
    as they use on demand execution and may target wrong plugin or extension and never be executed.
    
      **Bonus**: it may not be installed, because npm install doesn't do this. 
    See the long comment in [npm.kt](../gradle-conventions/src/main/kotlin/util/tasks/npm.kt).

Something doesn't work, and you are sure it's not your fault? Report it appropriately! Don't be lazy.

**Update this doc if you think the problem you solved is typical.**
