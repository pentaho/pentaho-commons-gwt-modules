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


package org.pentaho.mantle.client.dialogs.folderchooser;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.pentaho.gwt.widgets.client.genericfile.GenericFile;
import org.pentaho.gwt.widgets.client.genericfile.GenericFileTree;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class FolderTreeItem extends TreeItem {

  private static final String LOADING_STYLE_NAME = "loading";

  private boolean isLoading;

  public FolderTreeItem() {
  }

  public FolderTreeItem( Widget widget ) {
    super( widget );
  }

  /**
   * @param string
   */
  public FolderTreeItem( String string ) {
    super( ( new SafeHtmlBuilder() ).appendEscaped( string ).toSafeHtml() );
    getElement().setId( string );
  }


  @Override
  public GenericFileTree getUserObject() {
    return (GenericFileTree) super.getUserObject();
  }

  @Override
  public void setUserObject( Object userObject ) {
    if ( !( userObject instanceof GenericFileTree ) ) {
      throw new IllegalArgumentException( "Must be an instance of " + GenericFileTree.class.getSimpleName() );
    }

    super.setUserObject( userObject );
  }

  public GenericFileTree getFileTreeModel() {
    return getUserObject();
  }

  public void setFileTreeModel( GenericFileTree fileTreeModel ) {
    setUserObject( fileTreeModel );
  }

  public String getFileName() {
    GenericFile fileModel = getFileModel();
    return fileModel != null ? fileModel.getName() : null;
  }

  public GenericFile getFileModel() {
    GenericFileTree fileTreeModel = getFileTreeModel();
    return fileTreeModel != null ? fileTreeModel.getFile() : null;
  }

  @NonNull
  public Iterable<FolderTreeItem> getChildItems() {
    return new FolderTreeItemIterable( this );
  }

  public boolean isLoading() {
    return isLoading;
  }

  public void setLoading( boolean loading ) {
    if ( this.isLoading != loading ) {

      this.isLoading = loading;

      if ( this.isLoading ) {
        addStyleName( LOADING_STYLE_NAME );
      } else {
        removeStyleName( LOADING_STYLE_NAME );
      }
    }
  }

  private static class FolderTreeItemIterable implements Iterable<FolderTreeItem> {
    @NonNull
    private final TreeItem parentTreeItem;

    public FolderTreeItemIterable( @NonNull TreeItem parentTreeItem ) {
      Objects.requireNonNull( parentTreeItem );
      this.parentTreeItem = parentTreeItem;
    }

    @Override
    @NonNull
    public Iterator<FolderTreeItem> iterator() {
      return new FolderTreeItemIterator();
    }

    private class FolderTreeItemIterator implements Iterator<FolderTreeItem> {
      private int index;

      public FolderTreeItemIterator() {
        this.index = 0;
      }

      public boolean hasNext() {
        return index < FolderTreeItemIterable.this.parentTreeItem.getChildCount();
      }

      public FolderTreeItem next() {
        if ( index >= FolderTreeItemIterable.this.parentTreeItem.getChildCount() ) {
          throw new NoSuchElementException();
        }

        return (FolderTreeItem) FolderTreeItemIterable.this.parentTreeItem.getChild( index++ );
      }
    }
  }
}
