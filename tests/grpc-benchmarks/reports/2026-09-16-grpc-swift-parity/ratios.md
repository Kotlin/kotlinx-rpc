# Ratio tables (single run; ratios are point estimates)

## unary-latency

Throughput metric: `calls_per_second`; latency metric: `latency_p50_us` / `latency_mean_us`.

| case | cur tp | leg tp | swift tp | leg/cur tp | swift/cur tp | leg/swift tp | cur p50 | leg p50 | swift p50 | cur/leg p50 | cur/swift p50 | swift/leg p50 | cur/leg mean | cur/swift mean | swift/leg mean |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| default | 1662 | 6459 | 3436 | 3.89 | 2.07 | 1.88 | 601 | 143 | 290 | 4.19 | 2.07 | 2.02 | 3.89 | 2.07 | 1.88 |

## unary-throughput

Throughput metric: `calls_per_second`; latency metric: `latency_p50_us` / `latency_mean_us`.

| case | cur tp | leg tp | swift tp | leg/cur tp | swift/cur tp | leg/swift tp | cur p50 | leg p50 | swift p50 | cur/leg p50 | cur/swift p50 | swift/leg p50 | cur/leg mean | cur/swift mean | swift/leg mean |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| default | 3645 | 12593 | 19519 | 3.45 | 5.35 | 0.65 | 4175 | 1118 | 828 | 3.73 | 5.04 | 0.74 | 3.46 | 5.37 | 0.64 |

## unary-payload-sweep

Throughput metric: `calls_per_second`; latency metric: `latency_p50_us` / `latency_mean_us`.

| case | cur tp | leg tp | swift tp | leg/cur tp | swift/cur tp | leg/swift tp | cur p50 | leg p50 | swift p50 | cur/leg p50 | cur/swift p50 | swift/leg p50 | cur/leg mean | cur/swift mean | swift/leg mean |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| symmetric-64b | 1629 | 5943 | 2834 | 3.65 | 1.74 | 2.10 | 605 | 151 | 370 | 4.02 | 1.64 | 2.46 | 3.65 | 1.74 | 2.10 |
| symmetric-1k | 1611 | 5420 | 2565 | 3.36 | 1.59 | 2.11 | 608 | 158 | 381 | 3.86 | 1.60 | 2.42 | 3.37 | 1.59 | 2.11 |
| symmetric-64k | 943 | 1842 | 2228 | 1.95 | 2.36 | 0.83 | 973 | 393 | 454 | 2.48 | 2.15 | 1.15 | 1.95 | 2.36 | 0.83 |
| symmetric-1m | 93 | 171 | 327 | 1.85 | 3.53 | 0.52 | 7408 | 4560 | 3005 | 1.62 | 2.46 | 0.66 | 1.85 | 3.53 | 0.52 |
| symmetric-near-4m | 27 | 45 | 75 | 1.68 | 2.82 | 0.59 | 27823 | 17548 | 13397 | 1.59 | 2.08 | 0.76 | 1.68 | 2.82 | 0.59 |
| upload-1k | 1622 | 5265 | 2574 | 3.25 | 1.59 | 2.05 | 607 | 162 | 388 | 3.75 | 1.56 | 2.40 | 3.25 | 1.59 | 2.05 |
| upload-64k | 1541 | 3119 | 4596 | 2.02 | 2.98 | 0.68 | 588 | 244 | 214 | 2.41 | 2.75 | 0.87 | 2.03 | 2.98 | 0.68 |
| upload-1m | 262 | 449 | 1518 | 1.71 | 5.79 | 0.30 | 2612 | 1516 | 652 | 1.72 | 4.00 | 0.43 | 1.71 | 5.79 | 0.30 |
| upload-near-4m | 122 | 150 | 426 | 1.23 | 3.50 | 0.35 | 7890 | 5616 | 2343 | 1.40 | 3.37 | 0.42 | 1.23 | 3.50 | 0.35 |
| download-1k | 1598 | 5388 | 2798 | 3.37 | 1.75 | 1.93 | 608 | 151 | 366 | 4.03 | 1.66 | 2.43 | 3.37 | 1.75 | 1.93 |
| download-64k | 958 | 2476 | 1748 | 2.59 | 1.83 | 1.42 | 1006 | 317 | 574 | 3.17 | 1.75 | 1.81 | 2.59 | 1.83 | 1.42 |
| download-1m | 144 | 241 | 370 | 1.68 | 2.57 | 0.65 | 5843 | 2600 | 2720 | 2.25 | 2.15 | 1.05 | 1.68 | 2.57 | 0.65 |
| download-near-4m | 32 | 75 | 103 | 2.33 | 3.19 | 0.73 | 21088 | 9531 | 9755 | 2.21 | 2.16 | 1.02 | 2.33 | 3.19 | 0.73 |

