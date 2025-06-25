const {join} = require('path');

const isCI = process.env.TEAMCITY_VERSION || process.env.GITHUB_ACTIONS;

if (isCI) {
    /**
     * @type {import("puppeteer").Configuration}
     */
    module.exports = {
      // Changes the cache location for Puppeteer
      // So that in docker container browsers are included
      cacheDirectory: join(__dirname, '.puppeteer', 'browsers'),
    };
}
