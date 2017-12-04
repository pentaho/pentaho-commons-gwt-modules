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
