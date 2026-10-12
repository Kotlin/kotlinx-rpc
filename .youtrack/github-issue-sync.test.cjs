const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const test = require('node:test');
const vm = require('node:vm');

const appDirectory = path.join(__dirname, 'github-issue-sync');
const originalUrl = 'https://github.com/Kotlin/kotlinx-rpc/issues/123';

function issueSet(values = []) {
  const entries = new Set(values);
  return {
    isNotEmpty: () => entries.size > 0,
    forEach: callback => entries.forEach(callback),
    find: predicate => Array.from(entries).find(predicate),
    add: value => entries.add(value),
    delete: value => entries.delete(value)
  };
}

function links(values = []) {
  return {...issueSet(values), added: issueSet(), removed: issueSet()};
}

function loadModule(name, dependencies) {
  const context = {
    exports: {},
    require: (module) => dependencies[module],
    console: {log() {}, warn() {}}
  };
  vm.runInNewContext(fs.readFileSync(path.join(appDirectory, name + '.js'), 'utf8'), context);
  return context.exports;
}

function fixture({state = 'open', getCode = 200, patchCode = 200, pullRequest = false, throws = false} = {}) {
  const calls = [];
  class Connection {
    constructor(url) { calls.push(['connect', url]); }
    bearerAuth(token) { calls.push(['auth', token]); }
    addHeader(name, value) { calls.push(['header', name, value]); }
    getSync(url) {
      calls.push(['GET', url]);
      if (throws) { throw new Error('Network error'); }
      return {code: getCode, response: JSON.stringify({state, pull_request: pullRequest || undefined})};
    }
    patchSync(url, params, body) {
      calls.push(['PATCH', url, JSON.parse(body)]);
      return {code: patchCode, response: JSON.stringify({state: 'closed'})};
    }
  }
  const github = loadModule('github', {'@jetbrains/youtrack-scripting-api/http': {Connection}});
  const issue = {
    id: 'KXRPC-123',
    isReported: true,
    isResolved: true,
    becomesResolved: false,
    becomesUnresolved: false,
    project: {shortName: 'KXRPC'},
    fields: {State: {name: 'Fixed'}, isChanged: () => false},
    links: {'duplicates': links(), 'is duplicated by': links()},
    description: 'Original GitHub issue: ' + originalUrl + '\n\n---\n\nOther issue: https://github.com/Kotlin/kotlinx-rpc/issues/456',
    extensionProperties: {githubIssueSources: issueSet()}
  };
  const ctx = {issue, settings: {githubToken: 'test-token'}};
  return {github, calls, issue, ctx};
}

test('The source URL is captured once and survives description edits', () => {
  const {github, issue} = fixture();
  assert.equal(github.captureUrl(issue), true);
  assert.equal(issue.extensionProperties.githubIssueUrl, originalUrl);
  issue.description = 'Original GitHub issue: https://github.com/Kotlin/kotlinx-rpc/issues/456';
  assert.equal(github.captureUrl(issue), false);
  assert.equal(issue.extensionProperties.githubIssueUrl, originalUrl);
});

test('Body links, other repositories, pull URLs, and malformed URLs are ignored', () => {
  const {github, issue, ctx, calls} = fixture();
  for (const description of [
    'A reference: ' + originalUrl,
    'Original GitHub issue: https://github.com/other/repo/issues/123',
    'Original GitHub issue: https://github.com/Kotlin/kotlinx-rpc/pull/123',
    'Original GitHub issue: ' + originalUrl + '?other=456',
    'Original GitHub issue: ' + originalUrl + '/456'
  ]) {
    issue.description = description;
    assert.equal(github.captureUrl(issue), false);
  }
  issue.extensionProperties.githubIssueUrl = 'https://example.com/issues/123';
  github.relinkIssue(issue);
  github.queueClosure(ctx);
  assert.equal(github.closeIssue(ctx), 'unlinked');
  assert.equal(calls.length, 0);
});

