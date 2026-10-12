const entities = require('@jetbrains/youtrack-scripting-api/entities');
const github = require('./github');

exports.rule = entities.Issue.onChange({
  title: 'Relink duplicate GitHub issues and close canonical issues on resolution',
  guard: function (ctx) {
    const issue = ctx.issue;
    return issue.isReported && (
      (!issue.extensionProperties.githubIssueUrl && !!github.sourceUrl(issue)) ||
      issue.becomesResolved || issue.becomesUnresolved || issue.fields.isChanged('State') ||
      issue.links['duplicates'].added.isNotEmpty() || issue.links['duplicates'].removed.isNotEmpty() ||
      issue.links['is duplicated by'].added.isNotEmpty()
    );
  },
  action: function (ctx) {
    github.relinkTree(ctx.issue);
    if (ctx.issue.becomesResolved) {
      github.queueClosure(ctx);
    }
    github.closeIssue(ctx);
  },
  requirements: {}
});
