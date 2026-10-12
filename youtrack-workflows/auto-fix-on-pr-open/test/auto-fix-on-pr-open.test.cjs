const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const test = require('node:test');
const vm = require('node:vm');

const source = fs.readFileSync(path.join(__dirname, '..', 'auto-fix-on-pr-open.js'), 'utf8');
const fixedInBranch = {name: 'Fixed in branch'};

function pullRequest(overrides = {}) {
  return {
    title: 'KRPC-123 Fix the service',
    text: '',
    state: {name: 'OPEN'},
    url: 'https://github.com/Kotlin/kotlinx-rpc/pull/698',
    ...overrides
  };
}

function runRule({added = [pullRequest()], history = [], issue = {}, response, github = {state: 'open', draft: false}} = {}) {
  const requests = [];
  const warnings = [];
  class Connection {
    constructor(url, sslKeyName, timeout) {
      assert.equal(url, 'https://api.github.com');
      assert.equal(sslKeyName, null);
      assert.equal(timeout, 5000);
      this.headers = {};
    }

    addHeader(name, value) {
      this.headers[name] = value;
    }

    getSync(uri, queryParams) {
      requests.push(uri);
      assert.equal(this.headers.Accept, 'application/vnd.github+json');
      assert.ok(this.headers['User-Agent']);
      assert.equal(queryParams.length, 0);
      if (response instanceof Error) {
        throw response;
      }
      return response === undefined ? {isSuccess: true, response: JSON.stringify(github)} : response;
    }
  }
  const exports = {};
  vm.runInNewContext(source, {
    exports,
    console: {warn: (message) => warnings.push(message)},
    require: (name) => {
      if (name === '@jetbrains/youtrack-scripting-api/entities') {
        return {Issue: {onChange: (rule) => rule}, State: {fieldType: 'state'}};
      }
      assert.equal(name, '@jetbrains/youtrack-scripting-api/http');
      return {Connection};
    }
  });
  const ctx = {
    issue: {
      id: 'KRPC-123',
      isReported: true,
      isResolved: false,
      fields: {State: {name: 'In progress'}},
      pullRequests: Object.assign(history, {
        added: {isNotEmpty: () => added.length > 0, find: (predicate) => added.find(predicate)}
      }),
      ...issue
    },
    State: {FixedInBranch: fixedInBranch}
  };
  const initialState = ctx.issue.fields.State;
  if (exports.rule.guard(ctx)) {
    exports.rule.action(ctx);
  }
  return {state: ctx.issue.fields.State, initialState, requests, warnings, requirements: exports.rule.requirements};
}

test('moves a linked open PR to the required state', () => {
  const result = runRule();
  assert.equal(result.state, fixedInBranch);
  assert.equal(result.requirements.State.FixedInBranch.name, 'Fixed in branch');
  assert.deepEqual(result.requests, ['/repos/Kotlin/kotlinx-rpc/pulls/698']);
});

test('accepts a new OPEN snapshot when the previous state is also OPEN', () => {
  assert.equal(runRule({added: [pullRequest({previousState: {name: 'OPEN'}})]}).state, fixedInBranch);
});

test('processes each linked issue separately', () => {
  const pr = pullRequest({title: 'KRPC-123 KRPC-456 Fix both services'});
  for (const id of ['KRPC-123', 'KRPC-456']) {
    assert.equal(runRule({added: [pr], issue: {id}}).state, fixedInBranch);
  }
});

for (const text of ['Closes KRPC-123', 'fixed: #KRPC-123', 'RESOLVES\nKRPC-123']) {
  test('accepts the closing reference: ' + text.replace('\n', ' '), () => {
    assert.equal(runRule({added: [pullRequest({title: 'Fix the service', text})]}).state, fixedInBranch);
  });
}

for (const github of [
  {state: 'open', draft: true},
  {state: 'open'},
  {state: 'open', draft: null},
  {state: 'closed', draft: false}
]) {
  test('leaves the issue unchanged for GitHub status ' + JSON.stringify(github), () => {
    const result = runRule({github});
    assert.equal(result.state, result.initialState);
  });
}

for (const response of [null, {isSuccess: false}, {isSuccess: true, response: 'invalid JSON'}, new Error('Timeout')]) {
  test('leaves the issue unchanged when the GitHub check fails: ' + String(response), () => {
    const result = runRule({response});
    assert.equal(result.state, result.initialState);
    assert.equal(result.warnings.length, 1);
  });
}

for (const overrides of [
  {state: {name: 'MERGED'}},
  {state: {name: 'DECLINED'}},
  {state: null},
  {title: 'KRPC-1234 Fix the service'},
  {title: 'OTHER-KRPC-123 Fix the service'},
  {title: 'Fix the service', text: 'Related to KRPC-123'},
  {title: 'Fix the service', text: 'Closes KRPC-1234'},
  {url: 'https://gitlab.com/Kotlin/kotlinx-rpc/merge_requests/698'},
  {url: 'https://github.com.example.com/Kotlin/kotlinx-rpc/pull/698'},
  {url: null}
]) {
  test('skips an ineligible PR: ' + JSON.stringify(overrides), () => {
    const result = runRule({added: [pullRequest(overrides)]});
    assert.equal(result.state, result.initialState);
    assert.equal(result.requests.length, 0);
  });
}

for (const issue of [{isReported: false}, {isResolved: true}, {fields: {State: fixedInBranch}}]) {
  test('skips an ineligible issue: ' + JSON.stringify(issue), () => {
    const result = runRule({issue});
    assert.equal(result.state, result.initialState);
    assert.equal(result.requests.length, 0);
  });
}

test('does not replay an old open PR on an unrelated issue update', () => {
  const result = runRule({added: [], history: [pullRequest()]});
  assert.equal(result.state, result.initialState);
  assert.equal(result.requests.length, 0);
});

test('does not replay an old open PR when a different PR is merged', () => {
  const result = runRule({added: [pullRequest({state: {name: 'MERGED'}})], history: [pullRequest()]});
  assert.equal(result.state, result.initialState);
  assert.equal(result.requests.length, 0);
});

test('finds an eligible PR after an ineligible PR', () => {
  const result = runRule({added: [pullRequest({title: 'KRPC-456 Fix another service'}), pullRequest()]});
  assert.equal(result.state, fixedInBranch);
  assert.equal(result.requests.length, 1);
});
