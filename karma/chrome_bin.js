/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

config.set({
    "browsers": ["MyChromeHeadless"],
    "customLaunchers": {
        "MyChromeHeadless": {
            base: "ChromeHeadless",
            flags: [
                "--allow-failed-policy-fetch-for-test",
                "--allow-external-pages",
                "--no-sandbox",
                "--disable-web-security",
                "--disable-setuid-sandbox",
                "--enable-logging",
                "--v=1"
            ]
        }
    },
    "client": {
        captureConsole: true,
        "mocha": {
            timeout: 300000
        }
    }
});

// puppeteer 25+ is ESM-only and its executablePath() returns a Promise, while this file is
// inlined into the synchronous generated karma.conf.js. Resolve the browser path in a
// blocking child process instead. cwd matters twice: import("puppeteer") resolves against
// it, and puppeteer looks up .puppeteerrc.cjs from it.
process.env.CHROME_BIN = require('child_process').execFileSync(
    process.execPath,
    [
        '--input-type=module',
        '-e',
        'console.log(await (await import("puppeteer")).default.executablePath());'
    ],
    {encoding: 'utf8', cwd: __dirname}
).trim();
