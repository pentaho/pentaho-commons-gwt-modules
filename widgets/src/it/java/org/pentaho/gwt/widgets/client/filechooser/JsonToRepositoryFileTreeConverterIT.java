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

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.junit.client.GWTTestCase;

import java.util.List;

public class JsonToRepositoryFileTreeConverterIT extends GWTTestCase {
  public String getModuleName() {
    return "org.pentaho.gwt.widgets.Widgets"; //$NON-NLS-1$
  }

  public void testGetRepositoryFileTree() throws Exception {
    JsonToRepositoryFileTreeConverter converter = new JsonToRepositoryFileTreeConverter( "" );

    final JSONObject jsonObject = new JSONObject();
    final JSONObject file = new JSONObject();
    final String fileName = "fileName";
    file.put( "name", new JSONString( fileName ) );
    jsonObject.put( "file", file );

    final JSONArray children = new JSONArray();
    final JSONObject child = new JSONObject();
    final JSONObject childFile = new JSONObject();
    final String childFileName = "childFileName";
    childFile.put( "name", new JSONString( childFileName ) );
    child.put( "file", childFile );
    children.set( 0, child );
    jsonObject.put( "children", children );

    final RepositoryFileTree repositoryFileTree = converter.getRepositoryFileTree( jsonObject );
    assertNotNull( repositoryFileTree );
    assertEquals( fileName, repositoryFileTree.getFile().getName() );
    assertEquals( 1, repositoryFileTree.getChildren().size() );
    assertEquals( childFileName, repositoryFileTree.getChildren().get( 0 ).getFile().getName() );
  }

  public void testGetFileListFromJson() throws Exception {
    final String fileName1 = "fileName1";
    final String fileName2 = "fileName2";
    final List<RepositoryFile> fileListFromJson = JsonToRepositoryFileTreeConverter
        .getFileListFromJson( "{repositoryFileDto:[{name:'" + fileName1 + "'},{name:'" + fileName2 + "'}]}" );
    assertEquals( 2, fileListFromJson.size() );
    assertEquals( fileName1, fileListFromJson.get( 0 ).getName() );
    assertEquals( fileName2, fileListFromJson.get( 1 ).getName() );
  }
}
