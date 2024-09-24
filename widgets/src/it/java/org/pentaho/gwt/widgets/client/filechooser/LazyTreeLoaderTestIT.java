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
