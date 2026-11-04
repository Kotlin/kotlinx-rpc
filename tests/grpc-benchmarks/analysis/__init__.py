"""Offline analysis of gRPC benchmark trial artifacts.

The package reads the JSON trial records and run manifests that the benchmark
clients and the `bench` runner write. It never starts a client or contacts the
benchmark server.
"""

TRIAL_SCHEMA_VERSION = 1

COMPLETED = "completed"
FAILED = "failed"
