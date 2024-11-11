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
