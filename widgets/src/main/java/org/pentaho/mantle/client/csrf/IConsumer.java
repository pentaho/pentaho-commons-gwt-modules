/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/
package org.pentaho.mantle.client.csrf;

// Lambda expressions, and Java 8 types are only supported from GWT 2.8 onwards...
// Imitates java.util.function.Consumer<T>
@FunctionalInterface
public interface IConsumer<T> {
  void accept( T value );
}
