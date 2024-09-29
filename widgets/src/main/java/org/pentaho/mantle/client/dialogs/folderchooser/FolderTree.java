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
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import org.pentaho.gwt.widgets.client.utils.NameUtils;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;
import org.pentaho.mantle.client.dialogs.WaitPopup;
import org.pentaho.mantle.client.messages.Messages;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

import static org.pentaho.gwt.widgets.client.utils.ElementUtils.setStyleProperty;
import static org.pentaho.mantle.client.environment.EnvironmentHelper.getFullyQualifiedURL;

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

  private GenericFileTree loadingTreeModel;

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

  public FolderTree() {
    setAnimationEnabled( true );
    sinkEvents( Event.ONDBLCLICK );

    Element element = getElement();
    element.setId( "solutionTree" );
    element.setAttribute( "oncontextmenu", "return false;" );
    setStyleProperty( element, "margin", "29px 0 10px 0" );

    addSelectionHandler( this::handleItemSelection );
    addOpenHandler( this::handleOpen );
    addCloseHandler( this::handleClose );
  }

  // region fetchTreeModel
  public void fetchTreeModel() {
    fetchTreeModel( null );
  }

  public void fetchTreeModel( @Nullable AsyncCallback<GenericFileTree> callback ) {
    fetchTreeModel( callback, null );
  }

  public void fetchTreeModel( @Nullable final AsyncCallback<GenericFileTree> callback,
                              @Nullable String initialSelectedPath ) {

    // Preserve currently selected path, if none is specified.
    // Must do this before calling clear, from within onModelFetching, which also clears selection.
    final String initialOrPreviousSelectedPath = initialSelectedPath != null ? initialSelectedPath : getSelectedPath();

    onTreeModelFetching();

    RequestBuilder builder = new RequestBuilder( RequestBuilder.GET, buildFetchTreeModelUrl( initialOrPreviousSelectedPath ) );
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

          onTreeModelFetched( fileTreeModel, initialOrPreviousSelectedPath );
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

  @NonNull
  private String getServiceBaseUrl() {
    return getFullyQualifiedURL() + "plugin/scheduler-plugin/api/generic-files/";
  }

  private String buildFetchTreeModelUrl( @Nullable String expandedPath ) {
    String url = getServiceBaseUrl() + "tree?"
      + "filter=FOLDERS"
      + "&depth=" + depth
      + "&showHidden=" + showHiddenFiles;

    if ( expandedPath != null ) {
      url += "&expandedPath=" + URL.encodeQueryString( expandedPath );
    }

    return url + "&ts=" + System.currentTimeMillis();
  }

  protected void onTreeModelFetching() {
    WaitPopup.getInstance().setVisible( true );

    clear();
    assert selectedItemLag == null : "Clear should have reset currently selected item";

    addItem( buildLoadingTreeItem() );
  }

  protected void onTreeModelFetched( @NonNull GenericFileTree treeModel, @Nullable String initialSelectedPath ) {

    setTreeModel( treeModel, initialSelectedPath );

    WaitPopup.getInstance().setVisible( false );
  }
  // endregion

  // region fetchSubtreeModel
  private void fetchSubtreeModel( @NonNull FolderTreeItem treeItem ) {

    onSubtreeModelFetching( treeItem );

    String basePath = treeItem.getFileModel().getPath();

    RequestBuilder builder = new RequestBuilder( RequestBuilder.GET, buildFetchSubtreeModelUrl( basePath ) );
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

          onSubtreeModelFetched( fileTreeModel, treeItem );
        } else {
          onSubtreeModelFetched( null, treeItem );
        }
      }
    };

    try {
      builder.sendRequest( null, innerCallback );
    } catch ( RequestException e ) {
      Window.alert( e.toString() );
    }
  }

  private void onSubtreeModelFetching( @NonNull FolderTreeItem treeItem ) {
    treeItem.setLoading( true );
  }

  private void onSubtreeModelFetched( @Nullable GenericFileTree fileTreeModel, @NonNull FolderTreeItem treeItem ) {
    if ( !treeItem.isLoading() ) {
      return;
    }

    treeItem.setLoading( false );

    // Failed, or otherwise still did not return the children ?
    if ( fileTreeModel == null || fileTreeModel.getChildren() == null ) {
      // Close the item.
      treeItem.setState( false );
      return;
    }

    // Only update the model's children.
    treeItem.getFileTreeModel().setChildren( fileTreeModel.getChildren() );
    fileTreeModel = treeItem.getFileTreeModel();

    // Remove the Loading... tree item.
    treeItem.removeItems();

    buildSolutionTree( treeItem, fileTreeModel );

    fixTreeItemsWidgets( treeItem );

    if ( !fileTreeModel.hasChildren() ) {
      treeItem.removeStyleName( PARENT_WIDGET_STYLE_NAME );

      treeItem.setState( false );
    }
  }

  private String buildFetchSubtreeModelUrl( @NonNull String basePath ) {
    return getServiceBaseUrl()
      + NameUtils.URLEncode( GenericFileNameUtils.encodePath( basePath ) )
      + "/tree?"
      + "filter=FOLDERS"
      + "&depth=1"
      + "&showHidden=" + showHiddenFiles
      + "&ts=" + System.currentTimeMillis();
  }
  // endregion

  // region Loading Tree Item
  @NonNull
  private GenericFileTree buildLoadingTreeModel() {
    GenericFile fileModel = new GenericFile();
    fileModel.setName( Messages.getString( "loadingEllipsis" ) );
    fileModel.setCanAddChildren( false );

    return new GenericFileTree( fileModel );
  }

  @NonNull
  private GenericFileTree getLoadingTreeModel() {
    if ( loadingTreeModel == null ) {
      loadingTreeModel = buildLoadingTreeModel();
    }

    return loadingTreeModel;
  }

  @NonNull
  private FolderTreeItem buildLoadingTreeItem() {
    GenericFileTree treeModel = getLoadingTreeModel();

    FolderTreeItem loadingTreeItem = new FolderTreeItem( treeModel.getFile().getTitleOrNameDecoded() );
    loadingTreeItem.setFileTreeModel( treeModel );

    return loadingTreeItem;
  }

  private boolean isLoadingTreeItem( @NonNull FolderTreeItem treeItem ) {
    return loadingTreeModel != null && loadingTreeModel == treeItem.getFileTreeModel();
  }
  // endregion

  @Override
  public void onBrowserEvent( Event event ) {
    switch ( DOM.eventGetType( event ) ) {
      case Event.ONMOUSEDOWN:
        if ( event.getButton() == NativeEvent.BUTTON_RIGHT ) {
          TreeItem treeItem = findTreeItemAtPosition( event.getClientX(), event.getClientY() );
          if ( treeItem != null ) {
            setSelectedItem( treeItem );
          }
        }
        break;

      case Event.ONDBLCLICK:
        FolderTreeItem selectedItem = getSelectedItem();
        if ( selectedItem != null ) {
          selectedItem.setState( !selectedItem.getState(), true );
        }
        return;

      default:
        break;
    }

    super.onBrowserEvent( event );
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

  protected void setTreeModel( @NonNull GenericFileTree treeModel, @Nullable String initialSelectedPath ) {

    this.rootTreeModel = treeModel;

    onModelChanged( initialSelectedPath );
  }

  protected void onModelChanged( @Nullable String initialSelectedPath ) {

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

  // region fixTreeItemsWidgets et al.
  private void fixTreeItemsWidgets() {
    traverseTreeItems( this::fixTreeItemWidget );
  }

  private void fixTreeItemsWidgets( @NonNull FolderTreeItem treeItem ) {
    traverseTreeItems( treeItem, this::fixTreeItemWidget );
  }

  private void fixTreeItemWidget( @NonNull FolderTreeItem treeItem ) {
    GenericFileTree fileTreeModel = treeItem.getFileTreeModel();

    LeafItemWidget leafWidget;
    String itemText = treeItem.getText();
    if ( fileTreeModel != null && ( fileTreeModel.areChildrenLoaded() && !fileTreeModel.hasChildren() ) ) {
      leafWidget = new LeafItemWidget(
        itemText, "icon-tree-node", "icon-tree-leaf", "icon-folder", "icon-zoomable" );
    } else {
      leafWidget = new LeafItemWidget( itemText, "icon-tree-node", "icon-folder", "icon-zoomable" );
    }

    treeItem.setWidget( leafWidget );

    treeItem.getElement().getStyle().setProperty( "paddingLeft", "0px" );
  }

  private void traverseTreeItems( @NonNull Consumer<FolderTreeItem> consumer ) {
    for ( int i = 0; i < getItemCount(); i++ ) {
      traverseTreeItems( (FolderTreeItem) getItem( i ), consumer );
    }
  }

  private void traverseTreeItems( @NonNull FolderTreeItem treeItem, @NonNull Consumer<FolderTreeItem> consumer ) {

    if ( !isLoadingTreeItem( treeItem ) ) {
      consumer.accept( treeItem );
    }

    for ( int i = 0; i < treeItem.getChildCount(); i++ ) {
      traverseTreeItems( (FolderTreeItem) treeItem.getChild( i ), consumer );
    }
  }
  // endregion

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

    if ( isLoadingTreeItem( childTreeItem ) ) {
      return null;
    }

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

  // region Selection
  @Override
  public FolderTreeItem getSelectedItem() {
    return (FolderTreeItem) super.getSelectedItem();
  }

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

  public void select( @Nullable String path ) {
    TreeItem newSelectedItem = findTreeItem( path );
    if ( newSelectedItem != null ) {
      setSelectedItem( newSelectedItem, true );
    } else if ( path != null && !path.equals( getHomeFolder() ) ) {
      // If the given path did not exist, then select the home folder (recursive call).
      select( getHomeFolder() );
    }
  }

  @Nullable
  public GenericFile getSelectedFileModel() {
    final FolderTreeItem selectedItem = getSelectedItem();
    return selectedItem != null ? selectedItem.getFileModel() : null;
  }

  @Nullable
  public String getSelectedPath() {
    final GenericFile fileModel = getSelectedFileModel();
    return fileModel != null ? fileModel.getPath() : null;
  }

  private void handleItemSelection( @NonNull SelectionEvent<TreeItem> event ) {

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
        FolderTreeItem selectedItem = getSelectedItem();
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
  // endregion

  // region Interaction helpers
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
  // endregion

  private void handleOpen( OpenEvent<TreeItem> event ) {
    if ( !( event.getTarget() instanceof FolderTreeItem ) ) {
      return;
    }

    FolderTreeItem openingTreeItem = (FolderTreeItem) event.getTarget();
    if ( !openingTreeItem.isLoading() ) {
      GenericFileTree treeModel = openingTreeItem.getFileTreeModel();
      if ( treeModel != null && !treeModel.areChildrenLoaded() ) {
        fetchSubtreeModel( openingTreeItem );
      }
    }

    openingTreeItem.addStyleName( OPEN_STYLE_NAME );
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

      fixTreeItemsWidgets();
    }
  }

  private boolean shouldShowFileTreeModel( @NonNull GenericFileTree fileTreeModel ) {
    GenericFile fileModel = fileTreeModel.getFile();
    return fileModel.isFolder() && ( !fileModel.isHidden() || isShowHiddenFiles() );
  }

  private void buildSolutionTree( @NonNull HasTreeItems treeItem, @NonNull GenericFileTree treeModel ) {

    List<GenericFileTree> childTreeModels = treeModel.getChildren();

    // Children loaded?
    if ( childTreeModels != null ) {
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
    } else {
      treeItem.addItem( buildLoadingTreeItem() );
    }
  }

  @NonNull
  private FolderTreeItem buildFolderTreeItem( @NonNull GenericFileTree fileTreeModel ) {
    GenericFile fileModel = fileTreeModel.getFile();

    assert !fileModel.isGroupFolder() : "Folder tree item should not be mapped to group folder";

    String name = fileModel.getNameDecoded();
    String title = fileModel.getTitleOrNameDecoded();
    String description = fileModel.getDescription();

    FolderTreeItem treeItem = new FolderTreeItem();
    treeItem.setFileTreeModel( fileTreeModel );

    treeItem.getElement().setAttribute( "id", fileModel.getPath() );
    treeItem.setStylePrimaryName( LEAF_WIDGET_STYLE_NAME );
    if ( !fileTreeModel.areChildrenLoaded() || fileTreeModel.hasChildFolders() ) {
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

    setTreeModel( rootTreeModel, null );
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

  /**
   * Gets the default home folder.
   *
   * @return The home folder
   */
  static native String getHomeFolder() /*-{
    return $wnd.top.HOME_FOLDER;
  }-*/;

  // region Child Tree Items
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
  // endregion
}
