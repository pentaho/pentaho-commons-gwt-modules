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
 * Copyright (c) 2019 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.gwt.widgets.client.filechooser;

import com.google.gwt.junit.client.GWTTestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LazyTreeLoaderTestIT extends GWTTestCase {

  private LazyTreeLoader lazyTreeLoader = new LazyTreeLoader();

  @Test
  public void testInsertTree() {
    RepositoryFileTree rootTree = createTree( "/", "" );
    RepositoryFileTree level1Tree = createTree( "/home", "home" );
    RepositoryFileTree level2Tree = createTree( "/home/public", "public" );
    RepositoryFileTree level3Tree = createTree( "/home/public/test", "test");

    rootTree.getChildren().add( level1Tree );
    level1Tree.getChildren().add( level2Tree );
    level2Tree.getChildren().add( level3Tree );

    RepositoryFileTree replaceTree = createTree( "/home", "" );
    RepositoryFileTree rTree1 = createTree( "/home/home1", "home1" );
    RepositoryFileTree rTree2 = createTree( "/home/home2", "home2" );

    replaceTree.getChildren().add( rTree1 );
    replaceTree.getChildren().add( rTree2 );

    lazyTreeLoader.insertTree( rootTree, replaceTree );
    assertEquals( "home1", rootTree.children.get( 0 ).getChildren().get( 0 ).getFile().getName() );
    assertEquals( "home2", rootTree.children.get( 0 ).getChildren().get( 1 ).getFile().getName() );
  }

  private RepositoryFileTree createTree( String path, String name ) {
    RepositoryFileTree repositoryFileTree = new RepositoryFileTree();
    RepositoryFile repositoryFile = new RepositoryFile();
    repositoryFile.setPath( path );
    repositoryFile.setName( name );
    repositoryFile.setFolder( true );
    repositoryFileTree.setFile( repositoryFile );
    List<RepositoryFileTree> children = new ArrayList<RepositoryFileTree>();
    repositoryFileTree.setChildren( children );
    return repositoryFileTree;
  }

  public String getModuleName() {
    return "org.pentaho.gwt.widgets.Widgets";
  }

}