## unary-concurrency-sweep

Throughput metric: `calls_per_second`; latency metric: `latency_p50_us` / `latency_mean_us`.

| case | cur tp | leg tp | swift tp | leg/cur tp | swift/cur tp | leg/swift tp | cur p50 | leg p50 | swift p50 | cur/leg p50 | cur/swift p50 | swift/leg p50 | cur/leg mean | cur/swift mean | swift/leg mean |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| empty-c1 | 1668 | 6534 | 2850 | 3.92 | 1.71 | 2.29 | 597 | 142 | 361 | 4.20 | 1.65 | 2.54 | 3.92 | 1.71 | 2.29 |
| empty-c2 | 2797 | 10513 | 5371 | 3.76 | 1.92 | 1.96 | 714 | 175 | 373 | 4.09 | 1.92 | 2.13 | 3.76 | 1.92 | 1.96 |
| empty-c4 | 4164 | 12932 | 9705 | 3.11 | 2.33 | 1.33 | 964 | 271 | 401 | 3.55 | 2.41 | 1.48 | 3.11 | 2.33 | 1.33 |
| empty-c8 | 4346 | 13981 | 13182 | 3.22 | 3.03 | 1.06 | 1831 | 522 | 594 | 3.51 | 3.08 | 1.14 | 3.22 | 3.03 | 1.06 |
| empty-c16 | 3949 | 14380 | 15603 | 3.64 | 3.95 | 0.92 | 4007 | 1045 | 1028 | 3.83 | 3.90 | 0.98 | 3.65 | 3.95 | 0.92 |
| empty-c32 | 3204 | 13165 | 20108 | 4.11 | 6.28 | 0.65 | 9802 | 2114 | 1644 | 4.64 | 5.96 | 0.78 | 4.11 | 6.29 | 0.65 |
| empty-c64 | 2433 | 13284 | 18316 | 5.46 | 7.53 | 0.73 | 26647 | 4352 | 3514 | 6.12 | 7.58 | 0.81 | 5.46 | 7.54 | 0.72 |
| empty-c128 | 1849 | 20802 | 22360 | 11.25 | 12.09 | 0.93 | 62426 | 5170 | 5329 | 12.08 | 11.71 | 1.03 | 11.27 | 12.10 | 0.93 |
| 1k-c1 | 1616 | 5790 | 2857 | 3.58 | 1.77 | 2.03 | 610 | 156 | 364 | 3.90 | 1.68 | 2.32 | 3.58 | 1.77 | 2.03 |
| 1k-c2 | 2749 | 8536 | 5274 | 3.10 | 1.92 | 1.62 | 729 | 191 | 377 | 3.82 | 1.93 | 1.98 | 3.11 | 1.92 | 1.62 |
| 1k-c4 | 3740 | 11786 | 9808 | 3.15 | 2.62 | 1.20 | 969 | 288 | 396 | 3.36 | 2.45 | 1.37 | 2.99 | 2.49 | 1.20 |
| 1k-c8 | 4002 | 12811 | 13305 | 3.20 | 3.32 | 0.96 | 1867 | 532 | 588 | 3.51 | 3.18 | 1.10 | 3.21 | 3.33 | 0.96 |
| 1k-c16 | 3660 | 12699 | 20021 | 3.47 | 5.47 | 0.63 | 4182 | 1093 | 797 | 3.83 | 5.25 | 0.73 | 3.47 | 5.48 | 0.63 |
| 1k-c32 | 2995 | 12335 | 24408 | 4.12 | 8.15 | 0.51 | 10467 | 2170 | 1246 | 4.82 | 8.40 | 0.57 | 4.12 | 8.16 | 0.51 |
| 1k-c64 | 2035 | 13345 | 24385 | 6.56 | 11.99 | 0.55 | 30856 | 4126 | 2584 | 7.48 | 11.94 | 0.63 | 6.56 | 11.99 | 0.55 |
| 1k-c128 | 1346 | 15737 | 24675 | 11.69 | 18.34 | 0.64 | 95844 | 6312 | 5170 | 15.19 | 18.54 | 0.82 | 11.71 | 18.34 | 0.64 |

