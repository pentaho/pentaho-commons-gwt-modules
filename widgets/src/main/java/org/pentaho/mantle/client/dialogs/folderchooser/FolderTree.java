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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.UIObject;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.pentaho.gwt.widgets.client.genericfile.GenericFile;
import org.pentaho.gwt.widgets.client.genericfile.GenericFileNameUtils;
import org.pentaho.gwt.widgets.client.genericfile.GenericFileTree;
import org.pentaho.gwt.widgets.client.genericfile.GenericFileTreeComparator;
import org.pentaho.gwt.widgets.client.genericfile.GenericFileTreeJsonParser;
import org.pentaho.gwt.widgets.client.utils.ElementUtils;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;
import org.pentaho.mantle.client.dialogs.WaitPopup;
import org.pentaho.mantle.client.environment.EnvironmentHelper;
import org.pentaho.mantle.client.messages.Messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.pentaho.gwt.widgets.client.utils.ElementUtils.setStyleProperty;

public class FolderTree extends Tree {
  private static final String SELECTED_STYLE_NAME = "selected";
  private static final String HIDDEN_STYLE_NAME = "hidden";
  private static final String OPEN_STYLE_NAME = "open";
  private static final String LEAF_WIDGET_STYLE_NAME = "leaf-widget";
  private static final String PARENT_WIDGET_STYLE_NAME = "parent-widget";

  private boolean showLocalizedFileNames = true;
  private boolean showHiddenFiles;
  private int depth = -1;
  private boolean useDescriptionsForTooltip;
  private GenericFileTree rootTreeModel;
  private FolderTreeItem selectedItem;
  private String selectedPath;
  private static String homeFolder;

  private final FocusPanel focusable = new FocusPanel();

  public FolderTree() {
    super();

    setAnimationEnabled( true );
    sinkEvents( Event.ONDBLCLICK );

    Element element = getElement();
    element.setId( "solutionTree" );
    element.setAttribute( "oncontextmenu", "return false;" );
    setStyleProperty( element, "margin", "29px 0 10px 0" );

    Element focusableElement = focusable.getElement();
    focusableElement.setAttribute( "hideFocus", "true" );
    setStyleProperty( focusableElement, "fontSize", "0" );
    setStyleProperty( focusableElement, "position", "absolute" );
    setStyleProperty( focusableElement, "outline", "0" );
    setStyleProperty( focusableElement, "width", "1px" );
    setStyleProperty( focusableElement, "height", "1px" );
    setStyleProperty( focusableElement, "zIndex", "-1" );

    DOM.appendChild( element, focusableElement );
    DOM.sinkEvents( focusableElement, Event.FOCUSEVENTS );

    addSelectionHandler( this::handleItemSelection );
    addOpenHandler( this::handleOpen );
    addCloseHandler( this::handleClose );
  }

  public void fetchModel( final AsyncCallback<GenericFileTree> callback ) {
    // notify listeners that we are about to talk to the server (in case there's anything they want to do
    // such as busy cursor or tree loading indicators)
    onModelFetching();

    RequestBuilder builder = new RequestBuilder( RequestBuilder.GET, buildFetchUrl() );
    builder.setHeader( "Accept", "application/json" );
    builder.setHeader( "If-Modified-Since", "01 Jan 1970 00:00:00 GMT" );

    RequestCallback innerCallback = new RequestCallback() {

      public void onError( Request request, Throwable exception ) {
        Window.alert( exception.toString() );
      }

      public void onResponseReceived( Request request, Response response ) {
        if ( response.getStatusCode() == Response.SC_OK ) {
          final GenericFileTreeJsonParser parser = new GenericFileTreeJsonParser( response.getText() );
          final GenericFileTree fileTreeModel = parser.getTree();

          onModelFetched( fileTreeModel );
          if ( callback != null ) {
            callback.onSuccess( fileTreeModel );
          }
        }
      }
    };

    try {
      builder.sendRequest( null, innerCallback );
    } catch ( RequestException e ) {
      Window.alert( e.toString() );
    }
  }

  private String buildFetchUrl() {
    String url = EnvironmentHelper.getFullyQualifiedURL() + "plugin/scheduler-plugin/api/generic-files/folderTree?";

    return url + "depth=" + depth + "&showHidden=" + showHiddenFiles + "&ts=" + System.currentTimeMillis();
  }

