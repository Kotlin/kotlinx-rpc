HeaderInfo = provider(fields = ["headers_dir", "headers"])

# determines if the file is in the given repository
def _same_repo(file, repo_name):
    p = file.path
    if repo_name:
        return ("external/%s/" % repo_name) in p

    return "external/" not in p

def _include_dir_impl(ctx):
    cc = ctx.attr.target[CcInfo].compilation_context

    # transitive headers
    all_hdrs = cc.headers
    hdrs = all_hdrs.to_list() if type(all_hdrs) == "depset" else all_hdrs

    repo_name = ctx.attr.target.label.repo_name

    # filter: same repo + paths that contain /include/
    hdrs = [
        f
        for f in hdrs
        if "/include/" in f.path and _same_repo(f, repo_name)
    ]

    manifest = ctx.actions.declare_file(ctx.label.name + ".headers.list")
    outdir = ctx.actions.declare_directory(ctx.label.name + "_includes")

    ctx.actions.write(manifest, "\n".join([f.path for f in hdrs]))
    ctx.actions.run_shell(
        inputs = hdrs + [manifest],
        outputs = [outdir],
        command = r"""
set -euo pipefail
OUT="$1"; MAN="$2"
python3 - <<'PY' "$OUT" "$MAN"
import os, shutil, sys
out, man = sys.argv[1], sys.argv[2]
for p in open(man):
    p = p.strip()
    if not p: continue
    # keep include/ prefix once
    rel = p.split("/include/", 1)[1]
    dst = os.path.join(out, "include", rel)
    os.makedirs(os.path.dirname(dst), exist_ok=True)
    shutil.copy2(p, dst)
PY
""",
        arguments = [outdir.path, manifest.path],
        progress_message = "Collecting repo-scoped headers into %s" % outdir.path,
        mnemonic = "CollectIncludes",
    )

    return [
        DefaultInfo(files = depset([outdir])),
        HeaderInfo(headers_dir = outdir, headers = depset(hdrs)),
    ]

include_dir = rule(
    implementation = _include_dir_impl,
    attrs = {
        "target": attr.label(mandatory = True),
    },
)
