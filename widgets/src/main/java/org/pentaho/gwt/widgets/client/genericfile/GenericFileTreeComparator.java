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

import java.util.Comparator;

public class GenericFileTreeComparator implements Comparator<GenericFileTree> {

  private boolean useTitle = true;

  public GenericFileTreeComparator() {
  }

  public GenericFileTreeComparator( boolean useTitle ) {
    this.useTitle = useTitle;
  }

  @Override
  public int compare( GenericFileTree fileTree1, GenericFileTree fileTree2 ) {
    return compare( getSortName( fileTree1 ), getSortName( fileTree2 ) );
  }

  @NonNull
  private String getSortName( @NonNull GenericFileTree fileTree ) {
    GenericFile file = fileTree.getFile();
    String sortName = useTitle ? file.getTitleOrName() : file.getName();
    return sortName == null ? "" : sortName;
  }

  private native int compare( @NonNull String a, @NonNull String b ) /*-{
    var aName = a.toLowerCase();
    var bName = b.toLowerCase();
    if(aName.localeCompare(bName) === 0) {
      // If values equalsIgnoreCase, use original values for comparison
      aName = a;
      bName = b;
      return (aName < bName) ? -1 : ((aName > bName) ? 1 : 0);
    }

    return aName.localeCompare(bName);
  }-*/;
}
