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


package javax.validation;

/**
 * GWT super-source override of {@code javax.validation.Path}.
 *
 * <p><b>Why this file exists:</b> {@code validation-api 1.1.0.Final} added two new methods to the
 * {@code Path.Node} interface — {@code getKind()} and {@code as(Class<T>)} — that were not present
 * in {@code 1.0.0.GA}. The GWT runtime jar ({@code gwt-user-2.x}) bundles its own translatable
 * implementation of {@code Path.Node} ({@code NodeImpl}) that only satisfies the 1.0 API surface.
 * When the GWT compiler processes the bundled {@code NodeImpl} source against the 1.1 jar it fails
 * with "must implement inherited abstract method Path.Node.getKind()".
 *
 * <p><b>Fix:</b> This super-source file replaces {@code javax.validation.Path} <em>for GWT
 * translatable code only</em> with a version that exposes only the 1.0 API shape, allowing
 * {@code gwt-user}'s {@code NodeImpl} to compile cleanly. The regular JVM classpath is unaffected
 * and continues to use the full {@code 1.1.0.Final} jar at runtime.
 *
 * <p><b>Referenced by:</b> {@code ValidationCompat.gwt.xml} via {@code <super-source path="super"/>}.
 *
 * <p><b>Do not add {@code getKind()} or {@code as(Class)} here.</b> Doing so would re-introduce
 * the compile failure in {@code gwt-user}'s {@code NodeImpl}.
 */
public interface Path extends Iterable<Path.Node> {

  /**
   * Represents a single node (element) in a property path.
   * Intentionally limited to the Bean Validation 1.0 API surface.
   */
  interface Node {
    String getName();

    boolean isInIterable();

    Integer getIndex();

    Object getKey();
  }
}
