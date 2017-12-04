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

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.TreeItem;

public class TreeItemComparatorIT extends GWTTestCase {
  public String getModuleName() {
    return "org.pentaho.gwt.widgets.Widgets"; //$NON-NLS-1$
  }

  public void testCompare() throws Exception {
    TreeItemComparator comparator = new TreeItemComparator();

    assertEquals( -1, comparator.compare( populateTreeItem( "aaa" ), populateTreeItem( "bbb" ) ) );
    assertEquals( 1, comparator.compare( populateTreeItem( "bbb" ), populateTreeItem( "aaa" ) ) );
    assertEquals( 0, comparator.compare( populateTreeItem( "aaa" ), populateTreeItem( "aaa" ) ) );
    assertEquals( 1, comparator.compare( populateTreeItem( "aaa" ), populateTreeItem( "aAa" ) ) );
    assertEquals( -1, comparator.compare( populateTreeItem( "aAa" ), populateTreeItem( "aaa" ) ) );
  }

  private TreeItem populateTreeItem( String title ) {
    final TreeItem treeItem = new TreeItem();
    final RepositoryFileTree repositoryFileTree = new RepositoryFileTree();
    treeItem.setUserObject( repositoryFileTree );
    final RepositoryFile repositoryFile = new RepositoryFile();
    repositoryFileTree.setFile( repositoryFile );
    repositoryFile.setTitle( title );

    return treeItem;
  }
}
