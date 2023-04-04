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
 * Copyright (c) 2002-2023 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.mantle.client.dialogs.folderchooser;

import com.google.gwt.user.client.ui.SimplePanel;
import org.pentaho.gwt.widgets.client.dialogs.MessageDialogBox;
import org.pentaho.gwt.widgets.client.dialogs.PromptDialogBox;
import org.pentaho.gwt.widgets.client.filechooser.RepositoryFileTree;
import org.pentaho.gwt.widgets.client.panel.VerticalFlexPanel;
import org.pentaho.gwt.widgets.client.toolbar.Toolbar;
import org.pentaho.gwt.widgets.client.toolbar.ToolbarButton;
import org.pentaho.gwt.widgets.client.ui.ICallback;
import org.pentaho.mantle.client.messages.Messages;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.pentaho.mantle.client.environment.EnvironmentHelper;

public class SelectFolderDialog extends PromptDialogBox {

  private static class MySolutionTree extends FolderTree {
    public SelectFolderDialog localThis;

    public MySolutionTree( boolean showTrash ) {
      super( showTrash );
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

  private static MySolutionTree tree = new MySolutionTree( false );

  public SelectFolderDialog() {
    super( Messages.getString( "selectFolder" ), Messages.getString( "ok" ), Messages.getString( "cancel" ),
      false, true );

    setResponsive( true );
    setSizingMode( DialogSizingMode.FILL_VIEWPORT_WIDTH );
    setWidthCategory( DialogWidthCategory.SMALL );

    if ( tree == null ) {
      tree = new MySolutionTree( false );
    }
    tree.localThis = this;

    SimplePanel treeWrapper = new SimplePanel( tree );
    treeWrapper.getElement().addClassName( "select-folder-tree" );
    tree.getElement().getStyle().setMargin( 0d, Unit.PX );

    Toolbar bar = new Toolbar();
    bar.addStyleName( "select-folder-toolbar" );
    bar.add( new Label( Messages.getString( "newFolderColon" ), false ) );

    bar.add( Toolbar.GLUE );

    Image image = new Image( EnvironmentHelper.getFullyQualifiedURL() + "content/common-ui/resources/themes/images/spacer.gif" );
    image.addStyleName( "icon-small" );
    image.addStyleName( "icon-zoomable" );
    image.addStyleName( "pentaho-addbutton" );
    ToolbarButton add = new ToolbarButton( image );
    add.setToolTip( Messages.getString( "createNewFolder" ) );
    add.setCommand( new Command() {
      public void execute() {
        final NewFolderCommand nfc =
            new NewFolderCommand( ( (FolderTreeItem) tree.getSelectedItem() ).getRepositoryFile() );
        nfc.setCallback( new ICallback<String>() {
          public void onHandle( final String path ) {
            tree.fetchRepositoryFileTree( new AsyncCallback<RepositoryFileTree>() {

              @Override
              public void onSuccess( RepositoryFileTree result ) {
                // TODO Auto-generated method stub
                tree.select( path );
              }

              @Override
              public void onFailure( Throwable caught ) {
                // TODO Auto-generated method stub

              }
            }, null, null, false );

          }
        } );
        nfc.execute();
      }
    } );
    bar.add( add );

    VerticalPanel content = new VerticalFlexPanel();
    content.addStyleName( "with-layout-gap-none" );
    content.add( bar );
    content.add( treeWrapper );

    setContent( content );
    fetchRepository( getSelectedPath() );

    TreeItem selItem = tree.getSelectedItem();
    if ( selItem != null ) {
      DOM.scrollIntoView( selItem.getElement() );
    }
  }

  public String getSelectedPath() {
    final FolderTreeItem selectedItem = (FolderTreeItem) tree.getSelectedItem();
    return selectedItem != null ? selectedItem.getRepositoryFile().getPath() : FolderTree.getHomeFolder();
  }

  private void fetchRepository( final String selectedPath ) {

    tree.fetchRepositoryFileTree( new AsyncCallback<RepositoryFileTree>() {

      @Override
      public void onSuccess( RepositoryFileTree result ) {
        tree.select( selectedPath );
      }

      @Override
      public void onFailure( Throwable caught ) {
        MessageDialogBox dialogBox =
                new MessageDialogBox( Messages.getString( "error" ), Messages.getString( "refreshRepository" ), false, false, true );
        dialogBox.center();
      }
    }, null, null, false );
  }
}
