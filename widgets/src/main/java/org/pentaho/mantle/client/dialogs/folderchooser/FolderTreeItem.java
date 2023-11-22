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
 * Copyright (c) 2002-2023 Hitachi Vantara. All rights reserved.
 */

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

  private static class FolderTreeItemIterable implements Iterable<FolderTreeItem> {
    @NonNull
    private final TreeItem parentTreeItem;

    public FolderTreeItemIterable( @NonNull TreeItem parentTreeItem ) {
      Objects.requireNonNull( parentTreeItem );
      this.parentTreeItem = parentTreeItem;
    }

    @Override @NonNull
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
