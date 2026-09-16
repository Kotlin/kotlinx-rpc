# Calculated tables

Throughput ratios: higher favors the numerator. Units are RPCs/s for unary, measured workload units/s for streaming. Same-case message/s and nonzero application-byte/s ratios agree up to rounding.

## bidi-full-duplex

| Case | Current units/s | Legacy units/s | Swift units/s | C/L | C/S | S/L |
|---|---:|---:|---:|---:|---:|---:|
| balanced-1k | 32,016.2 | 10,306.3 | 34,773.7 | 3.106 | 0.921 | 3.374 |
| balanced-64k | 4,215.2 | 3,491.5 | 3,440.3 | 1.207 | 1.225 | 0.985 |
| upload-heavy | 9,996.8 | 5,364.0 | 8,634.9 | 1.864 | 1.158 | 1.610 |
| download-heavy | 6,104.8 | 5,483.9 | 5,548.9 | 1.113 | 1.100 | 1.012 |

## bidi-ping-pong

| Case | Current units/s | Legacy units/s | Swift units/s | C/L | C/S | S/L |
|---|---:|---:|---:|---:|---:|---:|
| empty | 3,038.5 | 7,080.4 | 3,330.7 | 0.429 | 0.912 | 0.470 |
| 1k | 2,836.0 | 6,372.8 | 3,220.7 | 0.445 | 0.881 | 0.505 |

## client-streaming-throughput

| Case | Current units/s | Legacy units/s | Swift units/s | C/L | C/S | S/L |
|---|---:|---:|---:|---:|---:|---:|
| 1k | 41,494.6 | 17,852.0 | 41,126.4 | 2.324 | 1.009 | 2.304 |
| 64k | 9,042.2 | 6,335.6 | 13,331.6 | 1.427 | 0.678 | 2.104 |
| 1m | 1,145.1 | 520.4 | 2,136.0 | 2.200 | 0.536 | 4.104 |

## server-streaming-throughput

| Case | Current units/s | Legacy units/s | Swift units/s | C/L | C/S | S/L |
|---|---:|---:|---:|---:|---:|---:|
| 1k | 64,502.2 | 52,519.6 | 99,609.0 | 1.228 | 0.648 | 1.897 |
| 64k | 6,171.7 | 8,235.8 | 6,715.2 | 0.749 | 0.919 | 0.815 |
| 1m | 403.7 | 514.5 | 431.7 | 0.785 | 0.935 | 0.839 |

## stream-concurrency-sweep

| Case | Current units/s | Legacy units/s | Swift units/s | C/L | C/S | S/L |
|---|---:|---:|---:|---:|---:|---:|
| c1 | 56,508.9 | 50,740.8 | 90,754.6 | 1.114 | 0.623 | 1.789 |
| c2 | 62,647.5 | 58,942.4 | 103,852.5 | 1.063 | 0.603 | 1.762 |
| c4 | 62,692.9 | 59,961.5 | 100,814.3 | 1.046 | 0.622 | 1.681 |
| c8 | 63,237.2 | 55,737.4 | 100,140.1 | 1.135 | 0.631 | 1.797 |
| c16 | 60,541.7 | 46,227.3 | 97,422.9 | 1.310 | 0.621 | 2.107 |

## stream-message-overhead

| Case | Current units/s | Legacy units/s | Swift units/s | C/L | C/S | S/L |
|---|---:|---:|---:|---:|---:|---:|
| 1k | 40,821.6 | 23,401.1 | 40,818.6 | 1.744 | 1.000 | 1.744 |
| 64k | 8,062.5 | 5,957.0 | 13,358.4 | 1.353 | 0.604 | 2.242 |
| 1m | 1,134.1 | 474.2 | 2,146.3 | 2.391 | 0.528 | 4.526 |
| near-4m | 202.6 | 129.1 | 491.7 | 1.570 | 0.412 | 3.809 |

## unary-concurrency-sweep

