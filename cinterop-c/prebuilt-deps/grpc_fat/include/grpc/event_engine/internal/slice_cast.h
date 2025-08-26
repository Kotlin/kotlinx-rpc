// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

#ifndef GRPC_EVENT_ENGINE_INTERNAL_SLICE_CAST_H
#define GRPC_EVENT_ENGINE_INTERNAL_SLICE_CAST_H

namespace grpc_event_engine {
namespace experimental {
namespace internal {

// Opt-in trait class for slice conversions.
// Declare a specialization of this class for any types that are compatible
// with `SliceCast`. Both ways need to be declared (i.e. if
// SliceCastable<A,B> exists, you should declare
// SliceCastable<B,A> too).
// The type has no members, it's just the existence of the specialization that
// unlocks SliceCast usage for a type pair.
template <typename Result, typename T>
struct SliceCastable;

// This is strictly too wide, but consider all types to be SliceCast-able to
// themselves.
// Unfortunately this allows `const int& x = SliceCast<int>(x);` which is kind
// of bogus.
template <typename A>
struct SliceCastable<A, A> {};

// Cast to `const Result&` from `const T&` without any runtime checks.
// This is only valid if `sizeof(Result) == sizeof(T)`, and if `Result`, `T` are
// opted in as compatible via `SliceCastable`.
template <typename Result, typename T>
const Result& SliceCast(const T& value, SliceCastable<Result, T> = {}) {
  // Insist upon sizes being equal to catch mismatches.
  // We assume if sizes are opted in and sizes are equal then yes, these two
  // types are expected to be layout compatible and actually appear to be.
  static_assert(sizeof(Result) == sizeof(T), "size mismatch");
  return reinterpret_cast<const Result&>(value);
}

// Cast to `Result&` from `T&` without any runtime checks.
// This is only valid if `sizeof(Result) == sizeof(T)`, and if `Result`, `T` are
// opted in as compatible via `SliceCastable`.
template <typename Result, typename T>
Result& SliceCast(T& value, SliceCastable<Result, T> = {}) {
  // Insist upon sizes being equal to catch mismatches.
  // We assume if sizes are opted in and sizes are equal then yes, these two
  // types are expected to be layout compatible and actually appear to be.
  static_assert(sizeof(Result) == sizeof(T), "size mismatch");
  return reinterpret_cast<Result&>(value);
}

// Cast to `Result&&` from `T&&` without any runtime checks.
// This is only valid if `sizeof(Result) == sizeof(T)`, and if `Result`, `T` are
// opted in as compatible via `SliceCastable`.
template <typename Result, typename T>
Result&& SliceCast(T&& value, SliceCastable<Result, T> = {}) {
  // Insist upon sizes being equal to catch mismatches.
  // We assume if sizes are opted in and sizes are equal then yes, these two
  // types are expected to be layout compatible and actually appear to be.
  static_assert(sizeof(Result) == sizeof(T), "size mismatch");
  return reinterpret_cast<Result&&>(value);
}

}  // namespace internal
}  // namespace experimental
}  // namespace grpc_event_engine

#endif  // GRPC_EVENT_ENGINE_INTERNAL_SLICE_CAST_H
