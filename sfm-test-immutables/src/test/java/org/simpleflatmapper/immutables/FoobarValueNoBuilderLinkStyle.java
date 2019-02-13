package org.simpleflatmapper.immutables;

import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
@Value.Style(typeImmutable = "Boo*")
public interface FoobarValueNoBuilderLinkStyle {
  int foo();
  String bar();
  UUID crux();
}