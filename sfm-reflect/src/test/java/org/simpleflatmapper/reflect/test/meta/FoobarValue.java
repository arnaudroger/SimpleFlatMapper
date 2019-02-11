package org.simpleflatmapper.reflect.test.meta;

import java.util.UUID;

public interface FoobarValue {
  static ImmutableFoobarValue.Builder builder() {
    return ImmutableFoobarValue.builder();
  }
  int foo();
  String bar();
  UUID crux();
}