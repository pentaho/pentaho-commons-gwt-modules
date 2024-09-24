package org.pentaho.mantle.client.csrf;

// Lambda expressions, and Java 8 types are only supported from GWT 2.8 onwards...
// Imitates java.util.function.Consumer<T>
@FunctionalInterface
public interface IConsumer<T> {
  void accept( T value );
}
