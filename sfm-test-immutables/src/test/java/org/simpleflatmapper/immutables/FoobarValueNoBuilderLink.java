package org.simpleflatmapper.immutables;

import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface FoobarValueNoBuilderLink {
  int foo();
  String bar();
  UUID crux();
}