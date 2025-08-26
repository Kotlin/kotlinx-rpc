HeaderInfo = provider(fields = ["headers_dir", "headers"])

def _include_dir_impl(ctx):
    cc = ctx.attr.target[CcInfo].compilation_context
    all_hdrs = cc.direct_headers
    hdrs = all_hdrs.to_list() if type(all_hdrs) == "depset" else all_hdrs

    manifest = ctx.actions.declare_file(ctx.label.name + ".headers.list")
    outdir = ctx.actions.declare_directory(ctx.label.name + "_includes")  # e.g. <target>_includes/

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
    if not p or "/include/" not in p:
        continue
    rel = p.split("/include/", 1)[1]
    dst = os.path.join(out, "include", rel)
    os.makedirs(os.path.dirname(dst), exist_ok=True)
    shutil.copy2(p, dst)
PY
""",
        arguments = [outdir.path, manifest.path],
        progress_message = "Collecting headers into %s" % outdir.path,
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