test('Unresolved issues and drafts do not send GitHub requests', () => {
  const {github, issue, ctx, calls} = fixture();
  github.captureUrl(issue);
  issue.isResolved = false;
  assert.equal(github.closeIssue(ctx), 'unresolved');
  issue.isResolved = true;
  issue.isReported = false;
  assert.equal(github.closeIssue(ctx), 'unresolved');
  assert.equal(calls.length, 0);
});

test('A resolved issue closes only the stored GitHub issue', () => {
  const {github, issue, ctx, calls} = fixture();
  github.relinkIssue(issue);
  github.queueClosure(ctx);
  assert.equal(github.closeIssue(ctx), 'closed');
  assert.deepEqual(calls.filter((call) => call[0] === 'PATCH'), [
    ['PATCH', '/repos/Kotlin/kotlinx-rpc/issues/123', {state: 'closed'}]
  ]);
});

test('Already closed issues and pull requests are not changed', () => {
  for (const options of [{state: 'closed'}, {pullRequest: true}]) {
    const {github, issue, ctx, calls} = fixture(options);
    github.relinkIssue(issue);
    github.queueClosure(ctx);
    assert.equal(github.closeIssue(ctx), options.pullRequest ? 'pending' : 'already-closed');
    assert.equal(calls.some((call) => call[0] === 'PATCH'), false);
  }
});

test('GitHub API errors and network errors remain eligible for retries', () => {
  for (const options of [{getCode: 403}, {getCode: 429}, {patchCode: 500}, {throws: true}]) {
    const {github, issue, ctx} = fixture(options);
    github.relinkIssue(issue);
    github.queueClosure(ctx);
    assert.equal(github.closeIssue(ctx), 'pending');
    assert.equal(issue.extensionProperties.githubIssueUrl, originalUrl);
  }
});

test('The change rule captures new imports and handles resolution transitions', () => {
  const {github, issue, ctx} = fixture();
  const entities = {Issue: {onChange: (rule) => rule}};
  const {rule} = loadModule('on-change', {'@jetbrains/youtrack-scripting-api/entities': entities, './github': github});
  issue.isResolved = false;
  assert.equal(rule.guard(ctx), true);
  rule.action(ctx);
  assert.equal(issue.extensionProperties.githubIssueUrl, originalUrl);
  assert.equal(rule.guard(ctx), false);
  issue.isResolved = true;
  issue.becomesResolved = true;
  assert.equal(rule.guard(ctx), true);
  rule.action(ctx);
});

test('Scheduled retries search only the attached project and resolved issues', () => {
  const {github, issue, ctx} = fixture();
  const entities = {Issue: {onSchedule: (rule) => rule}};
  const {rule} = loadModule('retry', {'@jetbrains/youtrack-scripting-api/entities': entities, './github': github});
  assert.equal(rule.search({project: {shortName: 'KXRPC'}}), 'project: {KXRPC} #Resolved');
  assert.equal(rule.guard(ctx), false);
  issue.extensionProperties.githubPendingClosureUrls = JSON.stringify([originalUrl]);
  assert.equal(rule.guard(ctx), true);
  issue.extensionProperties.githubPendingClosureUrls = '[]';
  assert.equal(rule.guard(ctx), false);
});

