HeaderInfo = provider(fields = ["headers"])

def _zip_impl(ctx):
    cc = ctx.attr.target[CcInfo].compilation_context
    all_hdrs = cc.direct_headers
    hdrs = all_hdrs.to_list() if type(all_hdrs) == "depset" else all_hdrs  # normalize

    manifest = ctx.actions.declare_file(ctx.label.name + ".headers.list")
    outzip = ctx.actions.declare_file(ctx.label.name + ".zip")

    ctx.actions.write(manifest, "\n".join([f.path for f in hdrs]))
    ctx.actions.run_shell(
        inputs = hdrs + [manifest],
        outputs = [outzip],
        command = r"""
/usr/bin/env python3 - <<'PY'
import os, zipfile
out = os.environ['OUT']
man = os.environ['MAN']

# Ensure parent dir exists
os.makedirs(os.path.dirname(out), exist_ok=True)

with zipfile.ZipFile(out, 'w', compression=zipfile.ZIP_DEFLATED) as z:
    with open(man) as f:
        for p in f.read().splitlines():
            if '/include/' not in p:
                continue
            # arcname keeps the include/ prefix exactly once
            rel = p.split('/include/', 1)[1]
            arc = os.path.join('include', rel)
            if os.path.isfile(p):
                z.write(p, arc)
PY
""",
        env = {"OUT": outzip.path, "MAN": manifest.path},
    )
    return [DefaultInfo(files = depset([outzip]))]

zip_include = rule(
    implementation = _zip_impl,
    attrs = {
        "target": attr.label(),
    },
)
