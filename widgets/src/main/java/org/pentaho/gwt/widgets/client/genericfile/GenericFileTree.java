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

package org.pentaho.gwt.widgets.client.genericfile;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GenericFileTree {

  @NonNull
  private final GenericFile file;

  @Nullable
  private List<GenericFileTree> children;

  public GenericFileTree( @NonNull GenericFile file ) {
    this.file = Objects.requireNonNull( file );
  }

  @NonNull
  public GenericFile getFile() {
    return file;
  }

  @Nullable
  public List<GenericFileTree> getChildren() {
    return children;
  }

  public void setChildren( @Nullable List<GenericFileTree> children ) {
    this.children = children;
  }

  public void addChild( @NonNull GenericFileTree tree ) {
    if ( children == null ) {
      children = new ArrayList<>();
    }

    children.add( tree );
  }

  public boolean areChildrenLoaded() {
    return children != null;
  }

  public boolean hasChildren() {
    return children != null && !children.isEmpty();
  }

  public boolean hasChildFolders() {
    return children != null && children.stream().anyMatch( child -> child.getFile().isFolder() );
  }
}
