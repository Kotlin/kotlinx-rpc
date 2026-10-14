exports.httpHandler = {
  endpoints: [
    {
      scope: 'issue',
      method: 'POST',
      path: 'keep-open',
      permissions: ['UPDATE_ISSUE'],
      handle: function (ctx) {
        if (ctx.issue.project.shortName !== 'KXRPC' && ctx.issue.project.shortName !== 'KRPC') {
          ctx.response.code = 400;
          ctx.response.json({error: 'The issue must belong to the kotlinx-rpc project.'});
          return;
        }
        const keepOpen = ctx.request.json().keepOpen;
        if (typeof keepOpen !== 'boolean') {
          ctx.response.code = 400;
          ctx.response.json({error: 'The keepOpen value must be a boolean.'});
          return;
        }
        ctx.issue.extensionProperties.githubIssueKeepOpen = keepOpen;
        ctx.response.json({githubIssueKeepOpen: keepOpen});
      }
    }
  ]
};
