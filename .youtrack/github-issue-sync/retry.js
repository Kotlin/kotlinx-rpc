const entities = require('@jetbrains/youtrack-scripting-api/entities');
const github = require('./github');

exports.rule = entities.Issue.onSchedule({
  title: 'Retry GitHub issue closures each hour',
  search: function (ctx) {
    return 'project: {' + ctx.project.shortName + '} #Resolved';
  },
  cron: '0 0 * * * ?',
  muteUpdateNotifications: true,
  modifyUpdatedProperties: false,
  guard: function (ctx) {
    const pending = ctx.issue.extensionProperties.githubPendingClosureUrls;
    return !!pending && pending !== '[]';
  },
  action: function (ctx) {
    github.relinkIssue(ctx.issue);
    github.closeIssue(ctx);
  },
  requirements: {}
});
