#!/usr/bin/env python3
# Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
"""Pair benchmark rows by (benchmark, case) and print ratio tables in Markdown."""
import csv, sys
from collections import defaultdict

path = sys.argv[1] if len(sys.argv) > 1 else __import__("os").path.join(__import__("os").path.dirname(__file__), "benchmark-data.csv")
rows = list(csv.DictReader(open(path)))
by = defaultdict(dict)
for r in rows:
    by[(r["benchmark"], r["case"])][r["implementation"]] = r

def f(r, k):
    v = r.get(k, "")
    return float(v) if v not in ("", None) else None

def ratio(a, b):
    return None if a is None or b is None or b == 0 else a / b

def fmt(x, nd=2):
    return "n/a" if x is None else f"{x:.{nd}f}"

order = ["unary-latency", "unary-throughput", "unary-payload-sweep", "unary-concurrency-sweep",
         "bidi-ping-pong", "bidi-full-duplex", "client-streaming-throughput",
         "server-streaming-throughput", "stream-message-overhead", "stream-concurrency-sweep"]
case_order = {}
for r in rows:
    case_order.setdefault((r["benchmark"], r["case"]), len(case_order))

print("# Ratio tables (single run; ratios are point estimates)\n")
for bench in order:
    keys = sorted([k for k in by if k[0] == bench], key=lambda k: case_order[k])
    if not keys:
        continue
    streaming = any(f(by[k].get("current", {}), "messages_per_second") for k in keys)
    tp_key = "messages_per_second" if streaming else "calls_per_second"
    print(f"## {bench}\n")
    print(f"Throughput metric: `{tp_key}`; latency metric: `latency_p50_us` / `latency_mean_us`.\n")
    print("| case | cur tp | leg tp | swift tp | leg/cur tp | swift/cur tp | leg/swift tp | cur p50 | leg p50 | swift p50 | cur/leg p50 | cur/swift p50 | swift/leg p50 | cur/leg mean | cur/swift mean | swift/leg mean |")
    print("|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|")
    for k in keys:
        c, l, s = by[k].get("current"), by[k].get("legacy"), by[k].get("swift")
        if not (c and l and s):
            print(f"| {k[1]} | missing rows: {sorted(by[k])} |")
            continue
        ct, lt, st = f(c, tp_key), f(l, tp_key), f(s, tp_key)
        cp, lp, sp = f(c, "latency_p50_us"), f(l, "latency_p50_us"), f(s, "latency_p50_us")
        cm, lm, sm = f(c, "latency_mean_us"), f(l, "latency_mean_us"), f(s, "latency_mean_us")
        print(f"| {k[1]} | {fmt(ct,0)} | {fmt(lt,0)} | {fmt(st,0)} | {fmt(ratio(lt,ct))} | {fmt(ratio(st,ct))} | {fmt(ratio(lt,st))} "
              f"| {fmt(cp,0)} | {fmt(lp,0)} | {fmt(sp,0)} | {fmt(ratio(cp,lp))} | {fmt(ratio(cp,sp))} | {fmt(ratio(sp,lp))} "
              f"| {fmt(ratio(cm,lm))} | {fmt(ratio(cm,sm))} | {fmt(ratio(sm,lm))} |")
    print()

# Extra: per-call implied costs for unary sweeps (mean latency at c1 = per-call service time incl. server)
print("## Derived: unary c1 mean latency deltas (us)\n")
print("| case | cur mean | leg mean | swift mean | cur-leg | cur-swift | swift-leg |")
print("|---|---|---|---|---|---|---|")
for k in sorted([k for k in by if k[0] in ("unary-latency", "unary-payload-sweep", "unary-concurrency-sweep") and by[k]["current"]["concurrency"] == "1"], key=lambda k: case_order[k]):
    c, l, s = by[k]["current"], by[k]["legacy"], by[k]["swift"]
    cm, lm, sm = f(c, "latency_mean_us"), f(l, "latency_mean_us"), f(s, "latency_mean_us")
    print(f"| {k[0]}/{k[1]} | {fmt(cm,0)} | {fmt(lm,0)} | {fmt(sm,0)} | {fmt(cm-lm,0)} | {fmt(cm-sm,0)} | {fmt(sm-lm,0)} |")
