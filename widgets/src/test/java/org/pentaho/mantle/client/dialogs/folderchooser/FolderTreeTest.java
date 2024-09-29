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


package org.pentaho.mantle.client.dialogs.folderchooser;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class FolderTreeTest {

  @Test
  public void select_simpleTest() {

    String path = "/this/is/a/fake/path";
    FolderTree tree = mock( FolderTree.class );

    doCallRealMethod().when( tree ).select( path );

    tree.select( path );

    verify( tree ).select( path );
    verify( tree ).findTreeItem( anyString() );
  }
}