## bidi-ping-pong

Throughput metric: `messages_per_second`; latency metric: `latency_p50_us` / `latency_mean_us`.

| case | cur tp | leg tp | swift tp | leg/cur tp | swift/cur tp | leg/swift tp | cur p50 | leg p50 | swift p50 | cur/leg p50 | cur/swift p50 | swift/leg p50 | cur/leg mean | cur/swift mean | swift/leg mean |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| empty | 4913 | 15344 | 7488 | 3.12 | 1.52 | 2.05 | 403 | 126 | 262 | 3.21 | 1.54 | 2.08 | 3.13 | 1.52 | 2.05 |
| 1k | 4897 | 14786 | 6493 | 3.02 | 1.33 | 2.28 | 406 | 130 | 319 | 3.13 | 1.27 | 2.47 | 3.02 | 1.33 | 2.28 |

## bidi-full-duplex

Throughput metric: `messages_per_second`; latency metric: `latency_p50_us` / `latency_mean_us`.

| case | cur tp | leg tp | swift tp | leg/cur tp | swift/cur tp | leg/swift tp | cur p50 | leg p50 | swift p50 | cur/leg p50 | cur/swift p50 | swift/leg p50 | cur/leg mean | cur/swift mean | swift/leg mean |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| balanced-1k | 40823 | 32167 | 67927 | 0.79 | 1.66 | 0.47 | 12 | 51 | 1 | 0.24 | 8.79 | 0.03 | 0.79 | 1.66 | 0.47 |
| balanced-64k | 4381 | 7982 | 8518 | 1.82 | 1.94 | 0.94 | 451 | 256 | 227 | 1.76 | 1.99 | 0.88 | 1.82 | 1.94 | 0.94 |
| upload-heavy | 14732 | 10488 | 24660 | 0.71 | 1.67 | 0.43 | 128 | 168 | 72 | 0.76 | 1.77 | 0.43 | 0.71 | 1.67 | 0.42 |
| download-heavy | 5961 | 10996 | 11969 | 1.84 | 2.01 | 0.92 | 300 | 115 | 138 | 2.61 | 2.17 | 1.20 | 1.85 | 2.01 | 0.92 |

## client-streaming-throughput

Throughput metric: `messages_per_second`; latency metric: `latency_p50_us` / `latency_mean_us`.

| case | cur tp | leg tp | swift tp | leg/cur tp | swift/cur tp | leg/swift tp | cur p50 | leg p50 | swift p50 | cur/leg p50 | cur/swift p50 | swift/leg p50 | cur/leg mean | cur/swift mean | swift/leg mean |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 1k | 31968 | 26065 | 43666 | 0.82 | 1.37 | 0.60 | 9 | 34 | 1 | 0.28 | 10.67 | 0.03 | 0.81 | 1.37 | 0.59 |
| 64k | 7576 | 6802 | 13461 | 0.90 | 1.78 | 0.51 | 121 | 122 | 6 | 0.99 | 19.96 | 0.05 | 0.89 | 1.78 | 0.50 |
| 1m | 905 | 573 | 2293 | 0.63 | 2.53 | 0.25 | 685 | 1437 | 428 | 0.48 | 1.60 | 0.30 | 0.59 | 2.47 | 0.24 |

## server-streaming-throughput

Throughput metric: `messages_per_second`; latency metric: `latency_p50_us` / `latency_mean_us`.

