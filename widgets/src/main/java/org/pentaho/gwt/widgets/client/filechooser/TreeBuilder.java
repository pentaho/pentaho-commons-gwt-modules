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

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import java.util.ArrayList;
import java.util.List;

public class TreeBuilder {

  public static Tree buildSolutionTree( RepositoryFileTree fileTree, boolean showHiddenFiles,
      boolean showLocalizedFileNames, FileFilter filter ) {
    // build a tree structure to represent the document
    Tree repositoryTree = new Tree();
    // get document root item
    RepositoryFile rootFile = fileTree.getFile();
    TreeItem rootItem = new TreeItem();
    rootItem.setText( rootFile.getPath() );
    rootItem.setUserObject( fileTree );
    repositoryTree.addItem( rootItem );

    // default file filter that accepts anything
    if ( filter == null ) {
      filter = new DefaultFileFilter();
    }
    buildSolutionTree( rootItem, fileTree, showHiddenFiles, showLocalizedFileNames, filter );
    return repositoryTree;
  }

  private static void buildSolutionTree( TreeItem parentTreeItem, RepositoryFileTree fileTree, boolean showHiddenFiles,
      boolean showLocalizedFileNames, FileFilter filter ) {
    for ( RepositoryFileTree repositoryFileTree : fileTree.getChildren() ) {
      RepositoryFile file = repositoryFileTree.getFile();
      boolean isVisible = !file.isHidden();
      boolean isDirectory = file.isFolder();

      if ( isVisible || showHiddenFiles ) {
        String fileTitle = file.getTitle(), fileName = file.getName();

        if ( filter.accept( fileName, isDirectory, ( isVisible || showHiddenFiles ) ) == false ) {
          continue;
        }

        TreeItem childTreeItem = new TreeItem();
        // TODO There is no concept of filename and a localized filename in the repository. Do we need this ?
        if ( showLocalizedFileNames ) {
          childTreeItem.setText( fileTitle );
          childTreeItem.setTitle( fileTitle );
        } else {
          childTreeItem.setText( fileTitle );
          childTreeItem.setTitle( fileTitle );
        }

        // ElementUtils.preventTextSelection(childTreeItem.getElement());

        childTreeItem.setUserObject( repositoryFileTree );

        // find the spot in the parentTreeItem to insert the node (based on showLocalizedFileNames)
        if ( parentTreeItem.getChildCount() == 0 ) {
          parentTreeItem.addItem( childTreeItem );
        } else {
          // this does sorting
          boolean inserted = false;
          for ( int j = 0; j < parentTreeItem.getChildCount(); j++ ) {
            TreeItem kid = (TreeItem) parentTreeItem.getChild( j );
            if ( showLocalizedFileNames ) {
              if ( childTreeItem.getText().compareTo( kid.getText() ) <= 0 ) {
                // leave all items ahead of the insert point
                // remove all items between the insert point and the end
                // add the new item
                // add back all removed items
                List<TreeItem> removedItems = new ArrayList<TreeItem>();
                for ( int x = j; x < parentTreeItem.getChildCount(); x++ ) {
                  TreeItem removedItem = (TreeItem) parentTreeItem.getChild( x );
                  removedItems.add( removedItem );
                }
                for ( TreeItem removedItem : removedItems ) {
                  parentTreeItem.removeItem( removedItem );
                }
                parentTreeItem.addItem( childTreeItem );
                inserted = true;
                for ( TreeItem removedItem : removedItems ) {
                  parentTreeItem.addItem( removedItem );
                }
                break;
              }
            } else {
              parentTreeItem.addItem( childTreeItem );
            }
          }
          if ( !inserted ) {
            parentTreeItem.addItem( childTreeItem );
          }
        }

        if ( isDirectory ) {
          buildSolutionTree( childTreeItem, repositoryFileTree, showHiddenFiles, showLocalizedFileNames, filter );
        }
      }
    }
  }

  private static class DefaultFileFilter implements FileFilter {
    public boolean accept( String name, boolean isDirectory, boolean isVisible ) {
      return true;
    }
  }
}