| Case | Current units/s | Legacy units/s | Swift units/s | C/L | C/S | S/L |
|---|---:|---:|---:|---:|---:|---:|
| empty-c1 | 2,472.0 | 6,092.9 | 2,752.5 | 0.406 | 0.898 | 0.452 |
| empty-c2 | 4,499.8 | 9,155.7 | 5,162.2 | 0.491 | 0.872 | 0.564 |
| empty-c4 | 7,065.2 | 12,753.8 | 8,638.1 | 0.554 | 0.818 | 0.677 |
| empty-c8 | 9,722.2 | 13,610.6 | 13,284.7 | 0.714 | 0.732 | 0.976 |
| empty-c16 | 10,731.2 | 13,571.1 | 15,407.8 | 0.791 | 0.696 | 1.135 |
| empty-c32 | 11,060.9 | 13,325.2 | 16,848.4 | 0.830 | 0.656 | 1.264 |
| empty-c64 | 10,675.8 | 13,243.5 | 18,664.9 | 0.806 | 0.572 | 1.409 |
| empty-c128 | 10,558.9 | 20,267.3 | 21,677.5 | 0.521 | 0.487 | 1.070 |
| 1k-c1 | 2,263.8 | 5,185.6 | 2,633.5 | 0.437 | 0.860 | 0.508 |
| 1k-c2 | 3,797.3 | 8,409.9 | 5,141.0 | 0.452 | 0.739 | 0.611 |
| 1k-c4 | 5,144.9 | 10,526.0 | 8,847.1 | 0.489 | 0.582 | 0.840 |
| 1k-c8 | 6,430.5 | 11,248.0 | 12,230.6 | 0.572 | 0.526 | 1.087 |
| 1k-c16 | 7,757.7 | 11,102.7 | 15,526.7 | 0.699 | 0.500 | 1.398 |
| 1k-c32 | 8,312.7 | 11,162.5 | 23,166.6 | 0.745 | 0.359 | 2.075 |
| 1k-c64 | 8,559.7 | 11,564.4 | 24,082.2 | 0.740 | 0.355 | 2.082 |
| 1k-c128 | 8,694.3 | 14,656.6 | 23,693.5 | 0.593 | 0.367 | 1.617 |

## unary-latency

| Case | Current units/s | Legacy units/s | Swift units/s | C/L | C/S | S/L |
|---|---:|---:|---:|---:|---:|---:|
| default | 2,314.4 | 6,050.7 | 3,454.3 | 0.383 | 0.670 | 0.571 |

## unary-payload-sweep

| Case | Current units/s | Legacy units/s | Swift units/s | C/L | C/S | S/L |
|---|---:|---:|---:|---:|---:|---:|
| symmetric-64b | 2,293.1 | 5,546.1 | 2,622.5 | 0.413 | 0.874 | 0.473 |
| symmetric-1k | 2,270.4 | 5,046.3 | 2,604.5 | 0.450 | 0.872 | 0.516 |
| symmetric-64k | 1,355.3 | 1,599.3 | 2,024.2 | 0.847 | 0.670 | 1.266 |
| symmetric-1m | 129.0 | 146.5 | 314.9 | 0.881 | 0.410 | 2.150 |
| symmetric-near-4m | 27.0 | 38.3 | 69.1 | 0.704 | 0.390 | 1.803 |
| upload-1k | 2,319.7 | 5,144.0 | 2,604.6 | 0.451 | 0.891 | 0.506 |
| upload-64k | 2,376.3 | 3,035.5 | 4,463.9 | 0.783 | 0.532 | 1.471 |
| upload-1m | 282.1 | 348.7 | 1,430.1 | 0.809 | 0.197 | 4.101 |
| upload-near-4m | 74.3 | 94.3 | 415.7 | 0.787 | 0.179 | 4.407 |
| download-1k | 2,286.7 | 5,554.1 | 2,631.6 | 0.412 | 0.869 | 0.474 |
| download-64k | 1,311.8 | 2,160.9 | 1,706.8 | 0.607 | 0.769 | 0.790 |
| download-1m | 203.4 | 201.1 | 358.9 | 1.011 | 0.567 | 1.784 |
| download-near-4m | 38.5 | 68.1 | 97.5 | 0.566 | 0.395 | 1.433 |

