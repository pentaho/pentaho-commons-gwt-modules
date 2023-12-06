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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Command;
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

  /**
   * Stores the currently selected item in duplication of the base class' private field,
   * obtained via {@link #getSelectedItem()}. This is because the selection changed event
   * as exposed by {@link #addSelectionHandler(SelectionHandler)} occurs after selection has changed and
   * does not provide the previously selected item, which is needed for CSS style maintenance.
   * <p>
   * This field is updated only from within {@link #handleItemSelection(SelectionEvent)}, in response to an actual
   * change of {@link #getSelectedItem()}.
   */
  private FolderTreeItem selectedItemLag;

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

    // TODO: Is this always null??
    selectedItemLag = (FolderTreeItem) getSelectedItem();
    addSelectionHandler( this::handleItemSelection );

    addOpenHandler( this::handleOpen );
    addCloseHandler( this::handleClose );
  }

  public void fetchModel() {
    fetchModel( null );
  }

  public void fetchModel( @Nullable AsyncCallback<GenericFileTree> callback ) {
    fetchModel( callback, null );
  }

  public void fetchModel( @Nullable final AsyncCallback<GenericFileTree> callback,
                          @Nullable String initialSelectedPath ) {

    // Preserve currently selected path, if none is specified.
    // Must do this before calling clear, from within onModelFetching, which also clears selection.
    final String initialOrPreviousSelectedPath = initialSelectedPath != null ? initialSelectedPath : getSelectedPath();

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

          onModelFetched( fileTreeModel, initialOrPreviousSelectedPath );
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
        // TODO: Is this achieving anything?
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
  }

  // region Tree item mouse hit test
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
  // endregion

  @Override
  protected void onLoad() {
    super.onLoad();
    // TODO: Is this really needed on load?
    fixLeafNodes();
  }

  public void onModelFetching() {
    WaitPopup.getInstance().setVisible( true );

    clear();
    assert selectedItemLag == null : "Clear should have reset currently selected item";

    addItem( new FolderTreeItem( Messages.getString( "loadingEllipsis" ) ) );
  }

  public void onModelFetched( @NonNull GenericFileTree treeModel, @Nullable String initialSelectedPath ) {

    setModel( treeModel, initialSelectedPath );

    WaitPopup.getInstance().setVisible( false );
  }

  private void setModel( @NonNull GenericFileTree treeModel, @Nullable String initialSelectedPath ) {

    this.rootTreeModel = treeModel;

    onModelChanged( initialSelectedPath );
  }

  private void onModelChanged( @Nullable String initialSelectedPath ) {

    // Preserve currently selected path, if none is specified.
    // Do this before calling buildSolutionTree, below, which clears the tree, including selection.
    if ( initialSelectedPath == null ) {
      initialSelectedPath = getSelectedPath();
    }

    buildSolutionTree();

    if ( initialSelectedPath != null ) {
      select( initialSelectedPath );
    } else {
      openRootTreeItems();
    }
  }

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

  @Nullable
  public String getSelectedPath() {
    final FolderTreeItem selectedItem = (FolderTreeItem) getSelectedItem();
    return selectedItem != null ? selectedItem.getFileModel().getPath() : null;
  }

  public void select( @Nullable String path ) {
    TreeItem newSelectedItem = findTreeItem( path );
    if ( newSelectedItem != null ) {
      setSelectedItem( newSelectedItem, true );
    } else if ( path != null && !path.equals( getHomeFolder() ) ) {
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

  protected void openRootTreeItems() {
    // Or, open all "root" nodes.
    for ( int i = 0; i < getItemCount(); i++ ) {
      getItem( i ).setState( true );
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

    assert !childFileModel.isGroupFolder() : "Folder tree item should not be mapped to group folder";

    String pathSegment = pathSegments.get( level );
    if ( childFileModel.isProviderRootFolder() ) {
      // The names of provider root folders do not match the path segment (e.g. "Repository" vs "/").
      if ( childFileModel.getPath().equals( pathSegment ) ) {
        // Matched. Continue with next level.
        return findTreeItemRecursive( childTreeItem, pathSegments, level + 1 );
      }
    } else if ( childFileModel.getName().equalsIgnoreCase( pathSegment ) ) {
      // Matched. Continue with next level.
      return findTreeItemRecursive( childTreeItem, pathSegments, level + 1 );
    }

    return null;
  }
  // endregion

  @Override
  public void setSelectedItem( TreeItem item, boolean fireEvents ) {
    if ( item != null ) {
      ensureTreeItemVisible( item );
    }

    super.setSelectedItem( item, fireEvents );

    if ( fireEvents && getSelectedItem() != selectedItemLag ) {
      // Unfortunately, the base class never fires the selection event when selection is cleared (null),
      // causing the local handler, handleItemSelection, to not be called.
      // So, call the local handler here, explicitly, with a locally instantiated event object.
      handleItemSelection( new SelectionEvent<TreeItem>( getSelectedItem() ) {
      } );
    }
  }

  private void handleItemSelection( SelectionEvent<TreeItem> event ) {

    if ( selectedItemLag != null ) {
      UIObject styleUIObject = getSelectionStyleUIObject( selectedItemLag );

      styleUIObject.removeStyleName( SELECTED_STYLE_NAME );

      if ( selectedItemLag.getFileModel().isHidden() ) {
        styleUIObject.addStyleDependentName( HIDDEN_STYLE_NAME );
      }
    }

    selectedItemLag = (FolderTreeItem) event.getSelectedItem();

    if ( selectedItemLag != null ) {
      UIObject styleUIObject = getSelectionStyleUIObject( selectedItemLag );

      styleUIObject.addStyleName( SELECTED_STYLE_NAME );

      if ( selectedItemLag.getFileModel().isHidden() ) {
        styleUIObject.removeStyleDependentName( HIDDEN_STYLE_NAME );
      }

      // TODO: Still not working in all cases. Just on arrow keys... Not working on initial loading.
      Scheduler.get().scheduleDeferred( (Command) () -> {
        FolderTreeItem selectedItem = (FolderTreeItem) getSelectedItem();
        if ( selectedItem != null ) {
          selectedItem.getElement().scrollIntoView();
        }
      } );
    }
  }

  @NonNull
  private static UIObject getSelectionStyleUIObject( @NonNull FolderTreeItem treeItem ) {
    return treeItem.getWidget() instanceof LeafItemWidget
      ? treeItem.getWidget().getParent()
      : treeItem;
  }

  private void handleOpen( OpenEvent<TreeItem> event ) {
   event.getTarget().addStyleName( OPEN_STYLE_NAME );
  }

  private void handleClose( CloseEvent<TreeItem> event ) {
    event.getTarget().removeStyleName( OPEN_STYLE_NAME );
  }

  // region buildSolutionTree et al.
  private void buildSolutionTree() {

    // Includes getting rid of the "Loading" tree item, if any.
    clear();
    assert selectedItemLag == null : "Clear should have reset currently selected item";

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

    assert !fileModel.isGroupFolder() : "Folder tree item should not be mapped to group folder";

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

    setModel( rootTreeModel, null );
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
    return new FolderTreeItemIterable( this );
  }

  @NonNull
  protected Iterable<FolderTreeItem> getChildItems( @Nullable FolderTreeItem parentTreeItem ) {
    return parentTreeItem != null ? parentTreeItem.getChildItems() : getChildItems();
  }

  private static class FolderTreeItemIterable implements Iterable<FolderTreeItem> {
    @NonNull
    private final FolderTree parentTree;

    public FolderTreeItemIterable( @NonNull FolderTree parentTree ) {
      Objects.requireNonNull( parentTree );
      this.parentTree = parentTree;
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
