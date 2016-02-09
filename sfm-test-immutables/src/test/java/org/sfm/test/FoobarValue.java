package org.sfm.test;

import org.immutables.value.Value;

@Value.Immutable
public abstract class FoobarValue {
  public static FoobarValue of(int foo, String bar) {
    return ImmutableFoobarValue.builder().foo(foo).bar(bar).build();
  }
  public abstract int foo();
  public abstract String bar();
}