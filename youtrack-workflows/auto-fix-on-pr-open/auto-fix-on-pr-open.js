const entities = require('@jetbrains/youtrack-scripting-api/entities');
const http = require('@jetbrains/youtrack-scripting-api/http');

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

function isReadyPullRequest(pr) {
  if (!pr.state || pr.state.name !== 'OPEN') {
    return false;
  }
  const match = /^https:\/\/github\.com\/([A-Za-z0-9_.-]+)\/([A-Za-z0-9_.-]+)\/pull\/([0-9]+)\/?$/.exec(pr.url || '');
  if (!match) {
    return false;
  }

  // YouTrack doesn't expose draft status: https://www.jetbrains.com/help/youtrack/devportal/v1-PullRequest.html
  try {
    const connection = new http.Connection('https://api.github.com', null, 5000);
    connection.addHeader('Accept', 'application/vnd.github+json');
    connection.addHeader('User-Agent', 'kotlinx-rpc-youtrack-workflow');
    const response = connection.getSync('/repos/' + match[1] + '/' + match[2] + '/pulls/' + match[3], []);
    if (!response || !response.isSuccess) {
      console.warn('Can\'t check GitHub pull request ' + pr.url + '.');
      return false;
    }
    const pullRequest = JSON.parse(response.response);
    return pullRequest.state === 'open' && pullRequest.draft === false;
  } catch (error) {
    console.warn('Can\'t check GitHub pull request ' + pr.url + '.');
    return false;
  }
}

exports.rule = entities.Issue.onChange({
  title: 'Set state to Fixed in branch when a non-draft PR opens',
  guard: (ctx) => {
    const issue = ctx.issue;
    return issue.isReported && !issue.isResolved
      && issue.fields.State !== ctx.State.FixedInBranch
      && issue.pullRequests.added.isNotEmpty();
  },
  action: (ctx) => {
    const issue = ctx.issue;
    const pullRequest = issue.pullRequests.added.find(function (pr) {
      return prClosesIssue(pr, issue.id) && isReadyPullRequest(pr);
    });
    if (pullRequest) {
      issue.fields.State = ctx.State.FixedInBranch;
    }
  },
  requirements: {
    State: {
      type: entities.State.fieldType,
      FixedInBranch: {name: 'Fixed in branch'}
    }
  }
});
