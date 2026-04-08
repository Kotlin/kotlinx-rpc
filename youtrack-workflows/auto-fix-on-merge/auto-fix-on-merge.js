const entities = require('@jetbrains/youtrack-scripting-api/entities');

exports.rule = entities.Issue.onChange({
  title: 'Set state to Fixed when PR is merged',
  guard: (ctx) => {
    return ctx.issue.pullRequests.isChanged;
  },
  action: (ctx) => {
    const issue = ctx.issue;
    issue.pullRequests.forEach(function (pr) {
      if (pr.state && pr.state.name === 'MERGED' && (!pr.previousState || pr.previousState.name !== 'MERGED')) {
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
