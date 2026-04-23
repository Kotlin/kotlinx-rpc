const entities = require('@jetbrains/youtrack-scripting-api/entities');

function prClosesIssue(pr, issueId) {
  if (!issueId) {
    return false;
  }
  const titleRe = new RegExp('(?:^|[^A-Za-z0-9-])' + issueId + '(?![A-Za-z0-9-])');
  if (pr.title && titleRe.test(pr.title)) {
    return true;
  }
  const bodyRe = new RegExp(
    '\\b(?:close[sd]?|fix(?:e[sd])?|resolve[sd]?)\\b[\\s:]*#?\\s*' + issueId + '(?![A-Za-z0-9-])',
    'i'
  );
  return !!pr.text && bodyRe.test(pr.text);
}

exports.rule = entities.Issue.onChange({
  title: 'Set state to Fixed when PR is merged',
  guard: (ctx) => {
    return ctx.issue.pullRequests.isChanged;
  },
  action: (ctx) => {
    const issue = ctx.issue;
    const issueId = issue.id;
    issue.pullRequests.forEach(function (pr) {
      const justMerged = pr.state && pr.state.name === 'MERGED'
        && (!pr.previousState || pr.previousState.name !== 'MERGED');
      if (justMerged && prClosesIssue(pr, issueId)) {
        issue.fields.State = ctx.State.Fixed;
      }
    });
  },
  requirements: {
    State: {
      type: entities.State.fieldType,
      Fixed: {}
    }
  }
});
