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
 * Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.mantle.client.dialogs.folderchooser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.pentaho.gwt.widgets.client.filechooser.JsonToRepositoryFileTreeConverter;
import org.pentaho.gwt.widgets.client.filechooser.RepositoryFile;
import org.pentaho.gwt.widgets.client.filechooser.RepositoryFileTree;
import org.pentaho.gwt.widgets.client.filechooser.TreeItemComparator;
import org.pentaho.gwt.widgets.client.utils.ElementUtils;
import org.pentaho.gwt.widgets.client.utils.string.StringTokenizer;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;
import org.pentaho.mantle.client.dialogs.WaitPopup;
import org.pentaho.mantle.client.dialogs.scheduling.ScheduleHelper;
import org.pentaho.mantle.client.messages.Messages;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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

public class FolderTree extends Tree /*implements IRepositoryFileTreeListener, UserSettingsLoadedEventHandler,
    IRepositoryFileProvider*/ {
  
  private static final String SELECTED_STYLE_NAME = "selected";
  
  private static final String HIDDEN_STYLE_NAME = "hidden";
  
  private boolean showLocalizedFileNames = true;
  private boolean showHiddenFiles = false;
  private boolean createRootNode = false;
  private boolean useDescriptionsForTooltip = false;
  public RepositoryFileTree repositoryFileTree;
  public List<RepositoryFile> trashItems;
  public FolderTreeItem trashItem;

  private TreeItem selectedItem = null;
  private String selectedPath = null;

  private FocusPanel focusable = new FocusPanel();

  public FolderTree( boolean showTrash ) {
    super();
    setAnimationEnabled( true );
    sinkEvents( Event.ONDBLCLICK );
    DOM.setElementAttribute( getElement(), "oncontextmenu", "return false;" ); //$NON-NLS-1$ //$NON-NLS-2$
    DOM.setStyleAttribute( focusable.getElement(), "fontSize", "0" ); //$NON-NLS-1$ //$NON-NLS-2$
    DOM.setStyleAttribute( focusable.getElement(), "position", "absolute" ); //$NON-NLS-1$ //$NON-NLS-2$
    DOM.setStyleAttribute( focusable.getElement(), "outline", "0px" ); //$NON-NLS-1$ //$NON-NLS-2$
    DOM.setStyleAttribute( focusable.getElement(), "width", "1px" ); //$NON-NLS-1$ //$NON-NLS-2$
    DOM.setStyleAttribute( focusable.getElement(), "height", "1px" ); //$NON-NLS-1$ //$NON-NLS-2$
    DOM.setElementAttribute( focusable.getElement(), "hideFocus", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
    DOM.setIntStyleAttribute( focusable.getElement(), "zIndex", -1 ); //$NON-NLS-1$
    DOM.appendChild( getElement(), focusable.getElement() );
    DOM.sinkEvents( focusable.getElement(), Event.FOCUSEVENTS );

    this.addSelectionHandler( new SelectionHandler<TreeItem>() {

      @Override
      public void onSelection( SelectionEvent<TreeItem> event ) {
        if ( selectedItem != null ) {
          Widget treeItemWidget = selectedItem.getWidget();
          if ( treeItemWidget != null && treeItemWidget instanceof LeafItemWidget ) {
            ( (LeafItemWidget) treeItemWidget ).getParent().removeStyleName( SELECTED_STYLE_NAME );
          } else {
            selectedItem.removeStyleName( SELECTED_STYLE_NAME );
          }
        }
        selectedItem = event.getSelectedItem();
        if ( selectedItem != null ) {
          Widget treeItemWidget = selectedItem.getWidget();
          if ( selectedItem instanceof FolderTreeItem ) {
            RepositoryFile repositoryFile = ( (FolderTreeItem) selectedItem ).getRepositoryFile();
            if ( repositoryFile != null && repositoryFile.isHidden() && !isShowHiddenFiles() ) {
              if ( treeItemWidget != null && treeItemWidget instanceof LeafItemWidget ) {
                ( (LeafItemWidget) treeItemWidget ).getParent().removeStyleName( HIDDEN_STYLE_NAME );
                ( (LeafItemWidget) treeItemWidget ).getParent().addStyleName( SELECTED_STYLE_NAME );
              } else {
                selectedItem.addStyleName( HIDDEN_STYLE_NAME );
                selectedItem.addStyleName( SELECTED_STYLE_NAME );                
              }
            } else {
              if ( treeItemWidget != null && treeItemWidget instanceof LeafItemWidget ) {
                ( (LeafItemWidget) treeItemWidget ).getParent().addStyleName( SELECTED_STYLE_NAME );
              } else {
                selectedItem.addStyleName( SELECTED_STYLE_NAME );
              }
            }
          } else {
            if ( treeItemWidget != null && treeItemWidget instanceof LeafItemWidget ) {
              ( (LeafItemWidget) treeItemWidget ).getParent().addStyleName( SELECTED_STYLE_NAME );
            } else {
              selectedItem.addStyleName( SELECTED_STYLE_NAME );
            }
          }
        }
      }
    } );
    // By default, expanding a node does not select it. Add that in here
    this.addOpenHandler( new OpenHandler<TreeItem>() {
      public void onOpen( OpenEvent<TreeItem> event ) {
        FolderTree.this.setSelectedItem( event.getTarget() );
        selectedItem.addStyleName( "open" );
      }
    } );

    this.addCloseHandler( new CloseHandler<TreeItem>() {
      @Override
      public void onClose( CloseEvent<TreeItem> event ) {
        event.getTarget().removeStyleName( "open" );
      }
    } );

    getElement().setId( "solutionTree" ); //$NON-NLS-1$
    getElement().getStyle().setProperty( "margin", "29px 0px 10px 0px" ); //$NON-NLS-1$ //$NON-NLS-2$
    
    beforeFetchRepositoryFileTree();

    fetchRepositoryFileTree( null, null, null, showHiddenFiles );
    
   // onFetchRepositoryFileTree( repositoryFileTree, Collections.<RepositoryFile>emptyList() );
    //RepositoryFileTreeManager.getInstance().addRepositoryFileTreeListener( this, null, null, showHiddenFiles );
    /*EventBusUtil.EVENT_BUS.addHandler( UserSettingsLoadedEvent.TYPE, this );
    UserSettingsManager.getInstance().getUserSettings( new AsyncCallback<JsArray<JsSetting>>() {

      public void onSuccess( JsArray<JsSetting> settings ) {
        onUserSettingsLoaded( new UserSettingsLoadedEvent( settings ) );
      }

      public void onFailure( Throwable caught ) {
      }
    }, false );*/
  }
  
  public void fetchRepositoryFileTree( final AsyncCallback<RepositoryFileTree> callback, Integer depth, String filter,
      Boolean showHidden ) {
    // notify listeners that we are about to talk to the server (in case there's anything they want to do
    // such as busy cursor or tree loading indicators)
    beforeFetchRepositoryFileTree();
    RequestBuilder builder = null;
    String url = ScheduleHelper.getFullyQualifiedURL() + "api/repo/files/:/tree?"; //$NON-NLS-1$
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
            + "depth=" + depth + "&filter=" + filter + "&showHidden=" + showHidden + "&ts=" + System.currentTimeMillis(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    builder = new RequestBuilder( RequestBuilder.GET, url );
    builder.setHeader( "Accept", "application/json" );
    builder.setHeader( "If-Modified-Since", "01 Jan 1970 00:00:00 GMT" );

    RequestCallback innerCallback = new RequestCallback() {

      public void onError( Request request, Throwable exception ) {
        Window.alert( exception.toString() );
      }

      public void onResponseReceived( Request request, Response response ) {
        if ( response.getStatusCode() == Response.SC_OK ) {
          String json = response.getText();
          System.out.println( json );

          final JsonToRepositoryFileTreeConverter converter =
              new JsonToRepositoryFileTreeConverter( response.getText() );
          final RepositoryFileTree fileTree = converter.getTree();

          String deletedFilesUrl = ScheduleHelper.getFullyQualifiedURL() + "api/repo/files/deleted?ts=" + System.currentTimeMillis();
          RequestBuilder deletedFilesRequestBuilder = new RequestBuilder( RequestBuilder.GET, deletedFilesUrl );
          deletedFilesRequestBuilder.setHeader( "Accept", "application/json" );
          deletedFilesRequestBuilder.setHeader( "If-Modified-Since", "01 Jan 1970 00:00:00 GMT" );
          try {
            deletedFilesRequestBuilder.sendRequest( null, new RequestCallback() {

              public void onError( Request request, Throwable exception ) {
                onFetchRepositoryFileTree(fileTree, Collections.<RepositoryFile>emptyList() );
                Window.alert( exception.toString() );
              }

              public void onResponseReceived( Request delRequest, Response delResponse ) {
                if ( delResponse.getStatusCode() == Response.SC_OK ) {
                  try {
                    trashItems = JsonToRepositoryFileTreeConverter.getTrashFiles( delResponse.getText() );
                  } catch ( Throwable t ) {
                    // apparently this happens when you have no trash
                  }
                  onFetchRepositoryFileTree(fileTree, Collections.<RepositoryFile>emptyList() );
                } else {
                  onFetchRepositoryFileTree(fileTree, Collections.<RepositoryFile>emptyList() );
                }
                if ( callback != null ) {
                  callback.onSuccess( fileTree );
                } 
              }

            } );
          } catch ( Exception e ) {
            onFetchRepositoryFileTree(fileTree, Collections.<RepositoryFile>emptyList() );
          }
        } else {
          /*fileTree = new RepositoryFileTree();
          RepositoryFile errorFile = new RepositoryFile();
          errorFile.setFolder( true );
          errorFile.setName( "!ERROR!" );
          repositoryFileTree.setFile( errorFile );*/
        }
      }

    };
    try {
      builder.sendRequest( null, innerCallback );
    } catch ( RequestException e ) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  
  
/*
  @Override
  public void onUserSettingsLoaded( UserSettingsLoadedEvent event ) {
    JsArray<JsSetting> settings = event.getSettings();
    if ( settings != null ) {
      for ( int i = 0; i < settings.length(); i++ ) {
        JsSetting setting = settings.get( i );
        if ( IMantleUserSettingsConstants.MANTLE_SHOW_LOCALIZED_FILENAMES.equals( setting.getName() ) ) {
          boolean showLocalizedFileNames = "true".equals( setting.getName() ); //$NON-NLS-1$
          setShowLocalizedFileNames( showLocalizedFileNames );
        } else if ( IMantleUserSettingsConstants.MANTLE_SHOW_DESCRIPTIONS_FOR_TOOLTIPS.equals( setting.getName() ) ) {
          boolean useDescriptions = "true".equals( setting.getValue() ); //$NON-NLS-1$
          setUseDescriptionsForTooltip( useDescriptions );
        } else if ( IMantleUserSettingsConstants.MANTLE_SHOW_HIDDEN_FILES.equals( setting.getName() ) ) {
          boolean showHiddenFiles = "true".equals( setting.getValue() ); //$NON-NLS-1$
          setShowHiddenFiles( showHiddenFiles );
        }
      }
    }
    RepositoryFileTreeManager.getInstance().addRepositoryFileTreeListener( this, null, null, showHiddenFiles );
  }
*/
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
    if ( trashItem != null ) {
      try {
        DOM.setStyleAttribute( trashItem.getElement(), "paddingLeft", "0px" ); //$NON-NLS-1$//$NON-NLS-2$
      } catch ( NullPointerException e ) {
        // This is sometimes thrown because the dom does not yet contain the trash items or the leaf nodes.
      }
    }
  }

  public void beforeFetchRepositoryFileTree() {
    WaitPopup.getInstance().setVisible( true );
    if ( getSelectedItem() != null ) {
      selectedItem = getSelectedItem();
    }
    clear();
    addItem( new FolderTreeItem( Messages.getString( "loadingEllipsis" ) ) ); //$NON-NLS-1$
    WaitPopup.getInstance().setVisible( false );
  }

  public void onFetchRepositoryFileTree( RepositoryFileTree fileTree, List<RepositoryFile> repositoryTrashItems ) {

    if ( fileTree == null ) {
      WaitPopup.getInstance().setVisible( false );
      return;
    }
    repositoryFileTree = fileTree;
    trashItems = repositoryTrashItems;
    // remember selectedItem, so we can reselect it after the tree is loaded
    clear();
    // get document root item
    RepositoryFile rootRepositoryFile = repositoryFileTree.getFile();
    if ( !rootRepositoryFile.isHidden() || isShowHiddenFiles() ) {
      FolderTreeItem rootItem = null;
      if ( createRootNode ) {
        rootItem = new FolderTreeItem();
        rootItem.setText( rootRepositoryFile.getPath() );
        rootItem.setTitle( rootRepositoryFile.getPath() );
        rootItem.getElement().setId( rootRepositoryFile.getPath() );
        // added so we can traverse the true names
        rootItem.setFileName( "/" ); //$NON-NLS-1$
        rootItem.setRepositoryFile( rootRepositoryFile );
        addItem( rootItem );
        buildSolutionTree( rootItem, repositoryFileTree );
      } else {
        buildSolutionTree( null, repositoryFileTree );
        // sort the root elements
        ArrayList<TreeItem> roots = new ArrayList<TreeItem>();
        for ( int i = 0; i < getItemCount(); i++ ) {
          roots.add( getItem( i ) );
        }
        Collections.sort( roots, new TreeItemComparator() ); // BISERVER-9599 - Custom Sort
        clear();
        for ( TreeItem myRootItem : roots ) {
          addItem( myRootItem );
        }
      }
    }
    fixLeafNodes();

    if ( selectedPath != null ) {
      select( selectedPath );
    } else if ( selectedItem != null ) {
      ArrayList<TreeItem> parents = new ArrayList<TreeItem>();
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
      RepositoryFileTree userObject = (RepositoryFileTree) treeItem.getUserObject();
      if ( userObject != null && userObject.getChildren().size() == 0 ) { // This is a leaf node so change the
                                                                          // widget
        treeItem
            .setWidget( new LeafItemWidget( treeItem.getText(), "icon-tree-node", "icon-tree-leaf", "icon-folder" ) ); //$NON-NLS-1$
      } else {
        treeItem.setWidget( new LeafItemWidget( treeItem.getText(), "icon-tree-node", "icon-folder" ) ); //$NON-NLS-1$
      }

      DOM.setStyleAttribute( treeItem.getElement(), "paddingLeft", "0px" ); //$NON-NLS-1$ //$NON-NLS-2$
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

  public FolderTreeItem getTrashItem() {
    return trashItem;
  }

  public List<RepositoryFile> getTrashItems() {
    return trashItems;
  }

  public void select( String path ) {
    this.selectedPath = path;
    List<String> pathSegments = new ArrayList<String>();
    if ( path != null ) {
      if ( path.startsWith( "/" ) ) { //$NON-NLS-1$
        path = path.substring( 1 );
      }
      StringTokenizer st = new StringTokenizer( path, '/' );
      for ( int i = 0; i < st.countTokens(); i++ ) {
        String token = st.tokenAt( i );
        pathSegments.add( token );
      }
    }
    TreeItem item = getTreeItem( pathSegments );
    selectedItem = item;
    ArrayList<TreeItem> parents = new ArrayList<TreeItem>();
    if ( item != null ) {
      this.setSelectedItem( item, false );
      parents.add( item );
      item = item.getParentItem();
      while ( item != null ) {
        parents.add( item );
        item = item.getParentItem();
      }
      Collections.reverse( parents );
      selectFromList( parents );
    }
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
      if ( pathDown == null ) {
        for ( int j = 0; j < getItemCount(); j++ ) {
          TreeItem possibleItem = getItem( j );
          if ( ( possibleItem instanceof FolderTreeItem ) && ( parent instanceof FolderTreeItem )
              && ( (FolderTreeItem) parent ).getFileName().equals( ( (FolderTreeItem) possibleItem ).getFileName() ) ) {
            pathDown = possibleItem;
            pathDown.setState( true, true );
            pathDown.setSelected( true );
            break;
          }
        }
      } else {
        for ( int j = 0; j < pathDown.getChildCount(); j++ ) {
          TreeItem possibleItem = pathDown.getChild( j );
          if ( ( possibleItem instanceof FolderTreeItem ) && ( parent instanceof FolderTreeItem )
              && ( (FolderTreeItem) parent ).getFileName().equals( ( (FolderTreeItem) possibleItem ).getFileName() ) ) {
            pathDown = possibleItem;
            pathDown.setState( true, true );
            break;
          }
        }
      }
    }
    if ( pathDown != null ) {
      setSelectedItem( pathDown );
      pathDown.setState( true, true );
    }
  }

  private void buildSolutionTree( FolderTreeItem parentTreeItem, RepositoryFileTree repositoryFileTree ) {
    List<RepositoryFileTree> children = repositoryFileTree.getChildren();

    // BISERVER-9599 - Custom Sort
    Collections.sort( children, new Comparator<RepositoryFileTree>() {
      @Override
      public int compare( RepositoryFileTree repositoryFileTree, RepositoryFileTree repositoryFileTree2 ) {
        return ( new TreeItemComparator() ).compare( repositoryFileTree.getFile().getTitle(), repositoryFileTree2
            .getFile().getTitle() );
      }
    } );

    for ( RepositoryFileTree treeItem : children ) {
      RepositoryFile file = treeItem.getFile();
      boolean isDirectory = file.isFolder();
      String fileName = file.getName();
      if ( ( !file.isHidden() || isShowHiddenFiles() ) && !StringUtils.isEmpty( fileName ) ) {

        // TODO Mapping Title to LocalizedName
        String localizedName = file.getTitle();
        String description = file.getDescription();
        FolderTreeItem childTreeItem = new FolderTreeItem();
        childTreeItem.setStylePrimaryName( "leaf-widget" );
        childTreeItem.getElement().setAttribute( "id", file.getPath() ); //$NON-NLS-1$
        childTreeItem.setUserObject( treeItem );
        childTreeItem.setRepositoryFile( file );
        if ( file.isHidden() && file.isFolder() ) {
          childTreeItem.addStyleDependentName( HIDDEN_STYLE_NAME );
        }

        if ( treeItem != null && treeItem.getChildren() != null ) {
          for ( RepositoryFileTree childItem : treeItem.getChildren() ) {
            if ( childItem.getFile().isFolder() ) {
              childTreeItem.addStyleName( "parent-widget" );
              break;
            }
          }
        }

        ElementUtils.killAllTextSelection( childTreeItem.getElement() );
        childTreeItem.setURL( fileName );
        if ( showLocalizedFileNames ) {
          childTreeItem.setText( localizedName );
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
            childTreeItem.setTitle( localizedName );
          }
        }
        childTreeItem.setFileName( fileName );
        if ( parentTreeItem == null && isDirectory ) {
          addItem( childTreeItem );
        } else {
          parentTreeItem.addItem( childTreeItem );
        }
        FolderTreeItem tmpParent = childTreeItem;
        String pathToChild = tmpParent.getFileName();
        while ( tmpParent.getParentItem() != null ) {
          tmpParent = (FolderTreeItem) tmpParent.getParentItem();
          pathToChild = tmpParent.getFileName() + "/" + pathToChild; //$NON-NLS-1$
        }
        /*
         * TODO Not sure what to do here if (parentTreeItem != null) { ArrayList<FileChooserRepositoryFile> files =
         * (ArrayList<FileChooserRepositoryFile>) parentTreeItem.getUserObject(); if (files == null) { files = new
         * ArrayList<FileChooserRepositoryFile>(); parentTreeItem.setUserObject(files); } files.add(file); }
         */
        if ( isDirectory ) {
          buildSolutionTree( childTreeItem, treeItem );
        } else {
          if ( parentTreeItem != null ) {
            parentTreeItem.removeItem( childTreeItem );
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
    onFetchRepositoryFileTree( repositoryFileTree, trashItems );
  }

  public boolean isCreateRootNode() {
    return createRootNode;
  }

  public List<RepositoryFile> getRepositoryFiles() {
    final FolderTreeItem selectedTreeItem = (FolderTreeItem) getSelectedItem();
    List<RepositoryFile> values = new ArrayList<RepositoryFile>();
    values.add( ( (RepositoryFileTree) selectedTreeItem.getUserObject() ).getFile() );
    return values;
  }

}
