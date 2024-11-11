/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

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
    String sortName = useTitle ? file.getTitleOrNameDecoded() : file.getNameDecoded();
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
