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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.pentaho.gwt.widgets.client.dialogs.MessageDialogBox;
import org.pentaho.gwt.widgets.client.dialogs.PromptDialogBox;
import org.pentaho.gwt.widgets.client.genericfile.GenericFile;
import org.pentaho.gwt.widgets.client.genericfile.GenericFileTree;
import org.pentaho.gwt.widgets.client.panel.VerticalFlexPanel;
import org.pentaho.gwt.widgets.client.toolbar.Toolbar;
import org.pentaho.gwt.widgets.client.toolbar.ToolbarButton;
import org.pentaho.mantle.client.environment.EnvironmentHelper;
import org.pentaho.mantle.client.messages.Messages;

import static org.pentaho.gwt.widgets.client.utils.ElementUtils.setStyleProperty;

public class SelectFolderDialog extends PromptDialogBox {

  private static class MySolutionTree extends FolderTree {
    public SelectFolderDialog localThis;

    public MySolutionTree() {
      super();
      super.setScrollOnSelectEnabled( false );
    }

    public void onBrowserEvent( Event event ) {
      try {
        if ( DOM.eventGetType( event ) == Event.ONDBLCLICK && getSelectedItem().getChildCount() == 0 ) {
          localThis.onOk();
          event.stopPropagation();
          event.preventDefault();
        } else {
          super.onBrowserEvent( event );
        }
      } catch ( Throwable t ) {
        // Window.alert(t);
      }
    }
  }

  private static final MySolutionTree tree = new MySolutionTree();

  private String defaultSelectedPath = FolderTree.getHomeFolder();

  public SelectFolderDialog() {
    this( FolderTree.getHomeFolder() );
  }

  public SelectFolderDialog( String selectedPath ) {
    super( Messages.getString( "selectFolder" ), Messages.getString( "ok" ), Messages.getString( "cancel" ),
      false, true );

    setResponsive( true );
    setSizingMode( DialogSizingMode.FILL_VIEWPORT_WIDTH );
    setWidthCategory( DialogWidthCategory.SMALL );

    if ( selectedPath != null ) {
      tree.setSelectedPath( selectedPath );
      defaultSelectedPath = selectedPath;
    }
    tree.localThis = this;

    SimplePanel treeWrapper = new SimplePanel( tree );
    treeWrapper.getElement().addClassName( "select-folder-tree" );
    setStyleProperty( tree.getElement(), "margin", "0" );

    Toolbar bar = new Toolbar();
    bar.addStyleName( "select-folder-toolbar" );
    bar.add( new Label( Messages.getString( "newFolderColon" ), false ) );
    bar.add( Toolbar.GLUE );

    Image image =
      new Image( EnvironmentHelper.getFullyQualifiedURL() + "content/common-ui/resources/themes/images/spacer.gif" );
    image.addStyleName( "icon-small" );
    image.addStyleName( "icon-zoomable" );
    image.addStyleName( "pentaho-addbutton" );

    ToolbarButton add = new ToolbarButton( image );
    add.setToolTip( Messages.getString( "createNewFolder" ) );
    add.setCommand( () -> {
      GenericFile fileModel = ( (FolderTreeItem) tree.getSelectedItem() ).getFileModel();
      final NewFolderCommand newFolderCommand = new NewFolderCommand( fileModel );

      newFolderCommand.setCallback( path -> tree.fetchModel( new AsyncCallback<GenericFileTree>() {
        @Override
        public void onSuccess( GenericFileTree fileTreeModel ) {
          tree.select( path );
        }

        @Override
        public void onFailure( Throwable caught ) {
          // noop
        }
      }, null, null, false ) );

      newFolderCommand.execute();
    } );
    bar.add( add );

    VerticalPanel content = new VerticalFlexPanel();
    content.addStyleName( "with-layout-gap-none" );
    content.add( bar );
    content.add( treeWrapper );

    setContent( content );
    fetchModel( getSelectedPath() );

    TreeItem selItem = tree.getSelectedItem();
    if ( selItem != null ) {
      DOM.scrollIntoView( selItem.getElement() );
    }
  }

  public void cancelSelection() {
    tree.select( defaultSelectedPath );
  }

  public String getSelectedPath() {
    final FolderTreeItem selectedItem = (FolderTreeItem) tree.getSelectedItem();
    return selectedItem != null ? selectedItem.getFileModel().getPath() : defaultSelectedPath;
  }

  private void fetchModel( final String selectedPath ) {

    tree.fetchModel( new AsyncCallback<GenericFileTree>() {

      @Override
      public void onSuccess( GenericFileTree fileTreeModel ) {
        tree.select( selectedPath );
      }

      @Override
      public void onFailure( Throwable caught ) {
        MessageDialogBox dialogBox = new MessageDialogBox(
          Messages.getString( "error" ),
          Messages.getString( "refreshRepository" ),
          false,
          false,
          true );
        dialogBox.center();
      }
    }, null, null, false );
  }
}