print()

print("## Derived: bytes/s (MiB/s) for payload-heavy cases\n")
print("| case | cur MiB/s | leg MiB/s | swift MiB/s | leg/cur | swift/cur | leg/swift |")
print("|---|---|---|---|---|---|---|")
for k in sorted([k for k in by if k[0] in ("unary-payload-sweep","stream-message-overhead","client-streaming-throughput","server-streaming-throughput","bidi-full-duplex")], key=lambda k: case_order[k]):
    c, l, s = by[k]["current"], by[k]["legacy"], by[k]["swift"]
    cb, lb, sb = [f(x, "application_bytes_per_second")/1048576 for x in (c,l,s)]
    print(f"| {k[0]}/{k[1]} | {fmt(cb,1)} | {fmt(lb,1)} | {fmt(sb,1)} | {fmt(ratio(lb,cb))} | {fmt(ratio(sb,cb))} | {fmt(ratio(lb,sb))} |")
print()

print("## Derived: per-message inter-arrival / send suspension (mean us) for streaming\n")
print("| case | cur mean | leg mean | swift mean | cur-leg | cur-swift | swift-leg | cur p99 | leg p99 | swift p99 |")
print("|---|---|---|---|---|---|---|---|---|---|")
for k in sorted([k for k in by if k[0] in ("stream-message-overhead","client-streaming-throughput","server-streaming-throughput","bidi-full-duplex","bidi-ping-pong","stream-concurrency-sweep")], key=lambda k: case_order[k]):
    c, l, s = by[k]["current"], by[k]["legacy"], by[k]["swift"]
    cm, lm, sm = f(c, "latency_mean_us"), f(l, "latency_mean_us"), f(s, "latency_mean_us")
    print(f"| {k[0]}/{k[1]} | {fmt(cm,1)} | {fmt(lm,1)} | {fmt(sm,1)} | {fmt(cm-lm,1)} | {fmt(cm-sm,1)} | {fmt(sm-lm,1)} | {fmt(f(c,'latency_p99_us'),0)} | {fmt(f(l,'latency_p99_us'),0)} | {fmt(f(s,'latency_p99_us'),0)} |")
print()

print("## Derived: concurrency scaling (throughput relative to c1 of same implementation)\n")
for bench, pref in (("unary-concurrency-sweep","empty-"),("unary-concurrency-sweep","1k-"),("stream-concurrency-sweep","")):
    keys = sorted([k for k in by if k[0]==bench and k[1].startswith(pref)], key=lambda k: case_order[k])
    tp_key = "messages_per_second" if bench.startswith("stream") else "calls_per_second"
    print(f"### {bench} {pref or ''}\n")
    print("| case | cur tp | cur/c1 | leg tp | leg/c1 | swift tp | swift/c1 | cur mean us | leg mean us | swift mean us |")
    print("|---|---|---|---|---|---|---|---|---|---|")
    base = {impl: f(by[keys[0]][impl], tp_key) for impl in ("current","legacy","swift")}
    for k in keys:
        c, l, s = by[k]["current"], by[k]["legacy"], by[k]["swift"]
        print(f"| {k[1]} | {fmt(f(c,tp_key),0)} | {fmt(f(c,tp_key)/base['current'])} | {fmt(f(l,tp_key),0)} | {fmt(f(l,tp_key)/base['legacy'])} | {fmt(f(s,tp_key),0)} | {fmt(f(s,tp_key)/base['swift'])} | {fmt(f(c,'latency_mean_us'),0)} | {fmt(f(l,'latency_mean_us'),0)} | {fmt(f(s,'latency_mean_us'),0)} |")
    print()
