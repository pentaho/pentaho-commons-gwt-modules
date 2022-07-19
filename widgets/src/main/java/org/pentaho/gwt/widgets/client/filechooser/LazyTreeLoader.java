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

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the construction of the RepositoryFileTree that serves as the file open/save browser model
 */
public class LazyTreeLoader {

  public static final String DELIMITER = "/";
  public static final String CHILDREN = "children";
  public static final String PATH = "path";
  public static final String FOLDER = "folder";
  public static final String FILE = "file";
  public static final String TITLE = "title";
  public static final String NAME = "name";

  private Map<String, RepositoryFileTree> cache = new HashMap<String, RepositoryFileTree>();

  /**
   * Checks to see if a RepositoryFileTree is cached for a specific path
   *
   * @param path
   * @return
   */
  public boolean isCached( String path ) {
    return cache.containsKey( path );
  }

  /**
   * Gets the cached RepositoryFileTree object by path
   *
   * @param path
   * @return
   */
  public RepositoryFileTree getCached( String path ) {
    return cache.get( path );
  }

  /**
   * Cache a RepositoryFileTree object by path
   *
   * @param path
   * @param fileTree
   */
  public void cache( String path, RepositoryFileTree fileTree ) {
    this.cache.put( path, fileTree );
  }

  /**
   * Takes a json string and creates empty parent directories to complete the RepositoryFileTree structure
   *
   * @param json
   * @return
   */
  public String buildTree( String json ) {
    JSONObject jsonObject = JSONParser.parseLenient( json ).isObject();
    JSONObject file = (JSONObject) jsonObject.get( FILE );
    String path = ( (JSONString) file.get( PATH ) ).stringValue();

    String[] parts = path.split( DELIMITER );
    JSONObject root = buildPath( DELIMITER, "" );
    JSONObject currentParent = root;
    String currentPath = "";
    for ( int i = 1; i < parts.length - 1; i++ ) {
      currentPath = currentPath + DELIMITER + parts[ i ];
      JSONObject child = findChildByPath( currentParent, currentPath );
      JSONArray children;
      if ( child == null ) {
        children = (JSONArray) currentParent.get( CHILDREN );
      } else {
        children = (JSONArray) child.get( CHILDREN );
      }
      currentParent = buildPath( currentPath, parts[ i ] );
      children.set( 0, currentParent );
    }

    JSONArray children = (JSONArray) currentParent.get( CHILDREN );
    children.set( 0, jsonObject );

    return root.toString();
  }

  /**
   * Locates the RepositoryFileTree object in the rootTree by path and replaces it with a new object
   *
   * @param rootTree
   * @param fileTree
   */
  public void insertTree( RepositoryFileTree rootTree, RepositoryFileTree fileTree ) {
    String path = fileTree.getFile().getPath();
    String parentPath = path.substring( 0, path.lastIndexOf( DELIMITER ) );
    parentPath = parentPath.equals( "" ) ? DELIMITER : parentPath;
    RepositoryFileTree parentTree = findParentByPath( rootTree, parentPath );
    if ( parentTree != null ) {
      int childIndex = -1;
      for ( int i = 0; i < parentTree.getChildren().size(); i++ ) {
        RepositoryFileTree childTree = parentTree.getChildren().get( i );
        if ( childTree.getFile().getPath().equals( fileTree.getFile().getPath() ) ) {
          childIndex = i;
        }
      }
      if ( childIndex != -1 ) {
        parentTree.getChildren().set( childIndex, fileTree );
      }
    }
  }

  /**
   * Recusively search for a parent folder in the RepositoryFileTree from a path
   *
   * @param tree
   * @param path
   * @return
   */
  private RepositoryFileTree findParentByPath( RepositoryFileTree tree, String path ) {
    if ( tree.getFile().getPath().equals( path ) ) {
      return tree;
    }
    for ( RepositoryFileTree child : tree.getChildren() ) {
      if ( child.getFile().isFolder() && child.getFile().getPath().equals( path ) ) {
        return child;
      }
      if ( child.getFile().isFolder() && !child.getChildren().isEmpty() ) {
        RepositoryFileTree repositoryFileTree = findParentByPath( child, path );
        if ( repositoryFileTree != null ) {
          return repositoryFileTree;
        }
      }
    }
    return null;
  }

  /**
   * File a child on a JSONObject parent by a path
   *
   * @param parent
   * @param path
   * @return
   */
  private JSONObject findChildByPath( JSONObject parent, String path ) {
    JSONArray children = (JSONArray) parent.get( CHILDREN );
    for ( int i = 0; i < children.size(); i++ ) {
      JSONObject child = (JSONObject) children.get( 0 );
      JSONString childPath = (JSONString) child.get( PATH );
      if ( childPath.stringValue().equals( path ) ) {
        return child;
      }
    }
    return null;
  }

  /**
   * Build scaffolding for a complete file tree quickly so we can lazy load later
   *
   * @param path
   * @param title
   * @return
   */
  private JSONObject buildPath( String path, String title ) {
    JSONObject file = new JSONObject();
    file.put( FOLDER, new JSONString( String.valueOf( true ) ) );
    file.put( TITLE, new JSONString( title ) );
    file.put( NAME, new JSONString( title ) );
    file.put( PATH, new JSONString( path ) );

    JSONObject parent = new JSONObject();
    parent.put( FILE, file );
    parent.put( CHILDREN, new JSONArray() );
    return parent;
  }

}