  @Override
  public void onBrowserEvent( Event event ) {
    int eventType = DOM.eventGetType( event );
    switch ( eventType ) {
      case Event.ONMOUSEDOWN:
        if ( event.getButton() == NativeEvent.BUTTON_RIGHT ) {
          TreeItem treeItem = findTreeItemAtPosition( event.getClientX(), event.getClientY() );
          if ( treeItem != null ) {
            setSelectedItem( treeItem );
          }
        }
        break;

      case Event.ONCLICK:
        try {
          int[] scrollOffsets = ElementUtils.calculateScrollOffsets( getElement() );
          int[] offsets = ElementUtils.calculateOffsets( getElement() );
          focusable.getElement().getStyle()
            .setProperty( "top", ( event.getClientY() + scrollOffsets[ 1 ] - offsets[ 1 ] ) + "px" );
        } catch ( Exception ignored ) {
          // ignore any exceptions fired by this. Most likely a result of the element
          // not being on the DOM
        }
        break;

      default:
        break;
    }

    try {
      if ( DOM.eventGetType( event ) == Event.ONDBLCLICK ) {
        getSelectedItem().setState( !getSelectedItem().getState(), true );
      } else {
        super.onBrowserEvent( event );
      }
    } catch ( Exception ignored ) {
      // death to this browser event
    }

    TreeItem selItem = getSelectedItem();
    if ( selItem != null ) {
      selItem.getElement().scrollIntoView();
    }
  }

  @Nullable
  private TreeItem findTreeItemAtPosition( int x, int y ) {
    return findTreeItemAtPosition( null, x, y );
  }

  @Nullable
  private TreeItem findTreeItemAtPosition( @Nullable FolderTreeItem parentTreeItem, int x, int y ) {

    for ( FolderTreeItem childTreeItem : getChildItems( parentTreeItem ) ) {
      TreeItem foundTreeItem = findTreeItemAtPosition( childTreeItem, x, y );
      if ( foundTreeItem != null ) {
        return foundTreeItem;
      }
    }

    if ( parentTreeItem != null
      && x >= parentTreeItem.getAbsoluteLeft()
      && x <= parentTreeItem.getAbsoluteLeft() + parentTreeItem.getOffsetWidth()
      && y >= parentTreeItem.getAbsoluteTop()
      && y <= parentTreeItem.getAbsoluteTop() + parentTreeItem.getOffsetHeight() ) {
      return parentTreeItem;
    }

    return null;
  }

  @Override
  protected void onLoad() {
    super.onLoad();
    fixLeafNodes();
  }

  public void onModelFetching() {
    WaitPopup.getInstance().setVisible( true );

    if ( getSelectedItem() != null ) {
      selectedItem = (FolderTreeItem) getSelectedItem();
    }

    clear();
    addItem( new FolderTreeItem( Messages.getString( "loadingEllipsis" ) ) );
  }

  public void onModelFetched( GenericFileTree treeModel ) {

    setModel( treeModel );

    WaitPopup.getInstance().setVisible( false );
  }

  private void setModel( GenericFileTree treeModel ) {
    if ( treeModel == null ) {
      return;
    }

    this.rootTreeModel = treeModel;

    // Remember selectedPath and selectedItem, so we can reselect it after the tree is loaded
    onModelChanged();
  }

  private void onModelChanged() {
    buildSolutionTree();

    // Restore selection
    if ( selectedPath != null ) {
      select( selectedPath );
    } else if ( selectedItem != null ) {
      selectFromList( getTreeItemPath( selectedItem ) );
    } else {
      // Or, open all "root" nodes.
      for ( int i = 0; i < getItemCount(); i++ ) {
        getItem( i ).setState( true );
      }
    }
  }

  /**
   *
   */
  private void fixLeafNodes() {
    List<FolderTreeItem> allNodes = getAllNodes();
    for ( FolderTreeItem treeItem : allNodes ) {
      LeafItemWidget leafWidget;
      String itemText = treeItem.getText();
      GenericFileTree fileTreeModel = treeItem.getFileTreeModel();

      if ( fileTreeModel != null && fileTreeModel.getChildren().isEmpty() ) {
        leafWidget = new LeafItemWidget(
          itemText, "icon-tree-node", "icon-tree-leaf", "icon-folder", "icon-zoomable" );
      } else {
        leafWidget = new LeafItemWidget( itemText, "icon-tree-node", "icon-folder", "icon-zoomable" );
      }

      treeItem.setWidget( leafWidget );

      treeItem.getElement().getStyle().setProperty( "paddingLeft", "0px" );
    }
  }