| case | cur tp | leg tp | swift tp | leg/cur tp | swift/cur tp | leg/swift tp | cur p50 | leg p50 | swift p50 | cur/leg p50 | cur/swift p50 | swift/leg p50 | cur/leg mean | cur/swift mean | swift/leg mean |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 1k | 56927 | 62070 | 107361 | 1.09 | 1.89 | 0.58 | 12 | 13 | 2 | 0.89 | 6.42 | 0.14 | 1.09 | 1.87 | 0.58 |
| 64k | 3005 | 8986 | 7486 | 2.99 | 2.49 | 1.20 | 319 | 94 | 123 | 3.40 | 2.58 | 1.31 | 2.99 | 2.49 | 1.20 |
| 1m | 206 | 565 | 522 | 2.74 | 2.53 | 1.08 | 4919 | 1673 | 1867 | 2.94 | 2.63 | 1.12 | 2.74 | 2.53 | 1.08 |

## stream-message-overhead

Throughput metric: `messages_per_second`; latency metric: `latency_p50_us` / `latency_mean_us`.

| case | cur tp | leg tp | swift tp | leg/cur tp | swift/cur tp | leg/swift tp | cur p50 | leg p50 | swift p50 | cur/leg p50 | cur/swift p50 | swift/leg p50 | cur/leg mean | cur/swift mean | swift/leg mean |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 1k | 33549 | 26315 | 40309 | 0.78 | 1.20 | 0.65 | 9 | 33 | 1 | 0.26 | 9.45 | 0.03 | 0.79 | 1.20 | 0.65 |
| 64k | 7420 | 6186 | 13930 | 0.83 | 1.88 | 0.44 | 122 | 122 | 6 | 0.99 | 20.70 | 0.05 | 0.83 | 1.88 | 0.44 |
| 1m | 914 | 540 | 2242 | 0.59 | 2.45 | 0.24 | 866 | 1454 | 413 | 0.60 | 2.10 | 0.28 | 0.59 | 2.65 | 0.22 |
| near-4m | 218 | 152 | 521 | 0.70 | 2.39 | 0.29 | 2564 | 5678 | 1855 | 0.45 | 1.38 | 0.33 | 0.57 | 2.35 | 0.24 |

## stream-concurrency-sweep

Throughput metric: `messages_per_second`; latency metric: `latency_p50_us` / `latency_mean_us`.

| case | cur tp | leg tp | swift tp | leg/cur tp | swift/cur tp | leg/swift tp | cur p50 | leg p50 | swift p50 | cur/leg p50 | cur/swift p50 | swift/leg p50 | cur/leg mean | cur/swift mean | swift/leg mean |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| c1 | 52596 | 64091 | 107287 | 1.22 | 2.04 | 0.60 | 12 | 13 | 2 | 0.92 | 6.58 | 0.14 | 1.20 | 2.00 | 0.60 |
| c2 | 50813 | 74658 | 107620 | 1.47 | 2.12 | 0.69 | 12 | 16 | 2 | 0.72 | 7.07 | 0.10 | 1.31 | 1.87 | 0.70 |
| c4 | 41518 | 71886 | 104994 | 1.73 | 2.53 | 0.68 | 12 | 16 | 2 | 0.73 | 6.48 | 0.11 | 1.51 | 2.74 | 0.55 |
| c8 | 34207 | 59586 | 103551 | 1.74 | 3.03 | 0.58 | 12 | 14 | 2 | 0.84 | 6.65 | 0.13 | 2.06 | 3.52 | 0.59 |
| c16 | 28136 | 45460 | 110112 | 1.62 | 3.91 | 0.41 | 483 | 15 | 1 | 31.74 | 330.82 | 0.10 | 2.89 | 5.79 | 0.50 |

## Derived: unary c1 mean latency deltas (us)

