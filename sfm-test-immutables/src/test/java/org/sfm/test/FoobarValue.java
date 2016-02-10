package org.sfm.test;

import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public abstract class FoobarValue {
  public static FoobarValue of(int foo, String bar, UUID crux) {
    return ImmutableFoobarValue.builder().foo(foo).bar(bar).crux(crux).build();
  }
  public abstract int foo();
  public abstract String bar();
  public abstract UUID crux();
}