  private List<FolderTreeItem> getAllNodes() {
    List<FolderTreeItem> nodeList = new ArrayList<>();
    for ( int i = 0; i < this.getItemCount(); i++ ) {
      nodeList.add( (FolderTreeItem) this.getItem( i ) );
      getAllNodes( (FolderTreeItem) this.getItem( i ), nodeList );
    }
    return nodeList;
  }

  private void getAllNodes( FolderTreeItem parent, List<FolderTreeItem> nodeList ) {
    for ( int i = 0; i < parent.getChildCount(); i++ ) {
      FolderTreeItem child = (FolderTreeItem) parent.getChild( i );
      nodeList.add( child );
      getAllNodes( child, nodeList );
    }
  }

  public void setSelectedPath( String path ) {
    selectedPath = path;
  }

  public void select( @Nullable String path ) {

    selectedPath = path;

    selectedItem = findTreeItem( selectedPath );

    if ( selectedItem != null ) {
      ensureTreeItemVisible( selectedItem );
      setSelectedItem( selectedItem, true );
    } else if ( selectedPath != null && !selectedPath.equals( getHomeFolder() ) ) {
      // If the given path did not exist, then select the home folder (recursive call).
      select( getHomeFolder() );
    }
  }

  // Based on Tree#ensureSelectedItemVisible. Allows ensuring a given tree item is visible
  // before making it selected, so that focus can be changed to it, when selected afterward.
  protected static void ensureTreeItemVisible( @NonNull TreeItem treeItem ) {
    TreeItem parentTreeItem = treeItem.getParentItem();
    while ( parentTreeItem != null ) {
      parentTreeItem.setState( true );
      parentTreeItem = parentTreeItem.getParentItem();
    }
  }

  // region findTreeItem et al.
  @Nullable
  FolderTreeItem findTreeItem( @Nullable String path ) {
    return findTreeItemRecursive( null, GenericFileNameUtils.splitPath( path ), 0 );
  }

  @Nullable
  private FolderTreeItem findTreeItemRecursive( @Nullable FolderTreeItem parentTreeItem,
                                                @NonNull List<String> pathSegments,
                                                int level ) {
    if ( level >= pathSegments.size() ) {
      return parentTreeItem;
    }

    for ( FolderTreeItem childTreeItem : getChildItems( parentTreeItem ) ) {
      FolderTreeItem foundTreeItem = matchTreeItemRecursive( childTreeItem, pathSegments, level );
      if ( foundTreeItem != null ) {
        return foundTreeItem;
      }
    }

    return null;
  }

  private FolderTreeItem matchTreeItemRecursive( @NonNull FolderTreeItem childTreeItem,
                                                 @NonNull List<String> pathSegments,
                                                 int level ) {

    GenericFile childFileModel = childTreeItem.getFileModel();

    if ( childFileModel.isGroupFolder() ) {
      // These do not contribute to the path, so just pass-through the same depth and keep looking.
      return findTreeItemRecursive( childTreeItem, pathSegments, level );
    }

    if ( childFileModel.getName().equalsIgnoreCase( pathSegments.get( level ) ) ) {
      // Matched. Continue with next level.
      return findTreeItemRecursive( childTreeItem, pathSegments, level + 1 );
    }

    return null;
  }
  // endregion

  @NonNull
  private static List<FolderTreeItem> getTreeItemPath( @Nullable FolderTreeItem treeItem ) {
    List<FolderTreeItem> treeItemPath = new ArrayList<>();
    while ( treeItem != null ) {
      treeItemPath.add( treeItem );
      treeItem = (FolderTreeItem) treeItem.getParentItem();
    }

    Collections.reverse( treeItemPath );

    return treeItemPath;
  }

  private void selectFromList( @NonNull List<FolderTreeItem> oldTreeItemPath ) {
    if ( oldTreeItemPath.isEmpty() ) {
      return;
    }

    FolderTreeItem recoveredTreeItem = recoverTreeItem( oldTreeItemPath );
    if ( recoveredTreeItem == null ) {
      return;
    }

    ensureTreeItemVisible( recoveredTreeItem );
    setSelectedItem( recoveredTreeItem );
  }

  @Nullable
  private FolderTreeItem recoverTreeItem( @NonNull List<FolderTreeItem> oldTreeItemPath ) {
    if ( oldTreeItemPath.isEmpty() ) {
      return null;
    }

    // Recovered tree item of path.
    FolderTreeItem newParentTreeItem = null;

    for ( FolderTreeItem oldTreeItem : oldTreeItemPath ) {
      newParentTreeItem = getChildTreeItemByFileName( newParentTreeItem, oldTreeItem.getFileName() );
      if ( newParentTreeItem == null ) {
        return null;
      }
    }

    // If execution got here, then at least one path segment was resolved.
    return newParentTreeItem;
  }