| case | cur mean | leg mean | swift mean | cur-leg | cur-swift | swift-leg |
|---|---|---|---|---|---|---|
| unary-concurrency-sweep/empty-c1 | 600 | 153 | 351 | 447 | 249 | 198 |
| unary-concurrency-sweep/1k-c1 | 619 | 173 | 350 | 446 | 269 | 177 |
| unary-latency/default | 602 | 155 | 291 | 447 | 311 | 136 |
| unary-payload-sweep/symmetric-64b | 614 | 168 | 353 | 446 | 261 | 185 |
| unary-payload-sweep/symmetric-1k | 621 | 184 | 390 | 436 | 231 | 205 |
| unary-payload-sweep/symmetric-64k | 1060 | 543 | 449 | 517 | 611 | -94 |
| unary-payload-sweep/symmetric-1m | 10778 | 5833 | 3055 | 4945 | 7723 | -2778 |
| unary-payload-sweep/symmetric-near-4m | 37656 | 22458 | 13362 | 15198 | 24295 | -9096 |
| unary-payload-sweep/upload-1k | 616 | 190 | 388 | 426 | 228 | 199 |
| unary-payload-sweep/upload-64k | 649 | 320 | 218 | 329 | 432 | -103 |
| unary-payload-sweep/upload-1m | 3815 | 2225 | 659 | 1590 | 3156 | -1566 |
| unary-payload-sweep/upload-near-4m | 8220 | 6681 | 2347 | 1539 | 5873 | -4334 |
| unary-payload-sweep/download-1k | 626 | 186 | 357 | 440 | 268 | 172 |
| unary-payload-sweep/download-64k | 1044 | 404 | 572 | 640 | 472 | 168 |
| unary-payload-sweep/download-1m | 6951 | 4144 | 2702 | 2807 | 4249 | -1441 |
| unary-payload-sweep/download-near-4m | 30929 | 13249 | 9705 | 17681 | 21224 | -3544 |

## Derived: bytes/s (MiB/s) for payload-heavy cases

| case | cur MiB/s | leg MiB/s | swift MiB/s | leg/cur | swift/cur | leg/swift |
|---|---|---|---|---|---|---|
| bidi-full-duplex/balanced-1k | 39.9 | 31.4 | 66.3 | 0.79 | 1.66 | 0.47 |
| bidi-full-duplex/balanced-64k | 273.8 | 498.9 | 532.3 | 1.82 | 1.94 | 0.94 |
| bidi-full-duplex/upload-heavy | 467.6 | 332.9 | 782.7 | 0.71 | 1.67 | 0.43 |
| bidi-full-duplex/download-heavy | 189.2 | 349.0 | 379.9 | 1.84 | 2.01 | 0.92 |
| client-streaming-throughput/1k | 31.2 | 25.5 | 42.6 | 0.82 | 1.37 | 0.60 |
| client-streaming-throughput/64k | 473.0 | 424.7 | 840.4 | 0.90 | 1.78 | 0.51 |
| client-streaming-throughput/1m | 891.1 | 564.0 | 2257.7 | 0.63 | 2.53 | 0.25 |
| server-streaming-throughput/1k | 55.6 | 60.6 | 104.8 | 1.09 | 1.89 | 0.58 |
| server-streaming-throughput/64k | 187.6 | 561.1 | 467.4 | 2.99 | 2.49 | 1.20 |
| server-streaming-throughput/1m | 203.1 | 555.9 | 513.9 | 2.74 | 2.53 | 1.08 |
| stream-message-overhead/1k | 32.8 | 25.7 | 39.4 | 0.78 | 1.20 | 0.65 |
| stream-message-overhead/64k | 463.3 | 386.2 | 869.8 | 0.83 | 1.88 | 0.44 |
| stream-message-overhead/1m | 899.5 | 532.1 | 2207.4 | 0.59 | 2.45 | 0.24 |
| stream-message-overhead/near-4m | 775.2 | 539.8 | 1851.5 | 0.70 | 2.39 | 0.29 |
| unary-payload-sweep/symmetric-64b | 0.2 | 0.7 | 0.3 | 3.65 | 1.74 | 2.10 |
| unary-payload-sweep/symmetric-1k | 3.1 | 10.6 | 5.0 | 3.36 | 1.59 | 2.11 |
| unary-payload-sweep/symmetric-64k | 117.9 | 230.2 | 278.4 | 1.95 | 2.36 | 0.83 |
| unary-payload-sweep/symmetric-1m | 185.6 | 342.9 | 654.6 | 1.85 | 3.53 | 0.52 |
| unary-payload-sweep/symmetric-near-4m | 212.4 | 356.1 | 598.6 | 1.68 | 2.82 | 0.59 |
| unary-payload-sweep/upload-1k | 1.7 | 5.5 | 2.7 | 3.25 | 1.59 | 2.05 |
| unary-payload-sweep/upload-64k | 96.4 | 195.1 | 287.5 | 2.02 | 2.98 | 0.68 |
| unary-payload-sweep/upload-1m | 262.1 | 449.4 | 1517.7 | 1.71 | 5.79 | 0.30 |
| unary-payload-sweep/upload-near-4m | 486.5 | 598.5 | 1703.9 | 1.23 | 3.50 | 0.35 |
| unary-payload-sweep/download-1k | 1.7 | 5.6 | 2.9 | 3.37 | 1.75 | 1.93 |
| unary-payload-sweep/download-64k | 59.9 | 154.9 | 109.3 | 2.59 | 1.83 | 1.42 |
| unary-payload-sweep/download-1m | 143.9 | 241.3 | 370.1 | 1.68 | 2.57 | 0.65 |
| unary-payload-sweep/download-near-4m | 129.3 | 301.8 | 412.0 | 2.33 | 3.19 | 0.73 |

