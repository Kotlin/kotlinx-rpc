const http = require('@jetbrains/youtrack-scripting-api/http');

const issueUrlPattern = /^https:\/\/github\.com\/Kotlin\/kotlinx-rpc\/issues\/([1-9][0-9]*)$/;
const sourcePattern = /^Original GitHub issue: (https:\/\/github\.com\/Kotlin\/kotlinx-rpc\/issues\/[1-9][0-9]*)[ \t]*(?:\r?\n|$)/;

exports.sourceUrl = function (issue) {
  const match = sourcePattern.exec(issue.description || '');
  return match ? match[1] : null;
};

exports.captureUrl = function (issue) {
  if (issue.extensionProperties.githubIssueUrl) {
    return false;
  }
  const url = exports.sourceUrl(issue);
  if (!url) {
    return false;
  }
  issue.extensionProperties.githubIssueUrl = url;
  return true;
};

function urls(value) {
  const parsed = JSON.parse(value || '[]');
  if (!Array.isArray(parsed) || parsed.some(url => typeof url !== 'string')) {
    throw new Error('The GitHub URL attribute must contain a JSON array of strings.');
  }
  return parsed;
}

function writeUrls(issue, property, values) {
  const value = JSON.stringify(Array.from(new Set(values)));
  if (issue.extensionProperties[property] !== value) {
    issue.extensionProperties[property] = value;
  }
}

exports.isDuplicate = function (issue) {
  return (issue.fields.State && issue.fields.State.name === 'Duplicate') ||
    issue.links['duplicates'].isNotEmpty();
};

exports.relinkIssue = function (issue) {
  exports.captureUrl(issue);
  const url = issue.extensionProperties.githubIssueUrl;
  if (!url) {
    return 'unlinked';
  }
  const duplicate = exports.isDuplicate(issue);
  const target = duplicate ? issue.duplicateRoot : issue;
  const previous = issue.extensionProperties.githubIssueTarget;
  if (previous && (!target || previous.id !== target.id)) {
    previous.extensionProperties.githubIssueSources.delete(issue);
    for (const property of ['githubIssueUrls', 'githubPendingClosureUrls']) {
      writeUrls(previous, property, urls(previous.extensionProperties[property]).filter(value => value !== url));
    }
  }
  if (!target || (duplicate && target.id === issue.id)) {
    issue.extensionProperties.githubIssueTarget = null;
    return 'duplicate-unlinked';
  }
  if (!previous || previous.id !== target.id) {
    issue.extensionProperties.githubIssueTarget = target;
  }
  if (target.id !== issue.id) {
    issue.extensionProperties.githubIssueSources.delete(issue);
    for (const property of ['githubIssueUrls', 'githubPendingClosureUrls']) {
      writeUrls(issue, property, urls(issue.extensionProperties[property]).filter(value => value !== url));
    }
  }
  target.extensionProperties.githubIssueSources.add(issue);
  writeUrls(target, 'githubIssueUrls', urls(target.extensionProperties.githubIssueUrls).concat(url));
  return duplicate ? 'relinked' : 'linked';
};

exports.relinkTree = function (issue, visited) {
  visited = visited || new Set();
  if (visited.has(issue.id)) {
    return;
  }
  visited.add(issue.id);
  exports.relinkIssue(issue);
  issue.links['is duplicated by'].forEach(child => exports.relinkTree(child, visited));
};

function keepOpen(issue, url) {
  if (issue.extensionProperties.githubIssueUrl === url && issue.extensionProperties.githubIssueKeepOpen) {
    return true;
  }
  return !!issue.extensionProperties.githubIssueSources.find(source =>
    source.extensionProperties.githubIssueUrl === url && source.extensionProperties.githubIssueKeepOpen);
}

function closeUrl(ctx, url) {
  const issue = ctx.issue;
  const match = issueUrlPattern.exec(url);
  if (!match) {
    return 'unlinked';
  }
  if (keepOpen(issue, url)) {
    return 'keep-open';
  }
  try {
    const connection = new http.Connection('https://api.github.com');
    connection.bearerAuth(ctx.settings.githubToken);
    connection.addHeader('Accept', 'application/vnd.github+json');
    connection.addHeader('Content-Type', 'application/json');
    connection.addHeader('X-GitHub-Api-Version', '2022-11-28');
    const path = '/repos/Kotlin/kotlinx-rpc/issues/' + match[1];
    const current = connection.getSync(path, null);
    if (current.code !== 200) {
      console.warn('GitHub issue lookup failed for ' + issue.id + ': HTTP ' + current.code);
      return 'pending';
    }
    const githubIssue = JSON.parse(current.response);
    if (githubIssue.pull_request) {
      console.warn('GitHub URL refers to a pull request for ' + issue.id);
      return 'pending';
    }
    if (githubIssue.state === 'closed') {
      return 'already-closed';
    }
    const response = connection.patchSync(path, null, JSON.stringify({state: 'closed'}));
    if (response.code !== 200 || JSON.parse(response.response).state !== 'closed') {
      console.warn('GitHub issue closure failed for ' + issue.id + ': HTTP ' + response.code);
      return 'pending';
    }
    console.log('Closed GitHub issue #' + match[1] + ' for ' + issue.id);
    return 'closed';
  } catch (error) {
    console.warn('GitHub request failed for ' + issue.id + '. The scheduled rule retries the request.');
    return 'pending';
  }
}

exports.closeIssue = function (ctx) {
  const issue = ctx.issue;
  if (!issue.isReported || !issue.isResolved) {
    writeUrls(issue, 'githubPendingClosureUrls', []);
    return 'unresolved';
  }
  if (exports.isDuplicate(issue)) {
    writeUrls(issue, 'githubPendingClosureUrls', []);
    return 'duplicate';
  }
  const results = [];
  const pending = urls(issue.extensionProperties.githubPendingClosureUrls).filter(url => {
    const result = closeUrl(ctx, url);
    results.push(result);
    return result === 'pending';
  });
  writeUrls(issue, 'githubPendingClosureUrls', pending);
  return results.includes('pending') ? 'pending' : (results[0] || 'idle');
};

exports.queueClosure = function (ctx) {
  if (exports.isDuplicate(ctx.issue) || !ctx.issue.isResolved || !ctx.issue.isReported) {
    return;
  }
  writeUrls(ctx.issue, 'githubPendingClosureUrls', urls(ctx.issue.extensionProperties.githubIssueUrls)
    .filter(url => !keepOpen(ctx.issue, url)));
};
