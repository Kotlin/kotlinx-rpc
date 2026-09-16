"""Reproduce ratios from selected prompt CSV fields; no Apple execution required."""
import sys
sys.dont_write_bytecode = True

import csv
import hashlib
from pathlib import Path
from measurements import DATA, LATENCY

ROOT = Path(__file__).resolve().parent
pairs = ((0, 1, 'current/legacy'), (0, 2, 'current/swift'), (2, 1, 'swift/legacy'))
rows = [line.split() for line in DATA.splitlines()]
assert len(rows) == 52
assert len({tuple(row[:2]) for row in rows}) == 52
assert len({row[0] for row in rows}) == 10
with (ROOT / 'throughput-ratios.csv').open('w', newline='') as f:
    writer = csv.writer(f, lineterminator='\n')
    writer.writerow(['benchmark', 'case', 'current_units_s', 'legacy_units_s', 'swift_units_s'] + [p[2] for p in pairs])
    for family, case, *values in rows:
        rates = list(map(float, values))
        assert min(rates) > 0
        ratios = [rates[a] / rates[b] for a, b, _ in pairs]
        assert abs(ratios[0] - ratios[1] * ratios[2]) < 1e-12
        writer.writerow([family, case, *values, *[f'{v:.6f}' for v in ratios]])
with (ROOT / 'latency-ratios.csv').open('w', newline='') as f:
    writer = csv.writer(f, lineterminator='\n')
    writer.writerow(['benchmark', 'case', 'metric', 'current_us', 'legacy_us', 'swift_us'] + [p[2] for p in pairs])
    for line in LATENCY.splitlines():
        family, case, *values = line.split()
        assert (family, case) in {tuple(row[:2]) for row in rows}
        metrics = [list(map(float, value.split(','))) for value in values]
        assert all(len(m) == 5 for m in metrics)
        assert all(m[1] <= m[2] <= m[3] <= m[4] for m in metrics)
        for i, name in enumerate(['mean', 'p50', 'p99', 'p999', 'max']):
            us = [m[i] for m in metrics]
            writer.writerow([family, case, name, *us, *[f'{us[a]/us[b]:.6f}' for a,b,_ in pairs]])
with (ROOT / 'tables.md').open('w') as f:
    f.write('# Calculated tables\n\nThroughput ratios: higher favors the numerator. Units are RPCs/s for unary, measured workload units/s for streaming. Same-case message/s and nonzero application-byte/s ratios agree up to rounding.\n\n')
    for family in dict.fromkeys(row[0] for row in rows):
        f.write(f'## {family}\n\n| Case | Current units/s | Legacy units/s | Swift units/s | C/L | C/S | S/L |\n|---|---:|---:|---:|---:|---:|---:|\n')
        for fam, case, *values in rows:
            if fam != family: continue
            r = list(map(float, values))
            f.write('| '+case+' | '+' | '.join(f'{v:,.1f}' for v in r)+' | '+' | '.join(f'{r[a]/r[b]:.3f}' for a,b,_ in pairs)+' |\n')
        f.write('\n')
    f.write('## Selected latency distributions\n\nAll values are microseconds; ratios above 1 favor the denominator.\n\n| Benchmark / case | Metric | Current | Legacy | Swift | C/L | C/S | S/L |\n|---|---|---:|---:|---:|---:|---:|---:|\n')
    for row in csv.DictReader((ROOT/'latency-ratios.csv').open()):
        f.write('| '+row['benchmark']+' / '+row['case']+' | '+row['metric']+' | '+' | '.join(f'{float(row[k]):.3f}' for k in ['current_us','legacy_us','swift_us','current/legacy','current/swift','swift/legacy'])+' |\n')
    f.write('\nSource: selected fields transcribed from the user-supplied CSV for this automation run. This is not a replacement for the original full CSV.\n')
print('Validated 52 paired cases, 10 families, 105 selected latency comparisons.')
print('Selected-input SHA256:', hashlib.sha256((ROOT/'measurements.py').read_bytes()).hexdigest())