  @Nullable
  private FolderTreeItem getChildTreeItemByFileName( @Nullable FolderTreeItem parentTreeItem,
                                                     @NonNull String fileName ) {

    for ( FolderTreeItem childTreeItem : getChildItems( parentTreeItem ) ) {
      if ( fileName.equals( childTreeItem.getFileName() ) ) {
        return childTreeItem;
      }
    }

    return null;
  }

  private void handleItemSelection( SelectionEvent<TreeItem> event ) {

    if ( selectedItem != null ) {
      UIObject styleUIObject = getSelectionStyleUIObject( selectedItem );

      styleUIObject.removeStyleName( SELECTED_STYLE_NAME );

      if ( selectedItem.getFileModel().isHidden() ) {
        styleUIObject.addStyleDependentName( HIDDEN_STYLE_NAME );
      }
    }

    selectedItem = (FolderTreeItem) event.getSelectedItem();

    if ( selectedItem != null ) {
      UIObject styleUIObject = getSelectionStyleUIObject( selectedItem );

      styleUIObject.addStyleName( SELECTED_STYLE_NAME );

      if ( selectedItem.getFileModel().isHidden() ) {
        styleUIObject.removeStyleDependentName( HIDDEN_STYLE_NAME );
      }
    }
  }

  @NonNull
  private static UIObject getSelectionStyleUIObject( @NonNull FolderTreeItem treeItem ) {
    return treeItem.getWidget() instanceof LeafItemWidget
      ? treeItem.getWidget().getParent()
      : treeItem;
  }

  private void handleOpen( OpenEvent<TreeItem> event ) {
    TreeItem target = event.getTarget();

    // By default, expanding a node does not select it. Add that in here
    setSelectedItem( target );

    target.addStyleName( OPEN_STYLE_NAME );
  }

  private void handleClose( CloseEvent<TreeItem> event ) {
    event.getTarget().removeStyleName( OPEN_STYLE_NAME );
  }

  // region buildSolutionTree et al.
  private void buildSolutionTree() {

    clear();

    if ( shouldShowFileTreeModel( rootTreeModel ) ) {
      buildSolutionTree( this, rootTreeModel );

      fixLeafNodes();
    }
  }

  private boolean shouldShowFileTreeModel( @NonNull GenericFileTree fileTreeModel ) {
    GenericFile fileModel = fileTreeModel.getFile();
    return fileModel.isFolder() && ( !fileModel.isHidden() || isShowHiddenFiles() );
  }

  private void buildSolutionTree( @NonNull HasTreeItems treeItem, @NonNull GenericFileTree treeModel ) {

    List<GenericFileTree> childTreeModels = treeModel.getChildren();

    // BISERVER-9599 - Custom Sort
    childTreeModels.sort( new GenericFileTreeComparator( showLocalizedFileNames ) );

    for ( GenericFileTree childTreeModel : childTreeModels ) {
      // Excludes non-folders and, when !showHiddenFiles, also hidden folders.
      if ( shouldShowFileTreeModel( childTreeModel ) ) {
        FolderTreeItem childTreeItem = buildFolderTreeItem( childTreeModel );
        treeItem.addItem( childTreeItem );

        buildSolutionTree( childTreeItem, childTreeModel );
      }
    }
  }

  @NonNull
  private FolderTreeItem buildFolderTreeItem( @NonNull GenericFileTree fileTreeModel ) {
    GenericFile fileModel = fileTreeModel.getFile();

    String name = fileModel.getName();
    String title = fileModel.getTitleOrName();
    String description = fileModel.getDescription();

    FolderTreeItem treeItem = new FolderTreeItem();
    treeItem.setFileTreeModel( fileTreeModel );

    treeItem.getElement().setAttribute( "id", fileModel.getPath() );
    treeItem.setStylePrimaryName( LEAF_WIDGET_STYLE_NAME );
    if ( fileTreeModel.hasChildFolders() ) {
      treeItem.addStyleName( PARENT_WIDGET_STYLE_NAME );
    }

    if ( fileModel.isHidden() ) {
      treeItem.addStyleDependentName( HIDDEN_STYLE_NAME );
    }

    ElementUtils.killAllTextSelection( treeItem.getElement() );

    if ( showLocalizedFileNames ) {
      treeItem.setText( title );

      if ( isUseDescriptionsForTooltip() && !StringUtils.isEmpty( description ) ) {
        treeItem.setTitle( description );
      } else {
        treeItem.setTitle( name );
      }
    } else {
      treeItem.setText( name );

      if ( isUseDescriptionsForTooltip() && !StringUtils.isEmpty( description ) ) {
        treeItem.setTitle( description );
      } else {
        treeItem.setTitle( title );
      }
    }

    return treeItem;
  }
  // endregion