test('Duplicate URLs move to the canonical attributes without replacing its own URL', () => {
  const {github, issue, ctx, calls} = fixture();
  const root = {...fixture().issue, id: 'KXRPC-1', extensionProperties: {
    githubIssueSources: issueSet(),
    githubIssueUrl: 'https://github.com/Kotlin/kotlinx-rpc/issues/1',
    githubIssueUrls: '["https://github.com/Kotlin/kotlinx-rpc/issues/1"]'
  }};
  github.relinkIssue(issue);
  issue.fields.State.name = 'Duplicate';
  issue.links['duplicates'] = links([root]);
  issue.duplicateRoot = root;
  assert.equal(github.relinkIssue(issue), 'relinked');
  assert.equal(issue.extensionProperties.githubIssueUrl, originalUrl);
  assert.equal(issue.extensionProperties.githubIssueTarget, root);
  assert.deepEqual(JSON.parse(root.extensionProperties.githubIssueUrls), [root.extensionProperties.githubIssueUrl, originalUrl]);
  assert.deepEqual(JSON.parse(issue.extensionProperties.githubIssueUrls), []);
  github.queueClosure(ctx);
  assert.equal(github.closeIssue(ctx), 'duplicate');
  assert.equal(calls.length, 0);
  assert.equal(root.extensionProperties.githubPendingClosureUrls, undefined);
});

test('Duplicate links prevent closure even when the State is Fixed', () => {
  const {github, issue, ctx, calls} = fixture();
  issue.links['duplicates'] = links([fixture().issue]);
  issue.extensionProperties.githubPendingClosureUrls = JSON.stringify([originalUrl]);
  assert.equal(github.closeIssue(ctx), 'duplicate');
  assert.equal(issue.extensionProperties.githubPendingClosureUrls, '[]');
  assert.equal(calls.length, 0);
});

test('A duplicate without a canonical issue stays open', () => {
  const {github, issue, ctx, calls} = fixture();
  issue.fields.State.name = 'Duplicate';
  assert.equal(github.relinkIssue(issue), 'duplicate-unlinked');
  github.queueClosure(ctx);
  assert.equal(github.closeIssue(ctx), 'duplicate');
  assert.equal(calls.length, 0);
});

test('Changed duplicate roots remove old attributes and pending closures', () => {
  const {github, issue} = fixture();
  const first = {...fixture().issue, id: 'KXRPC-1'};
  const last = {...fixture().issue, id: 'KXRPC-2'};
  issue.fields.State.name = 'Duplicate';
  issue.links['duplicates'] = links([first]);
  issue.duplicateRoot = first;
  github.relinkIssue(issue);
  first.extensionProperties.githubPendingClosureUrls = JSON.stringify([originalUrl]);
  issue.duplicateRoot = last;
  github.relinkIssue(issue);
  assert.equal(first.extensionProperties.githubIssueUrls, '[]');
  assert.equal(first.extensionProperties.githubPendingClosureUrls, '[]');
  assert.deepEqual(JSON.parse(last.extensionProperties.githubIssueUrls), [originalUrl]);
});

test('A source keep-open flag applies after relinking and during retries', () => {
  const {github, issue, ctx, calls} = fixture();
  const source = {...fixture().issue, id: 'KXRPC-1', duplicateRoot: issue};
  source.fields.State.name = 'Duplicate';
  source.extensionProperties.githubIssueUrl = 'https://github.com/Kotlin/kotlinx-rpc/issues/1';
  source.extensionProperties.githubIssueKeepOpen = true;
  github.relinkIssue(source);
  github.relinkIssue(issue);
  github.queueClosure(ctx);
  assert.deepEqual(JSON.parse(issue.extensionProperties.githubPendingClosureUrls), [originalUrl]);
  github.closeIssue(ctx);
  const requestCount = calls.length;
  issue.extensionProperties.githubPendingClosureUrls = JSON.stringify([source.extensionProperties.githubIssueUrl]);
  assert.equal(github.closeIssue(ctx), 'keep-open');
  assert.equal(calls.length, requestCount);
});

test('Successful closures are not repeated when a GitHub issue is reopened', () => {
  const {github, issue, ctx, calls} = fixture();
  github.relinkIssue(issue);
  github.queueClosure(ctx);
  assert.equal(github.closeIssue(ctx), 'closed');
  const requestCount = calls.length;
  assert.equal(github.closeIssue(ctx), 'idle');
  assert.equal(calls.length, requestCount);
});

