/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

const {join} = require('path');

const isTC = process.env.TEAMCITY_VERSION;
const isGA = process.env.GITHUB_ACTIONS;

if (isTC !== undefined || isGA !== undefined) {
    /**
     * @type {import("puppeteer").Configuration}
     */
    module.exports = {
      // Changes the cache location for Puppeteer
      // So that in docker container browsers are included
      cacheDirectory: join(__dirname, '.puppeteer', 'browsers'),
    };
}
