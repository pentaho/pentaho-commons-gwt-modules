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

import java.util.ArrayList;
import java.util.List;

public class GenericFileTree {

  private GenericFile file;

  private List<GenericFileTree> children = new ArrayList<>();

  public GenericFileTree() {
  }

  public GenericFileTree( GenericFile file ) {
    this.file = file;
  }

  public GenericFile getFile() {
    return file;
  }

  public void setFile( GenericFile file ) {
    this.file = file;
  }

  public List<GenericFileTree> getChildren() {
    return children;
  }

  public void setChildren( List<GenericFileTree> children ) {
    this.children = children;
  }

  public void addChild( GenericFileTree tree ) {
    children.add( tree );
  }

  public boolean hasChildFolders() {
    return children != null && children.stream().anyMatch( child -> child.getFile().isFolder() );
  }
}