test('A canonical resolution closes all associated URLs after duplicate relinking', () => {
  const {github, issue, ctx, calls} = fixture();
  const duplicate = {...fixture().issue, id: 'KXRPC-124', description: null, extensionProperties: {
    githubIssueSources: issueSet(),
    githubIssueUrl: 'https://github.com/Kotlin/kotlinx-rpc/issues/124'
  }, duplicateRoot: issue};
  duplicate.fields.State.name = 'Duplicate';
  duplicate.links['duplicates'] = links([issue]);
  github.relinkIssue(duplicate);
  github.relinkIssue(issue);
  github.queueClosure(ctx);
  github.closeIssue(ctx);
  assert.deepEqual(calls.filter(call => call[0] === 'PATCH').map(call => call[1]).sort(), [
    '/repos/Kotlin/kotlinx-rpc/issues/123', '/repos/Kotlin/kotlinx-rpc/issues/124'
  ]);
});

test('Duplicate chains relink descendant URL attributes to the new root', () => {
  const {github, issue, calls} = fixture();
  const parent = {...fixture().issue, id: 'KXRPC-1', description: null};
  const root = {...fixture().issue, id: 'KXRPC-2', description: null};
  const child = {...fixture().issue, id: 'KXRPC-3', description: null};
  issue.fields.State.name = 'Duplicate';
  issue.links['duplicates'] = links([child]);
  issue.duplicateRoot = parent;
  github.relinkIssue(issue);
  parent.links['is duplicated by'] = links([child]);
  child.links['is duplicated by'] = links([issue]);
  parent.duplicateRoot = root;
  issue.duplicateRoot = root;
  github.relinkTree(parent);
  assert.equal(parent.extensionProperties.githubIssueUrls, '[]');
  assert.deepEqual(JSON.parse(root.extensionProperties.githubIssueUrls), [originalUrl]);
  assert.equal(issue.extensionProperties.githubIssueTarget, root);
  assert.equal(calls.length, 0);
});

test('The keep-open endpoint changes only the source override and validates the project and value', () => {
  const {ctx, calls} = fixture();
  const {httpHandler} = loadModule('issue-settings', {});
  const endpoint = httpHandler.endpoints.find(endpoint => endpoint.path === 'keep-open');
  let response;
  ctx.response = {json: value => { response = value; }};
  for (const keepOpen of [true, false]) {
    ctx.request = {json: () => ({keepOpen})};
    endpoint.handle(ctx);
    assert.equal(response.githubIssueKeepOpen, keepOpen);
    assert.equal(ctx.issue.extensionProperties.githubIssueKeepOpen, keepOpen);
  }
  ctx.request = {json: () => ({keepOpen: 'true'})};
  endpoint.handle(ctx);
  assert.equal(ctx.response.code, 400);
  assert.equal(ctx.issue.extensionProperties.githubIssueKeepOpen, false);
  ctx.issue.project.shortName = 'OTHER';
  ctx.request = {json: () => ({keepOpen: true})};
  endpoint.handle(ctx);
  assert.equal(ctx.response.code, 400);
  assert.equal(ctx.issue.extensionProperties.githubIssueKeepOpen, false);
  assert.equal(calls.length, 0);
  assert.equal(ctx.issue.extensionProperties.githubPendingClosureUrls, undefined);
});

test('Changing a resolved State to Duplicate relinks without closing GitHub', () => {
  const {github, issue, ctx, calls} = fixture();
  github.relinkIssue(issue);
  const root = {...fixture().issue, id: 'KXRPC-1'};
  issue.fields.State.name = 'Duplicate';
  issue.fields.isChanged = field => field === 'State';
  issue.duplicateRoot = root;
  const entities = {Issue: {onChange: rule => rule}};
  const {rule} = loadModule('on-change', {'@jetbrains/youtrack-scripting-api/entities': entities, './github': github});
  assert.equal(rule.guard(ctx), true);
  rule.action(ctx);
  assert.equal(issue.extensionProperties.githubIssueTarget, root);
  assert.equal(calls.length, 0);
});