## Derived: per-message inter-arrival / send suspension (mean us) for streaming

| case | cur mean | leg mean | swift mean | cur-leg | cur-swift | swift-leg | cur p99 | leg p99 | swift p99 |
|---|---|---|---|---|---|---|---|---|---|
| bidi-full-duplex/balanced-1k | 49.0 | 62.1 | 29.4 | -13.1 | 19.5 | -32.6 | 733 | 181 | 362 |
| bidi-full-duplex/balanced-64k | 456.4 | 250.3 | 234.7 | 206.0 | 221.7 | -15.6 | 757 | 965 | 521 |
| bidi-full-duplex/upload-heavy | 135.4 | 190.4 | 80.9 | -55.0 | 54.5 | -109.5 | 403 | 955 | 193 |
| bidi-full-duplex/download-heavy | 335.4 | 181.7 | 167.1 | 153.7 | 168.3 | -14.6 | 623 | 1296 | 245 |
| bidi-ping-pong/empty | 407.0 | 130.2 | 267.1 | 276.8 | 140.0 | 136.8 | 491 | 181 | 481 |
| bidi-ping-pong/1k | 408.3 | 135.2 | 308.0 | 273.1 | 100.3 | 172.8 | 495 | 180 | 416 |
| client-streaming-throughput/1k | 31.0 | 38.3 | 22.6 | -7.3 | 8.5 | -15.7 | 1189 | 67 | 33 |
| client-streaming-throughput/64k | 130.9 | 146.6 | 73.7 | -15.7 | 57.2 | -72.9 | 397 | 201 | 580 |
| client-streaming-throughput/1m | 1007.6 | 1719.5 | 408.0 | -711.9 | 599.6 | -1311.5 | 6792 | 4950 | 883 |
| server-streaming-throughput/1k | 17.4 | 16.0 | 9.3 | 1.5 | 8.1 | -6.7 | 53 | 41 | 31 |
| server-streaming-throughput/64k | 332.7 | 111.1 | 133.7 | 221.6 | 199.0 | 22.5 | 561 | 2111 | 211 |
| server-streaming-throughput/1m | 4917.6 | 1795.6 | 1944.5 | 3122.0 | 2973.0 | 149.0 | 5424 | 6106 | 2415 |
| stream-concurrency-sweep/c1 | 18.6 | 15.5 | 9.3 | 3.2 | 9.3 | -6.2 | 59 | 41 | 30 |
| stream-concurrency-sweep/c2 | 34.6 | 26.4 | 18.5 | 8.2 | 16.1 | -7.9 | 87 | 55 | 30 |
| stream-concurrency-sweep/c4 | 65.5 | 43.3 | 23.9 | 22.2 | 41.6 | -19.4 | 148 | 59 | 30 |
| stream-concurrency-sweep/c8 | 164.9 | 80.0 | 46.8 | 85.0 | 118.1 | -33.2 | 5540 | 89 | 41 |
| stream-concurrency-sweep/c16 | 469.9 | 162.5 | 81.2 | 307.4 | 388.8 | -81.3 | 5408 | 7277 | 4365 |
| stream-message-overhead/1k | 29.7 | 37.8 | 24.7 | -8.1 | 4.9 | -13.0 | 1130 | 70 | 23 |
| stream-message-overhead/64k | 134.0 | 161.2 | 71.1 | -27.2 | 62.9 | -90.1 | 381 | 269 | 565 |
| stream-message-overhead/1m | 1071.7 | 1817.8 | 404.9 | -746.0 | 666.8 | -1412.9 | 1259 | 12054 | 832 |
| stream-message-overhead/near-4m | 3736.9 | 6545.1 | 1588.2 | -2808.2 | 2148.7 | -4956.9 | 21457 | 17510 | 3491 |

