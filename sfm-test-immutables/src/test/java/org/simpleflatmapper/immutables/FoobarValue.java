package org.simpleflatmapper.immutables;

import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface FoobarValue {
  static ImmutableFoobarValue.Builder builder() {
    return ImmutableFoobarValue.builder();
  }
  int foo();
  String bar();
  UUID crux();
}