  public void setShowLocalizedFileNames( boolean showLocalizedFileNames ) {
    this.showLocalizedFileNames = showLocalizedFileNames;
    // use existing tree and switch text/title
    for ( int i = 0; i < getItemCount(); i++ ) {
      toggleLocalizedFileNames( (FolderTreeItem) getItem( i ) );
    }
  }

  private void toggleLocalizedFileNames( FolderTreeItem parentTreeItem ) {
    String title = parentTreeItem.getTitle();
    String text = parentTreeItem.getText();

    parentTreeItem.setTitle( text );
    parentTreeItem.setText( title );

    for ( int i = 0; i < parentTreeItem.getChildCount(); i++ ) {
      toggleLocalizedFileNames( (FolderTreeItem) parentTreeItem.getChild( i ) );
    }
  }

  public boolean isShowHiddenFiles() {
    return showHiddenFiles;
  }

  /**
   * Sets if hidden files should be displayed.
   * <p>
   * Only takes effect the next time the tree is refreshed.
   * <p>
   * Defaults to <code>false</code>.
   *
   * @param showHiddenFiles <code>true</code>, to show hidden files, <code>false</code>, to hide them.
   */
  public void setShowHiddenFiles( boolean showHiddenFiles ) {
    this.showHiddenFiles = showHiddenFiles;
  }

  public boolean isShowLocalizedFileNames() {
    return showLocalizedFileNames;
  }

  public boolean isUseDescriptionsForTooltip() {
    return useDescriptionsForTooltip;
  }

  public void setUseDescriptionsForTooltip( boolean useDescriptionsForTooltip ) {
    this.useDescriptionsForTooltip = useDescriptionsForTooltip;
    onModelFetched( rootTreeModel );
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth( int depth ) {
    if ( depth < 0 ) {
      depth = -1;
    }

    this.depth = depth;
  }

  public List<GenericFile> getFiles() {
    final FolderTreeItem selectedTreeItem = (FolderTreeItem) getSelectedItem();
    List<GenericFile> values = new ArrayList<>();
    values.add( selectedTreeItem.getFileModel() );
    return values;
  }

  /**
   * Retrieves the default home folder.
   *
   * @return The home folder
   */
  static String getHomeFolder() {
    return homeFolder != null ? homeFolder : refreshHomeFolder();
  }

  private static String refreshHomeFolder() {

    final String userHomeDirUrl = EnvironmentHelper.getFullyQualifiedURL() + "api/session/userWorkspaceDir";
    final RequestBuilder builder = new RequestBuilder( RequestBuilder.GET, userHomeDirUrl );

    try {
      // Get user home folder string
      RequestCallback rc = new RequestCallback() {
        @Override
        public void onResponseReceived( final Request request, final Response response ) {
          if ( response.getStatusCode() == 200 ) {
            // API returns /user/home_folder/workspace
            homeFolder = response.getText().replace( "/workspace", "" );
          }
        }

        @Override
        public void onError( Request request, Throwable exception ) {
          Window.alert( exception.toString() );
        }
      };
      builder.sendRequest( "", rc );

    } catch ( RequestException e ) {
      Window.alert( e.getMessage() );
    }

    return homeFolder;
  }

  @NonNull
  public Iterable<FolderTreeItem> getChildItems() {
    return getChildItems( null );
  }

  @NonNull
  protected Iterable<FolderTreeItem> getChildItems( @Nullable FolderTreeItem parentTreeItem ) {
    return parentTreeItem != null
      ? parentTreeItem.getChildItems()
      : new FolderTreeItemIterable( this );
  }

  private static class FolderTreeItemIterable implements Iterable<FolderTreeItem> {
    @NonNull
    private final FolderTree parentTree;

    public FolderTreeItemIterable( @NonNull FolderTree parentTree ) {
      Objects.requireNonNull( parentTree );
      this.parentTree = parentTree;
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
        return index < FolderTreeItemIterable.this.parentTree.getItemCount();
      }

      public FolderTreeItem next() {
        if ( index >= FolderTreeItemIterable.this.parentTree.getItemCount() ) {
          throw new NoSuchElementException();
        }

        return (FolderTreeItem) FolderTreeItemIterable.this.parentTree.getItem( index++ );
      }
    }
  }
}