## unary-throughput

| Case | Current units/s | Legacy units/s | Swift units/s | C/L | C/S | S/L |
|---|---:|---:|---:|---:|---:|---:|
| default | 8,784.1 | 11,229.3 | 14,954.8 | 0.782 | 0.587 | 1.332 |

## Selected latency distributions

All values are microseconds; ratios above 1 favor the denominator.

| Benchmark / case | Metric | Current | Legacy | Swift | C/L | C/S | S/L |
|---|---|---:|---:|---:|---:|---:|---:|
| unary-latency / default | mean | 432.015 | 165.205 | 289.419 | 2.615 | 1.493 | 1.752 |
| unary-latency / default | p50 | 421.167 | 153.292 | 287.708 | 2.747 | 1.464 | 1.877 |
| unary-latency / default | p99 | 608.000 | 401.250 | 332.250 | 1.515 | 1.830 | 0.828 |
| unary-latency / default | p999 | 1227.792 | 589.125 | 577.916 | 2.084 | 2.125 | 0.981 |
| unary-latency / default | max | 1376.667 | 797.333 | 626.875 | 1.727 | 2.196 | 0.786 |
| unary-throughput / default | mean | 1816.661 | 1421.648 | 1067.725 | 1.278 | 1.701 | 0.751 |
| unary-throughput / default | p50 | 1160.291 | 1173.417 | 1072.792 | 0.989 | 1.082 | 0.914 |
| unary-throughput / default | p99 | 1978.250 | 1725.042 | 1623.583 | 1.147 | 1.218 | 0.941 |
| unary-throughput / default | p999 | 403373.750 | 117858.583 | 2023.167 | 3.423 | 199.377 | 0.017 |
| unary-throughput / default | max | 404098.916 | 118153.166 | 3113.167 | 3.420 | 129.803 | 0.026 |
| unary-concurrency-sweep / empty-c1 | mean | 404.479 | 164.086 | 363.237 | 2.465 | 1.114 | 2.214 |
| unary-concurrency-sweep / empty-c1 | p50 | 399.417 | 146.917 | 362.375 | 2.719 | 1.102 | 2.467 |
| unary-concurrency-sweep / empty-c1 | p99 | 585.917 | 425.917 | 652.541 | 1.376 | 0.898 | 1.532 |
| unary-concurrency-sweep / empty-c1 | p999 | 789.084 | 645.958 | 1514.208 | 1.222 | 0.521 | 2.344 |
| unary-concurrency-sweep / empty-c1 | max | 6687.459 | 15822.292 | 5227.209 | 0.423 | 1.279 | 0.330 |
| unary-concurrency-sweep / empty-c128 | mean | 12111.925 | 6296.493 | 5895.503 | 1.924 | 2.054 | 0.936 |
| unary-concurrency-sweep / empty-c128 | p50 | 6746.125 | 5192.084 | 5436.667 | 1.299 | 1.241 | 1.047 |
| unary-concurrency-sweep / empty-c128 | p99 | 167756.708 | 49215.125 | 10223.750 | 3.409 | 16.409 | 0.208 |
| unary-concurrency-sweep / empty-c128 | p999 | 197764.084 | 67536.041 | 11002.625 | 2.928 | 17.974 | 0.163 |
| unary-concurrency-sweep / empty-c128 | max | 216420.792 | 70111.875 | 12805.250 | 3.087 | 16.901 | 0.183 |
| unary-concurrency-sweep / 1k-c1 | mean | 441.689 | 192.793 | 379.666 | 2.291 | 1.163 | 1.969 |
| unary-concurrency-sweep / 1k-c1 | p50 | 426.375 | 164.750 | 370.875 | 2.588 | 1.150 | 2.251 |
| unary-concurrency-sweep / 1k-c1 | p99 | 711.083 | 406.917 | 670.958 | 1.747 | 1.060 | 1.649 |
| unary-concurrency-sweep / 1k-c1 | p999 | 1457.875 | 493.917 | 1330.083 | 2.952 | 1.096 | 2.693 |
| unary-concurrency-sweep / 1k-c1 | max | 6169.458 | 77824.750 | 4177.083 | 0.079 | 1.477 | 0.054 |
| unary-concurrency-sweep / 1k-c128 | mean | 14717.161 | 8718.388 | 5399.926 | 1.688 | 2.725 | 0.619 |
| unary-concurrency-sweep / 1k-c128 | p50 | 6208.958 | 6233.542 | 5273.584 | 0.996 | 1.177 | 0.846 |
| unary-concurrency-sweep / 1k-c128 | p99 | 359772.625 | 99240.291 | 7775.125 | 3.625 | 46.272 | 0.078 |
| unary-concurrency-sweep / 1k-c128 | p999 | 396858.542 | 116058.666 | 10059.709 | 3.419 | 39.450 | 0.087 |
| unary-concurrency-sweep / 1k-c128 | max | 402353.625 | 159603.541 | 12032.042 | 2.521 | 33.440 | 0.075 |
| unary-payload-sweep / symmetric-64b | mean | 436.056 | 180.265 | 381.269 | 2.419 | 1.144 | 2.115 |
| unary-payload-sweep / symmetric-64b | p50 | 420.958 | 161.000 | 375.542 | 2.615 | 1.121 | 2.333 |
| unary-payload-sweep / symmetric-64b | p99 | 695.959 | 422.625 | 689.875 | 1.647 | 1.009 | 1.632 |
| unary-payload-sweep / symmetric-64b | p999 | 1425.875 | 489.375 | 1577.208 | 2.914 | 0.904 | 3.223 |
| unary-payload-sweep / symmetric-64b | max | 4088.334 | 132211.750 | 5068.459 | 0.031 | 0.807 | 0.038 |
| unary-payload-sweep / symmetric-1k | mean | 440.404 | 198.120 | 383.905 | 2.223 | 1.147 | 1.938 |
| unary-payload-sweep / symmetric-1k | p50 | 427.333 | 163.000 | 372.500 | 2.622 | 1.147 | 2.285 |
| unary-payload-sweep / symmetric-1k | p99 | 674.125 | 433.916 | 634.000 | 1.554 | 1.063 | 1.461 |
| unary-payload-sweep / symmetric-1k | p999 | 1175.209 | 503.292 | 1614.959 | 2.335 | 0.728 | 3.209 |
| unary-payload-sweep / symmetric-1k | max | 3731.584 | 158769.708 | 6641.542 | 0.024 | 0.562 | 0.042 |
| unary-payload-sweep / symmetric-64k | mean | 737.792 | 625.201 | 493.956 | 1.180 | 1.494 | 0.790 |
| unary-payload-sweep / symmetric-64k | p50 | 522.666 | 447.875 | 469.542 | 1.167 | 1.113 | 1.048 |
| unary-payload-sweep / symmetric-64k | p99 | 1520.375 | 864.000 | 993.667 | 1.760 | 1.530 | 1.150 |
| unary-payload-sweep / symmetric-64k | p999 | 4366.209 | 29808.041 | 1379.209 | 0.146 | 3.166 | 0.046 |
| unary-payload-sweep / symmetric-64k | max | 274051.750 | 156054.542 | 6616.917 | 1.756 | 41.417 | 0.042 |
| unary-payload-sweep / symmetric-1m | mean | 7750.594 | 6826.202 | 3175.585 | 1.135 | 2.441 | 0.465 |
| unary-payload-sweep / symmetric-1m | p50 | 3954.375 | 4727.500 | 3124.583 | 0.836 | 1.266 | 0.661 |
| unary-payload-sweep / symmetric-1m | p99 | 5633.500 | 52134.333 | 4192.333 | 0.108 | 1.344 | 0.080 |
| unary-payload-sweep / symmetric-1m | p999 | 403018.792 | 161704.500 | 4716.166 | 2.492 | 85.455 | 0.029 |
| unary-payload-sweep / symmetric-1m | max | 403018.792 | 161704.500 | 4716.166 | 2.492 | 85.455 | 0.029 |
| unary-payload-sweep / symmetric-near-4m | mean | 37066.595 | 26096.560 | 14470.444 | 1.420 | 2.562 | 0.554 |
| unary-payload-sweep / symmetric-near-4m | p50 | 14743.750 | 18196.375 | 14383.250 | 0.810 | 1.025 | 0.790 |
| unary-payload-sweep / symmetric-near-4m | p99 | 394281.583 | 182100.750 | 16238.166 | 2.165 | 24.281 | 0.089 |
| unary-payload-sweep / symmetric-near-4m | p999 | 394281.583 | 182100.750 | 16238.166 | 2.165 | 24.281 | 0.089 |
| unary-payload-sweep / symmetric-near-4m | max | 394281.583 | 182100.750 | 16238.166 | 2.165 | 24.281 | 0.089 |
| unary-payload-sweep / upload-1k | mean | 431.035 | 194.357 | 383.881 | 2.218 | 1.123 | 1.975 |
| unary-payload-sweep / upload-1k | p50 | 420.167 | 169.625 | 370.625 | 2.477 | 1.134 | 2.185 |
| unary-payload-sweep / upload-1k | p99 | 644.500 | 434.458 | 699.875 | 1.483 | 0.921 | 1.611 |
| unary-payload-sweep / upload-1k | p999 | 1238.584 | 740.209 | 1567.250 | 1.673 | 0.790 | 2.117 |
| unary-payload-sweep / upload-1k | max | 8578.833 | 145808.375 | 3169.375 | 0.059 | 2.707 | 0.022 |
| unary-payload-sweep / upload-64k | mean | 420.777 | 329.385 | 223.971 | 1.277 | 1.879 | 0.680 |
| unary-payload-sweep / upload-64k | p50 | 330.458 | 256.666 | 216.834 | 1.288 | 1.524 | 0.845 |
| unary-payload-sweep / upload-64k | p99 | 567.458 | 529.334 | 320.000 | 1.072 | 1.773 | 0.605 |
| unary-payload-sweep / upload-64k | p999 | 1160.625 | 1678.458 | 536.208 | 0.691 | 2.165 | 0.319 |
| unary-payload-sweep / upload-64k | max | 157981.208 | 124226.667 | 538.167 | 1.272 | 293.554 | 0.004 |
| unary-payload-sweep / upload-1m | mean | 3544.473 | 2867.581 | 699.139 | 1.236 | 5.070 | 0.244 |
| unary-payload-sweep / upload-1m | p50 | 1661.000 | 1735.250 | 679.209 | 0.957 | 2.445 | 0.391 |
| unary-payload-sweep / upload-1m | p99 | 3036.583 | 3334.208 | 1120.292 | 0.911 | 2.711 | 0.336 |
| unary-payload-sweep / upload-1m | p999 | 393356.166 | 161251.917 | 1372.042 | 2.439 | 286.694 | 0.009 |
| unary-payload-sweep / upload-1m | max | 393356.166 | 161251.917 | 1372.042 | 2.439 | 286.694 | 0.009 |
| unary-payload-sweep / upload-near-4m | mean | 13461.214 | 10599.838 | 2405.303 | 1.270 | 5.596 | 0.227 |
| unary-payload-sweep / upload-near-4m | p50 | 5946.917 | 5950.625 | 2389.583 | 0.999 | 2.489 | 0.402 |
| unary-payload-sweep / upload-near-4m | p99 | 335517.667 | 165143.500 | 2640.792 | 2.032 | 127.052 | 0.016 |
| unary-payload-sweep / upload-near-4m | p999 | 335517.667 | 165143.500 | 2640.792 | 2.032 | 127.052 | 0.016 |
| unary-payload-sweep / upload-near-4m | max | 335517.667 | 165143.500 | 2640.792 | 2.032 | 127.052 | 0.016 |
| unary-payload-sweep / download-1k | mean | 437.274 | 180.007 | 379.947 | 2.429 | 1.151 | 2.111 |
| unary-payload-sweep / download-1k | p50 | 423.167 | 156.750 | 374.000 | 2.700 | 1.131 | 2.386 |
| unary-payload-sweep / download-1k | p99 | 736.000 | 431.333 | 715.166 | 1.706 | 1.029 | 1.658 |
| unary-payload-sweep / download-1k | p999 | 1438.916 | 501.458 | 1606.083 | 2.869 | 0.896 | 3.203 |
| unary-payload-sweep / download-1k | max | 3986.667 | 149338.250 | 4676.000 | 0.027 | 0.853 | 0.031 |
| unary-payload-sweep / download-64k | mean | 762.240 | 462.705 | 585.814 | 1.647 | 1.301 | 1.266 |
| unary-payload-sweep / download-64k | p50 | 624.834 | 382.125 | 563.541 | 1.635 | 1.109 | 1.475 |
| unary-payload-sweep / download-64k | p99 | 915.875 | 679.333 | 940.417 | 1.348 | 0.974 | 1.384 |
| unary-payload-sweep / download-64k | p999 | 1412.333 | 1719.042 | 2335.958 | 0.822 | 0.605 | 1.359 |
| unary-payload-sweep / download-64k | max | 254704.833 | 163188.291 | 2482.334 | 1.561 | 102.607 | 0.015 |
| unary-payload-sweep / download-1m | mean | 4916.460 | 4972.232 | 2786.392 | 0.989 | 1.764 | 0.560 |
| unary-payload-sweep / download-1m | p50 | 3057.167 | 2704.083 | 2796.208 | 1.131 | 1.093 | 1.034 |
| unary-payload-sweep / download-1m | p99 | 3703.292 | 73810.167 | 3270.625 | 0.050 | 1.132 | 0.044 |
| unary-payload-sweep / download-1m | p999 | 371839.291 | 168230.833 | 3791.084 | 2.210 | 98.083 | 0.023 |
| unary-payload-sweep / download-1m | max | 371839.291 | 168230.833 | 3791.084 | 2.210 | 98.083 | 0.023 |
| unary-payload-sweep / download-near-4m | mean | 25972.938 | 14690.705 | 10251.512 | 1.768 | 2.534 | 0.698 |
| unary-payload-sweep / download-near-4m | p50 | 10420.500 | 10049.750 | 10100.833 | 1.037 | 1.032 | 1.005 |
| unary-payload-sweep / download-near-4m | p99 | 392019.250 | 173438.125 | 14552.167 | 2.260 | 26.939 | 0.084 |
| unary-payload-sweep / download-near-4m | p999 | 392019.250 | 173438.125 | 14552.167 | 2.260 | 26.939 | 0.084 |
| unary-payload-sweep / download-near-4m | max | 392019.250 | 173438.125 | 14552.167 | 2.260 | 26.939 | 0.084 |
| bidi-ping-pong / empty | mean | 329.029 | 141.111 | 300.195 | 2.332 | 1.096 | 2.127 |
| bidi-ping-pong / empty | p50 | 322.083 | 127.750 | 293.542 | 2.521 | 1.097 | 2.298 |
| bidi-ping-pong / empty | p99 | 570.541 | 292.167 | 443.042 | 1.953 | 1.288 | 1.516 |
| bidi-ping-pong / empty | p999 | 1081.375 | 1883.000 | 636.916 | 0.574 | 1.698 | 0.338 |
| bidi-ping-pong / empty | max | 6241.083 | 2916.417 | 6204.584 | 2.140 | 1.006 | 2.127 |
| bidi-ping-pong / 1k | mean | 352.525 | 156.798 | 310.439 | 2.248 | 1.136 | 1.980 |
| bidi-ping-pong / 1k | p50 | 345.334 | 132.583 | 306.250 | 2.605 | 1.128 | 2.310 |
| bidi-ping-pong / 1k | p99 | 599.292 | 322.959 | 443.416 | 1.856 | 1.352 | 1.373 |
| bidi-ping-pong / 1k | p999 | 1222.417 | 3325.083 | 845.833 | 0.368 | 1.445 | 0.254 |
| bidi-ping-pong / 1k | max | 2827.583 | 4150.459 | 3434.292 | 0.681 | 0.823 | 0.827 |

Source: selected fields transcribed from the user-supplied CSV for this automation run. This is not a replacement for the original full CSV.
