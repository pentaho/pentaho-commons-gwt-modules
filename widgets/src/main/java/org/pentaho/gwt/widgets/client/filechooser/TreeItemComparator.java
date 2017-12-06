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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
