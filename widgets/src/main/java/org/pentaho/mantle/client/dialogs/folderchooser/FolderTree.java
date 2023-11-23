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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pentaho.gwt.widgets.client.genericfile.GenericFile;
import org.pentaho.gwt.widgets.client.genericfile.GenericFileTree;
import org.pentaho.gwt.widgets.client.genericfile.GenericFileTreeComparator;
import org.pentaho.gwt.widgets.client.genericfile.GenericFileTreeJsonParser;
import org.pentaho.gwt.widgets.client.utils.ElementUtils;
import org.pentaho.gwt.widgets.client.utils.string.StringTokenizer;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;
import org.pentaho.mantle.client.dialogs.WaitPopup;
import org.pentaho.mantle.client.messages.Messages;

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
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import org.pentaho.mantle.client.environment.EnvironmentHelper;

import static org.pentaho.gwt.widgets.client.utils.ElementUtils.setStyleProperty;

public class FolderTree extends Tree {
  private static final String SELECTED_STYLE_NAME = "selected";
  private static final String HIDDEN_STYLE_NAME = "hidden";
  private static final String OPEN_STYLE_NAME = "open";

  private boolean showLocalizedFileNames = true;
  private boolean showHiddenFiles = false;
  private boolean createRootNode = false;
  private boolean useDescriptionsForTooltip = false;
  public GenericFileTree fileTreeModel;
  private TreeItem selectedItem = null;
  private String selectedPath = null;
  private static String homeFolder = null;

  private FocusPanel focusable = new FocusPanel();

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

    this.addSelectionHandler( this::handleItemSelection );
    this.addOpenHandler( this::handleOpen );
    this.addCloseHandler( this::handleClose );