## Derived: concurrency scaling (throughput relative to c1 of same implementation)

### unary-concurrency-sweep empty-

| case | cur tp | cur/c1 | leg tp | leg/c1 | swift tp | swift/c1 | cur mean us | leg mean us | swift mean us |
|---|---|---|---|---|---|---|---|---|---|
| empty-c1 | 1668 | 1.00 | 6534 | 1.00 | 2850 | 1.00 | 600 | 153 | 351 |
| empty-c2 | 2797 | 1.68 | 10513 | 1.61 | 5371 | 1.88 | 715 | 190 | 372 |
| empty-c4 | 4164 | 2.50 | 12932 | 1.98 | 9705 | 3.41 | 960 | 309 | 412 |
| empty-c8 | 4346 | 2.61 | 13981 | 2.14 | 13182 | 4.63 | 1839 | 571 | 606 |
| empty-c16 | 3949 | 2.37 | 14380 | 2.20 | 15603 | 5.48 | 4050 | 1111 | 1024 |
| empty-c32 | 3204 | 1.92 | 13165 | 2.01 | 20108 | 7.06 | 9985 | 2428 | 1588 |
| empty-c64 | 2433 | 1.46 | 13284 | 2.03 | 18316 | 6.43 | 26296 | 4816 | 3489 |
| empty-c128 | 1849 | 1.11 | 20802 | 3.18 | 22360 | 7.85 | 69181 | 6140 | 5716 |

### unary-concurrency-sweep 1k-

| case | cur tp | cur/c1 | leg tp | leg/c1 | swift tp | swift/c1 | cur mean us | leg mean us | swift mean us |
|---|---|---|---|---|---|---|---|---|---|
| 1k-c1 | 1616 | 1.00 | 5790 | 1.00 | 2857 | 1.00 | 619 | 173 | 350 |
| 1k-c2 | 2749 | 1.70 | 8536 | 1.47 | 5274 | 1.85 | 727 | 234 | 379 |
| 1k-c4 | 3740 | 2.31 | 11786 | 2.04 | 9808 | 3.43 | 1013 | 339 | 408 |
| 1k-c8 | 4002 | 2.48 | 12811 | 2.21 | 13305 | 4.66 | 1998 | 623 | 600 |
| 1k-c16 | 3660 | 2.27 | 12699 | 2.19 | 20021 | 7.01 | 4369 | 1258 | 797 |
| 1k-c32 | 2995 | 1.85 | 12335 | 2.13 | 24408 | 8.54 | 10681 | 2592 | 1309 |
| 1k-c64 | 2035 | 1.26 | 13345 | 2.30 | 24385 | 8.54 | 31453 | 4792 | 2623 |
| 1k-c128 | 1346 | 0.83 | 15737 | 2.72 | 24675 | 8.64 | 95106 | 8120 | 5185 |

### stream-concurrency-sweep 

| case | cur tp | cur/c1 | leg tp | leg/c1 | swift tp | swift/c1 | cur mean us | leg mean us | swift mean us |
|---|---|---|---|---|---|---|---|---|---|
| c1 | 52596 | 1.00 | 64091 | 1.00 | 107287 | 1.00 | 19 | 15 | 9 |
| c2 | 50813 | 0.97 | 74658 | 1.16 | 107620 | 1.00 | 35 | 26 | 18 |
| c4 | 41518 | 0.79 | 71886 | 1.12 | 104994 | 0.98 | 66 | 43 | 24 |
| c8 | 34207 | 0.65 | 59586 | 0.93 | 103551 | 0.97 | 165 | 80 | 47 |
| c16 | 28136 | 0.53 | 45460 | 0.71 | 110112 | 1.03 | 470 | 163 | 81 |

