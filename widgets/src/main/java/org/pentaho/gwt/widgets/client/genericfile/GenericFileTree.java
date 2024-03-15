/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2023 Hitachi Vantara. All rights reserved.
 */

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