    onModelFetching();
    fetchModel( null, null, null, showHiddenFiles );
  }

  public void fetchModel( final AsyncCallback<GenericFileTree> callback, Integer depth, String filter,
                          Boolean showHidden ) {
    // notify listeners that we are about to talk to the server (in case there's anything they want to do
    // such as busy cursor or tree loading indicators)
    onModelFetching();

    RequestBuilder builder = null;
    String url = EnvironmentHelper.getFullyQualifiedURL() + "plugin/scheduler-plugin/api/generic-files/folderTree?";

    if ( depth == null ) {
      depth = -1;
    }
    if ( filter == null ) {
      filter = "*"; //$NON-NLS-1$
    }
    if ( showHidden == null ) {
      showHidden = Boolean.FALSE;
    }
    url =
        url
            + "depth=" + depth + "&filter=" + filter + "&showHidden=" + showHidden + "&ts=" + System.currentTimeMillis();
    builder = new RequestBuilder( RequestBuilder.GET, url );
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

  public void onBrowserEvent( Event event ) {
    int eventType = DOM.eventGetType( event );
    switch ( eventType ) {
      case Event.ONMOUSEDOWN:
        if ( DOM.eventGetButton( event ) == NativeEvent.BUTTON_RIGHT ) {
          TreeItem selectedItem = findSelectedItem( null, event.getClientX(), event.getClientY() );
          if ( selectedItem != null ) {
            setSelectedItem( selectedItem );
          }
        }
        break;
      case Event.ONMOUSEUP:
        break;
      case Event.ONCLICK:
        try {
          int[] scrollOffsets = ElementUtils.calculateScrollOffsets( getElement() );
          int[] offsets = ElementUtils.calculateOffsets( getElement() );
          DOM.setStyleAttribute( focusable.getElement(),
              "top", ( event.getClientY() + scrollOffsets[1] - offsets[1] ) + "px" ); //$NON-NLS-1$ //$NON-NLS-2$
        } catch ( Exception ignored ) {
          // ignore any exceptions fired by this. Most likely a result of the element
          // not being on the DOM
        }
        break;
    }

    try {

      if ( DOM.eventGetType( event ) == Event.ONDBLCLICK ) {
        getSelectedItem().setState( !getSelectedItem().getState(), true );
      } else {
        super.onBrowserEvent( event );
      }
    } catch ( Throwable t ) {
      // death to this browser event
    }
    TreeItem selItem = getSelectedItem();
    if ( selItem != null ) {
      DOM.scrollIntoView( selItem.getElement() );
    }
  }

  private TreeItem findSelectedItem( TreeItem item, int x, int y ) {
    if ( item == null ) {
      for ( int i = 0; i < getItemCount(); i++ ) {
        TreeItem selected = findSelectedItem( getItem( i ), x, y );
        if ( selected != null ) {
          return selected;
        }
      }
      return null;
    }

    for ( int i = 0; i < item.getChildCount(); i++ ) {
      TreeItem selected = findSelectedItem( item.getChild( i ), x, y );
      if ( selected != null ) {
        return selected;
      }
    }

    if ( x >= item.getAbsoluteLeft() && x <= item.getAbsoluteLeft() + item.getOffsetWidth()
        && y >= item.getAbsoluteTop() && y <= item.getAbsoluteTop() + item.getOffsetHeight() ) {
      return item;
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
      selectedItem = getSelectedItem();
    }
    clear();
    addItem( new FolderTreeItem( Messages.getString( "loadingEllipsis" ) ) ); //$NON-NLS-1$
    WaitPopup.getInstance().setVisible( false );
  }

  public void onModelFetched( GenericFileTree fileTreeModel ) {

    if ( fileTreeModel == null ) {
      WaitPopup.getInstance().setVisible( false );
      return;
    }

    this.fileTreeModel = fileTreeModel;

    // remember selectedItem, so we can reselect it after the tree is loaded
    clear();
    // get document root item
    GenericFile fileModel = this.fileTreeModel.getFile();
    if ( !fileModel.isHidden() || isShowHiddenFiles() ) {
      FolderTreeItem rootItem = null;
      if ( createRootNode ) {
        rootItem = new FolderTreeItem();
        rootItem.setText( fileModel.getPath() );
        rootItem.setTitle( fileModel.getPath() );
        rootItem.getElement().setId( fileModel.getPath() );
        // added so we can traverse the true names
        rootItem.setFileName( "/" ); //$NON-NLS-1$
        rootItem.setFileModel( fileModel );
        addItem( rootItem );
        buildSolutionTree( rootItem, this.fileTreeModel );
      } else {
        buildSolutionTree( null, this.fileTreeModel );
      }
    }

    fixLeafNodes();

    if ( selectedPath != null ) {
      select( selectedPath );
    } else if ( selectedItem != null ) {
      ArrayList<TreeItem> parents = new ArrayList<>();
      while ( selectedItem != null ) {
        parents.add( selectedItem );
        selectedItem = selectedItem.getParentItem();
      }
      Collections.reverse( parents );
      selectFromList( parents );
    } else {
      for ( int i = 0; i < getItemCount(); i++ ) {
        getItem( i ).setState( true );
      }
    }
    WaitPopup.getInstance().setVisible( false );
  }

  /**
   *
   */
  private void fixLeafNodes() {
    List<FolderTreeItem> allNodes = getAllNodes();
    for ( FolderTreeItem treeItem : allNodes ) {
      LeafItemWidget leafWidget;
      String itemText = treeItem.getText();
      GenericFileTree userObject = (GenericFileTree) treeItem.getUserObject();

      if (userObject != null && userObject.getChildren().isEmpty()) {
        leafWidget = new LeafItemWidget(
          itemText,"icon-tree-node", "icon-tree-leaf", "icon-folder", "icon-zoomable" );
      } else {
        leafWidget = new LeafItemWidget( itemText, "icon-tree-node", "icon-folder", "icon-zoomable" );
      }

      treeItem.setWidget( leafWidget );

      DOM.setStyleAttribute( treeItem.getElement(), "paddingLeft", "0px" );
    }
  }

  private List<FolderTreeItem> getAllNodes() {
    List<FolderTreeItem> nodeList = new ArrayList<FolderTreeItem>();
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

  public void select( String path ) {
    this.selectedPath = path;

    selectedItem  = getTreeItem( path );
    if ( selectedItem != null ) {
      ArrayList<TreeItem> parents = new ArrayList<>();

      TreeItem item = selectedItem;
      setSelectedItem( item, false );

      while ( item != null ) {
        parents.add( item );
        item = item.getParentItem();
      }

      Collections.reverse( parents );
      selectFromList( parents );
    } else if ( path != null && !path.equals( getHomeFolder() ) ) {
      select( getHomeFolder() );
    }
  }

  private FolderTreeItem getTreeItem( String path ) {
    List<String> pathSegments = new ArrayList<>();

    if ( path != null ) {
      String normalizedPath = path.startsWith( "/" )
        ? path.substring( 1 )
        : path;

      StringTokenizer st = new StringTokenizer( normalizedPath, '/' );
      for ( int i = 0; i < st.countTokens(); i++ ) {
        String token = st.tokenAt( i );
        pathSegments.add( token );
      }
    }

    return getTreeItem( pathSegments );
  }

  public FolderTreeItem getTreeItem( final List<String> pathSegments ) {
    if ( pathSegments.size() > 0 ) {
      // the first path segment is going to be a 'root' in the tree
      String rootSegment = pathSegments.get( 0 );
      for ( int i = 0; i < getItemCount(); i++ ) {
        FolderTreeItem root = (FolderTreeItem) getItem( i );
        if ( root.getFileName().equalsIgnoreCase( rootSegment ) ) {
          return getTreeItem( root, pathSegments.subList( 1, pathSegments.size() ) );
        }
      }
    }
    return null;
  }

  private FolderTreeItem getTreeItem( final FolderTreeItem root, final List<String> pathSegments ) {
    int depth = 0;
    FolderTreeItem currentItem = root;
    while ( depth < pathSegments.size() ) {
      String pathSegment = pathSegments.get( depth );
      for ( int i = 0; i < currentItem.getChildCount(); i++ ) {
        FolderTreeItem childItem = (FolderTreeItem) currentItem.getChild( i );
        if ( childItem.getFileName().equalsIgnoreCase( pathSegment ) ) {
          currentItem = childItem;
        }
      }
      depth++;
    }
    // let's check if the currentItem matches our segments (it might point to the last item before
    // we eventually failed to find the complete match)
    FolderTreeItem tmpItem = currentItem;
    depth = pathSegments.size() - 1;
    while ( tmpItem != null && depth >= 0 ) {
      if ( tmpItem.getFileName().equalsIgnoreCase( pathSegments.get( depth ) ) ) {
        tmpItem = (FolderTreeItem) tmpItem.getParentItem();
        depth--;
      } else {
        // every item must match
        return null;
      }
    }

    return currentItem;
  }

  private void selectFromList( List<TreeItem> parents ) {
    TreeItem pathDown = null;

    for ( int i = 0; i < parents.size(); i++ ) {
      TreeItem parent = parents.get( i );

      int itemCount = pathDown != null ? pathDown.getChildCount() : getItemCount();
      for ( int j = 0; j < itemCount; j++ ) {
        TreeItem possibleItem = pathDown != null ? pathDown.getChild( j ) : getItem( j );

        if ( ( possibleItem instanceof FolderTreeItem ) && ( parent instanceof FolderTreeItem )
          && ( (FolderTreeItem) parent ).getFileName().equals( ( (FolderTreeItem) possibleItem ).getFileName() ) ) {
          pathDown = possibleItem;
          pathDown.setState( true, true );
          break;
        }
      }
    }

    if ( pathDown != null ) {
      setSelectedItem( pathDown );
      pathDown.setState( true, true );
    }
  }

  private void handleItemSelection( SelectionEvent<TreeItem> event ) {
    if ( selectedItem != null ) {
      Widget treeItemWidget = selectedItem.getWidget();
      if ( treeItemWidget instanceof LeafItemWidget ) {
        treeItemWidget.getParent().removeStyleName( SELECTED_STYLE_NAME );
      } else {
        selectedItem.removeStyleName( SELECTED_STYLE_NAME );
      }
    }
    selectedItem = event.getSelectedItem();
    if ( selectedItem != null ) {
      Widget treeItemWidget = selectedItem.getWidget();
      if ( selectedItem instanceof FolderTreeItem ) {
        GenericFile fileModel = ( (FolderTreeItem) selectedItem ).getFileModel();
        if ( fileModel != null && fileModel.isHidden() && !isShowHiddenFiles() ) {
          if ( treeItemWidget instanceof LeafItemWidget ) {
            treeItemWidget.getParent().removeStyleName( HIDDEN_STYLE_NAME );
            treeItemWidget.getParent().addStyleName( SELECTED_STYLE_NAME );
          } else {
            selectedItem.addStyleName( HIDDEN_STYLE_NAME );
            selectedItem.addStyleName( SELECTED_STYLE_NAME );
          }
        } else {
          if ( treeItemWidget instanceof LeafItemWidget ) {
            treeItemWidget.getParent().addStyleName( SELECTED_STYLE_NAME );
          } else {
            selectedItem.addStyleName( SELECTED_STYLE_NAME );
          }
        }
      } else {
        if ( treeItemWidget instanceof LeafItemWidget ) {
          treeItemWidget.getParent().addStyleName( SELECTED_STYLE_NAME );
        } else {
          selectedItem.addStyleName( SELECTED_STYLE_NAME );
        }
      }
    }
  }

  private void handleOpen( OpenEvent<TreeItem> event ) {
    TreeItem target = event.getTarget();

    // By default, expanding a node does not select it. Add that in here
    this.setSelectedItem( target );

    target.addStyleName( OPEN_STYLE_NAME );
  }

  private void handleClose( CloseEvent<TreeItem> event ) {
    event.getTarget().removeStyleName( OPEN_STYLE_NAME );
  }

  private void buildSolutionTree( FolderTreeItem treeItem, GenericFileTree treeModel ) {
    List<GenericFileTree> childTreeModels = treeModel.getChildren();

    // BISERVER-9599 - Custom Sort
    Collections.sort( childTreeModels, new GenericFileTreeComparator( showLocalizedFileNames ) );

    for ( GenericFileTree childTreeModel : childTreeModels ) {
      GenericFile childFileModel = childTreeModel.getFile();
      boolean isDirectory = childFileModel.isFolder();
      String fileName = childFileModel.getName();
      if ( ( !childFileModel.isHidden() || isShowHiddenFiles() ) && !StringUtils.isEmpty( fileName ) ) {

        String title = childFileModel.getTitleOrName();
        String description = childFileModel.getDescription();
        FolderTreeItem childTreeItem = new FolderTreeItem();
        childTreeItem.setStylePrimaryName( "leaf-widget" );
        childTreeItem.getElement().setAttribute( "id", childFileModel.getPath() ); //$NON-NLS-1$
        childTreeItem.setUserObject( childTreeModel );
        childTreeItem.setFileModel( childFileModel );
        if ( childFileModel.isHidden() && childFileModel.isFolder() ) {
          childTreeItem.addStyleDependentName( HIDDEN_STYLE_NAME );
        }

        if ( childTreeModel != null && childTreeModel.getChildren() != null ) {
          for ( GenericFileTree childItem : childTreeModel.getChildren() ) {
            if ( childItem.getFile().isFolder() ) {
              childTreeItem.addStyleName( "parent-widget" );
              break;
            }
          }
        }

        ElementUtils.killAllTextSelection( childTreeItem.getElement() );
        childTreeItem.setURL( fileName );
        if ( showLocalizedFileNames ) {
          childTreeItem.setText( title );
          if ( isUseDescriptionsForTooltip() && !StringUtils.isEmpty( description ) ) {
            childTreeItem.setTitle( description );
          } else {
            childTreeItem.setTitle( fileName );
          }
        } else {
          childTreeItem.setText( fileName );
          if ( isUseDescriptionsForTooltip() && !StringUtils.isEmpty( description ) ) {
            childTreeItem.setTitle( description );
          } else {
            childTreeItem.setTitle( title );
          }
        }
        childTreeItem.setFileName( fileName );
        if ( treeItem == null && isDirectory ) {
          addItem( childTreeItem );
        } else {
          treeItem.addItem( childTreeItem );
        }
        FolderTreeItem tmpParent = childTreeItem;
        String pathToChild = tmpParent.getFileName();
        while ( tmpParent.getParentItem() != null ) {
          tmpParent = (FolderTreeItem) tmpParent.getParentItem();
          pathToChild = tmpParent.getFileName() + "/" + pathToChild; //$NON-NLS-1$
        }
        /*
         * TODO Not sure what to do here if (treeItem != null) { ArrayList<FileChooserRepositoryFile> files =
         * (ArrayList<FileChooserRepositoryFile>) treeItem.getUserObject(); if (files == null) { files = new
         * ArrayList<FileChooserRepositoryFile>(); treeItem.setUserObject(files); } files.add(file); }
         */
        if ( isDirectory ) {
          buildSolutionTree( childTreeItem, childTreeModel );
        } else {
          if ( treeItem != null ) {
            treeItem.removeItem( childTreeItem );
          }
        }
      }
    }
  }

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
    onModelFetched( fileTreeModel );
  }

  public boolean isCreateRootNode() {
    return createRootNode;
  }

  public List<GenericFile> getFiles() {
    final FolderTreeItem selectedTreeItem = (FolderTreeItem) getSelectedItem();
    List<GenericFile> values = new ArrayList<>();
    values.add( ( (GenericFileTree) selectedTreeItem.getUserObject() ).getFile() );
    return values;
  }

  /**
   * Retrieves the default home folder.
   * @return The home folder
   */
  static String getHomeFolder() {
    return ( homeFolder != null ) ? homeFolder : refreshHomeFolder();
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
            homeFolder = response.getText().replaceAll( "/workspace", "" );
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
}
