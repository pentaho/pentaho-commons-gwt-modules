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


package org.pentaho.gwt.widgets.client.filechooser;

import com.google.gwt.user.client.ui.TreeItem;

import java.util.Comparator;

/**
 * @author Rowell Belen
 */
public class TreeItemComparator implements Comparator<TreeItem> {

  public TreeItemComparator() {
    setupNativeHooks( this );
  }

  @Override
  public int compare( TreeItem treeItem1, TreeItem treeItem2 ) {
    RepositoryFileTree repositoryFileTree1 = (RepositoryFileTree) treeItem1.getUserObject();
    RepositoryFile repositoryFile1 = repositoryFileTree1.getFile();

    RepositoryFileTree repositoryFileTree2 = (RepositoryFileTree) treeItem2.getUserObject();
    RepositoryFile repositoryFile2 = repositoryFileTree2.getFile();

    return compare( repositoryFile1.getTitle(), repositoryFile2.getTitle() );
  }

  private static native void setupNativeHooks( TreeItemComparator comparator )
  /*-{
    $wnd.localeCompare = function(title1, title2) {
      return comparator.@org.pentaho.gwt.widgets.client.filechooser.TreeItemComparator::compare(Ljava/lang/String;Ljava/lang/String;)(title1, title2)
    }
  }-*/;

  // Code should be in sync with Mantle's browser.js, customSort~localeCompare
  public native int compare( String a, String b )
  /*-{

    var aTitle = a.toLowerCase();
    var bTitle = b.toLowerCase();

    if(aTitle.localeCompare(bTitle) == 0){
      // if values equalsIgnoreCase, use original values for comparison
      aTitle = a;
      bTitle = b;
      return ((aTitle < bTitle) ? -1 : ((aTitle > bTitle) ? 1 : 0));
    }
    else{
      return aTitle.localeCompare(bTitle);
    }

  }-*/;

}
