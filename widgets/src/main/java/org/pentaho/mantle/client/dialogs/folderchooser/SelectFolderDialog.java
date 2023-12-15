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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.pentaho.gwt.widgets.client.dialogs.PromptDialogBox;
import org.pentaho.gwt.widgets.client.genericfile.GenericFile;
import org.pentaho.gwt.widgets.client.panel.VerticalFlexPanel;
import org.pentaho.gwt.widgets.client.toolbar.Toolbar;
import org.pentaho.gwt.widgets.client.toolbar.ToolbarButton;
import org.pentaho.mantle.client.environment.EnvironmentHelper;
import org.pentaho.mantle.client.messages.Messages;

import static org.pentaho.gwt.widgets.client.utils.ElementUtils.setStyleProperty;

public class SelectFolderDialog extends PromptDialogBox {

  private class MySolutionTree extends FolderTree {
    public void onBrowserEvent( Event event ) {
      try {
        if ( DOM.eventGetType( event ) == Event.ONDBLCLICK
          && getSelectedItem().getChildCount() == 0
          && getSelectedItem().getFileModel().isCanAddChildren() ) {
          SelectFolderDialog.this.onOk();
          event.stopPropagation();
          event.preventDefault();
        } else {
          super.onBrowserEvent( event );
        }
      } catch ( Exception ex ) {
        // Window.alert(ex);
      }
    }
  }

  private final MySolutionTree tree;

  private final ToolbarButton addButton;

  private static String defaultSelectedPath = FolderTree.getHomeFolder();

  public SelectFolderDialog() {
    this( null );
  }

  public SelectFolderDialog( String selectedPath ) {
    super( Messages.getString( "selectFolder" ), Messages.getString( "ok" ), Messages.getString( "cancel" ),
      false, true );

    setResponsive( true );
    setSizingMode( DialogSizingMode.FILL_VIEWPORT_WIDTH );
    setWidthCategory( DialogWidthCategory.SMALL );

    tree = new MySolutionTree();
    tree.addSelectionHandler( event -> onSelectionChanged( tree.getSelectedFileModel() ) );

    SimplePanel treeWrapper = new SimplePanel( tree );
    treeWrapper.getElement().addClassName( "select-folder-tree" );
    setStyleProperty( tree.getElement(), "margin", "0" );

    Toolbar bar = new Toolbar();
    bar.addStyleName( "select-folder-toolbar" );
    bar.add( Toolbar.GLUE );

    addButton = createToolbarButtonAdd();
    bar.add( addButton );

    bar.add( createToolbarButtonRefresh() );

    VerticalPanel content = new VerticalFlexPanel();
    content.addStyleName( "with-layout-gap-none" );
    content.add( bar );
    content.add( treeWrapper );

    setContent( content );

    fetchModel( selectedPath != null ? selectedPath : defaultSelectedPath );
  }

  // region Create Toolbar Helpers
  @NonNull
  private ToolbarButton createToolbarButtonAdd() {
    ToolbarButton button = new ToolbarButton( createToolbarButtonImage( "pentaho-addbutton") );

    button.setToolTip( Messages.getString( "createNewFolder" ) );

    button.setCommand( () -> {
      GenericFile selectedFileModel = tree.getSelectedFileModel();
      if ( selectedFileModel != null ) {
        NewFolderCommand newFolderCommand = new NewFolderCommand( selectedFileModel );
        newFolderCommand.setCallback( this::fetchModel );
        newFolderCommand.execute();
      }
    } );

    return button;
  }

  @NonNull
  private ToolbarButton createToolbarButtonRefresh() {
    ToolbarButton button = new ToolbarButton( createToolbarButtonImage( "icon-refresh" ) );

    button.setToolTip( Messages.getString( "refreshTooltip" ) );

    button.setCommand( () -> {
      GenericFile selectedFileModel = tree.getSelectedFileModel();
      if ( selectedFileModel != null ) {
        RefreshFolderTreeCommand refreshFolderCommand = new RefreshFolderTreeCommand();
        refreshFolderCommand.setCallback( nothing -> fetchModel( null ) );
        refreshFolderCommand.execute();
      }
    } );

    return button;
  }

  @NonNull
  private static Image createToolbarButtonImage( @NonNull String styleName ) {
    Image image = new Image(
      EnvironmentHelper.getFullyQualifiedURL() + "content/common-ui/resources/themes/images/spacer.gif" );

    image.addStyleName( "icon-small" );
    image.addStyleName( "icon-zoomable" );
    image.addStyleName( styleName );
    return image;
  }
  // endregion

  protected void onSelectionChanged( @Nullable GenericFile selectedFileModel ) {
    boolean canAddChildren = selectedFileModel != null && selectedFileModel.isCanAddChildren();

    addButton.setEnabled( canAddChildren );
    okButton.setEnabled( canAddChildren );
  }

  public String getSelectedPath() {
    String selectedPath = tree.getSelectedPath();
    return selectedPath != null ? selectedPath : defaultSelectedPath;
  }

  private void fetchModel( String selectedPath ) {
    tree.fetchModel( null, selectedPath );
  }

  @Override
  protected void onOkValid() {
    // Accept the selected path as the default selected path.
    String selectedPath = tree.getSelectedPath();
    if ( selectedPath != null ) {
      setDefaultSelectedPath( selectedPath );
    }

    super.onOkValid();
  }

  private static void setDefaultSelectedPath( String selectedPath ) {
    defaultSelectedPath = selectedPath;
  }
}
