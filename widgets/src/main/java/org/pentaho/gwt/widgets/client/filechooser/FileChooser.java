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
 * Copyright (c) 2002 - 2020 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.gwt.widgets.client.filechooser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.pentaho.gwt.widgets.client.dialogs.IDialogCallback;
import org.pentaho.gwt.widgets.client.utils.ElementUtils;
import org.pentaho.gwt.widgets.client.utils.string.CssUtils;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
@SuppressWarnings( "deprecation" )
public class FileChooser extends VerticalPanel {

  public enum FileChooserMode {
    OPEN, OPEN_READ_ONLY, SAVE
  }

  public static final String ETC_FOLDER = "etc";
  FileChooserMode mode = FileChooserMode.OPEN;
  String selectedPath;
  private boolean isLazy = false;
  private LazyTreeLoader lazyTreeLoader = new LazyTreeLoader();
  private FileChooserTreeListener treeListener;

  ListBox navigationListBox;
  Tree repositoryTree;
  TreeItem selectedTreeItem;
  boolean showHiddenFiles = false;
  boolean showLocalizedFileNames = true;
  com.google.gwt.user.client.Element lastSelectedFileElement;
  TextBox fileNameTextBox = new TextBox();
  DateTimeFormat dateFormat = DateTimeFormat.getMediumDateTimeFormat();
  RepositoryFileTree fileTree;

  ArrayList<FileChooserListener> listeners = new ArrayList<FileChooserListener>();
  private String actualFileName;
  boolean fileSelected = false;

  private FileFilter fileFilter;

  private boolean submitOnEnter = true;

  public FileChooser() {
    super();

    fileNameTextBox.getElement().setId( "fileNameTextBox" );

    // workaround webkit browsers quirk of not being able to set focus in a widget by clicking on it
    fileNameTextBox.addClickHandler( new ClickHandler() {
      public void onClick( ClickEvent event ) {
        fileNameTextBox.setFocus( true );
      }
    } );

    fileNameTextBox.addKeyUpHandler( new KeyUpHandler() {
      public void onKeyUp( KeyUpEvent event ) {
        actualFileName = fileNameTextBox.getText();
        if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER && isSubmitOnEnter() ) {
          if ( mode != FileChooserMode.SAVE ) {
            fireFileSelected( search( fileTree, actualFileName ) );
          } else {
            fireFileSelected();
          }
        }
      }
    } );

    setSpacing( 3 );
  }

  public FileChooser( boolean showHiddenFiles ) {
    this();

    this.showHiddenFiles = showHiddenFiles;
  }

  private RepositoryFile search( RepositoryFileTree tree, String actualFileName ) {
    try {
      RepositoryFile file = tree.getFile();
      if ( file != null && !file.isFolder() && file.getPath().equals( actualFileName ) ) {
        return file;
      }

      if ( file != null ) {
        for ( RepositoryFileTree treeItem : tree.getChildren() ) {
          file = search( treeItem, actualFileName );
          if ( file != null ) {
            return file;
          }
        }
      }
    } catch ( Exception e ) {
      // does nothing
    }

    return null;
  }

  public FileChooser( FileChooserMode mode, String selectedPath, boolean showLocalizedFileNames ) {
    this( mode, selectedPath, showLocalizedFileNames, null );
  }

  public FileChooser( FileChooserMode mode, String selectedPath, boolean showLocalizedFileNames,
                      RepositoryFileTree fileTree ) {
    this();

    this.mode = mode;
    this.selectedPath = selectedPath;
    this.fileTree = fileTree;
    this.showLocalizedFileNames = showLocalizedFileNames;

    if ( null != fileTree ) {
      repositoryTree = TreeBuilder.buildSolutionTree( fileTree, showHiddenFiles, showLocalizedFileNames, fileFilter );
      selectedTreeItem = repositoryTree.getItem( 0 );
      initUI();
    }
  }

  public FileChooser( FileChooserMode mode, String selectedPath, IDialogCallback callback ) {
    this();

    this.mode = mode;
    this.selectedPath = selectedPath;

    try {
      fetchRepository( callback );
    } catch ( RequestException e ) {
      Window.alert( e.toString() );
    }
  }

  private native String getFullyQualifiedURL()
    /*-{
      return $wnd.location.protocol + "//" + $wnd.location.host + $wnd.CONTEXT_PATH
    }-*/;

  /**
   * Load a directory by path. If it's cached, use the cache, if not load it from the server
   *
   * @param path
   */
  private void loadDirectory( String path ) {
    if ( lazyTreeLoader.isCached( path ) ) {
      RepositoryFileTree loadedTree = lazyTreeLoader.getCached( path );
      buildTree( loadedTree, false );
    } else {
      try {
        fetchRepositoryDirectory( path, 1, null );
      } catch ( RequestException e ) {
        Window.alert( e.toString() );
      }
    }
  }

  /**
   * Take the loaded RepositoryFileTree and insert it into the file structure RepositoryFileTree
   *
   * @param loadedTree
   * @param cache
   */
  private void buildTree( RepositoryFileTree loadedTree, boolean cache ) {
    final String loadedFilePath = loadedTree.getFile().getPath();
    if ( cache ) {
      lazyTreeLoader.cache( loadedFilePath, loadedTree );
    }

    if ( fileTree == null || loadedFilePath.equals( "/" ) ) {
      fileTree = loadedTree;
    } else {
      lazyTreeLoader.insertTree( fileTree, loadedTree );
    }

    repositoryTree = TreeBuilder.buildSolutionTree( fileTree, showHiddenFiles, showLocalizedFileNames, fileFilter );
    selectedTreeItem = repositoryTree.getItem( 0 );

    initUI();
    fireFileSelectionChanged();

    if ( treeListener != null ) {
      treeListener.loaded();
    }
  }

  /**
   * Load a directory from the server
   *
   * @param folder
   * @param depth
   * @param filter
   * @throws RequestException
   */
  public void fetchRepositoryDirectory( String folder, int depth, String filter ) throws RequestException {
    final String url = getRepositoryRequestUrl( folder, depth, filter ) + "&ts=" + System.currentTimeMillis();

    final RequestBuilder builder = new RequestBuilder( RequestBuilder.GET, url );
    builder.setHeader( "accept", "application/json" );

    RequestCallback callback = new RequestCallback() {

      public void onError( Request request, Throwable exception ) {
        Window.alert( exception.toString() );
      }

      public void onResponseReceived( Request request, Response response ) {
        if ( response.getStatusCode() == Response.SC_OK ) {
          String jsonData = response.getText();
          if ( fileTree == null ) {
            jsonData = lazyTreeLoader.buildTree( jsonData );
          }

          JsonToRepositoryFileTreeConverter converter = new JsonToRepositoryFileTreeConverter( jsonData );
          buildTree( converter.getTree(), fileTree != null );
        } else {
          Window.alert( "Unable to find or access contents within the selected folder" );
        }
      }
    };

    builder.sendRequest( null, callback );
  }

  public void fetchRepository( final IDialogCallback completedCallback ) throws RequestException {
    final String url = getRepositoryRequestUrl( ":", -1, "*" );

    RequestBuilder builder = new RequestBuilder( RequestBuilder.GET, url );
    builder.setHeader( "accept", "application/json" );

    RequestCallback callback = new RequestCallback() {

      public void onError( Request request, Throwable exception ) {
        Window.alert( exception.toString() );
      }

      public void onResponseReceived( Request request, Response response ) {
        if ( response.getStatusCode() == Response.SC_OK ) {
          String jsonData = response.getText();

          JsonToRepositoryFileTreeConverter converter = new JsonToRepositoryFileTreeConverter( jsonData );
          repositoryTree = TreeBuilder.buildSolutionTree(
            converter.getTree(), showHiddenFiles, showLocalizedFileNames, fileFilter );

          selectedTreeItem = repositoryTree.getItem( 0 );

          initUI();

          if ( completedCallback != null ) {
            completedCallback.okPressed();
          }
        } else {
          Window.alert( "Unable to find or access contents within the selected folder" );
        }
      }

    };

    builder.sendRequest( null, callback );
  }

  private String getRepositoryRequestUrl( String folder, int depth, String filter ) {
    final String folderId = folder == null ? ":" : folder.replace( "/", ":" );

    if ( filter == null ) {
      filter = "*";
    }

    return getFullyQualifiedURL() + "api/repo/files/" + folderId + "/tree?"
        + "showHidden=" + showHiddenFiles + "&depth=" + depth + "&filter=" + filter;
  }

  public void initUI() {

    if ( mode == FileChooserMode.OPEN_READ_ONLY ) {
      fileNameTextBox.setReadOnly( true );
    }

    // We are here because we are initiating a fresh UI for a new directory
    // Since there is no file selected currently, we are setting file selected to false.
    setFileSelected( false );

    String path = this.selectedPath;
    selectedTreeItem = repositoryTree.getItem( 0 );

    // find the selected item from the list
    List<String> actualPathSegments = new ArrayList<String>();
    // Getting a list of the Localized Path Segments as well, need to get the true Tree Items from the actual
    // Path, but we need to display to the user the Localized Path instead.
    List<String> localizedPathSegments = new ArrayList<String>();
    if ( path != null ) {
      int index = path.indexOf( "/", 0 );
      int oldIndex;
      String segment;
      TreeItem childItem;
      while ( index >= 0 ) {
        oldIndex = index;
        index = path.indexOf( "/", oldIndex + 1 );
        if ( index >= 0 ) {
          segment = path.substring( oldIndex + 1, index );
        } else {
          segment = path.substring( oldIndex + 1 );
        }
        childItem = getChildTreeItem( segment, selectedTreeItem );
        if ( childItem != null ) {
          actualPathSegments.add( segment );
          localizedPathSegments.add( childItem.getTitle() );
          selectedTreeItem = childItem;
        } else {
          break;
        }
      }
    }

    navigationListBox = new ListBox();
    navigationListBox.getElement().setId( "navigationListBox" );
    navigationListBox.setWidth( "350px" );
    // now we can find the tree nodes who match the path segments
    navigationListBox.addItem( "/", "/" );
    // Actual and Localized Path segments will be the same size based on how items get added to their lists.
    for ( int i = 0; i < actualPathSegments.size(); i++ ) {
      String actualFullPath = "";
      String localizedFullPath = "";
      for ( int j = 0; j <= i; j++ ) {
        String actualSegmentPath = actualPathSegments.get( j );
        String localizedSegmentPath = localizedPathSegments.get( j );
        if ( actualSegmentPath != null && localizedSegmentPath != null
          && actualSegmentPath.length() > 0 && localizedSegmentPath.length() > 0 ) {
          actualFullPath += "/" + actualSegmentPath;
          localizedFullPath += "/" + localizedSegmentPath;
        }
      }
      if ( !actualFullPath.equals( "/" ) && !localizedFullPath.equals( "/" ) ) {
        navigationListBox.addItem( localizedFullPath, actualFullPath );
      }
    }

    navigationListBox.setSelectedIndex( navigationListBox.getItemCount() - 1 );
    navigationListBox.addChangeListener( new ChangeListener() {
      public void onChange( Widget sender ) {
        changeToPath( navigationListBox.getValue( navigationListBox.getSelectedIndex() ) );
      }
    } );

    clear();

    VerticalPanel locationBar = new VerticalPanel();
    locationBar.add( new Label( FileChooserEntryPoint.messages.getString( "location" ) ) );

    HorizontalPanel navigationBar = new HorizontalPanel();

    final Image upDirImage = new Image();
    upDirImage.setUrl( GWT.getModuleBaseURL() + "images/spacer.gif" );
    upDirImage.addStyleName( "pentaho-filechooseupbutton" );
    upDirImage.setTitle( FileChooserEntryPoint.messages.getString( "upOneLevel" ) );

    upDirImage.addMouseListener( new MouseListener() {

      public void onMouseDown( Widget sender, int x, int y ) {
      }

      public void onMouseEnter( Widget sender ) {
      }

      public void onMouseLeave( Widget sender ) {
      }

      public void onMouseMove( Widget sender, int x, int y ) {
      }

      public void onMouseUp( Widget sender, int x, int y ) {
      }

    } );

    upDirImage.addClickListener( new ClickListener() {
      public void onClick( Widget sender ) {
        // go up a dir
        TreeItem tmpItem = selectedTreeItem;
        List<String> parentSegments = new ArrayList<String>();
        while ( tmpItem != null ) {
          RepositoryFileTree tree = (RepositoryFileTree) tmpItem.getUserObject();
          if ( tree.getFile() != null && tree.getFile().getName() != null ) {
            parentSegments.add( tree.getFile().getName() );
          }
          tmpItem = tmpItem.getParentItem();
        }

        Collections.reverse( parentSegments );
        String myPath = "";
        // If we have a file selected then we need to go one lesser level deep
        final int loopCount = isFileSelected() ? parentSegments.size() - 2 : parentSegments.size() - 1;
        for ( int i = 0; i < loopCount; i++ ) {
          String pathSegment = parentSegments.get( i );
          if ( pathSegment != null && pathSegment.length() > 0 ) {
            myPath += "/" + pathSegment;
          }
        }

        if ( myPath.equals( "" ) ) {
          myPath = "/";
        }

        selectedTreeItem = selectedTreeItem.getParentItem();
        if ( selectedTreeItem == null ) {
          selectedTreeItem = repositoryTree.getItem( 0 );
        }

        changeToPath( myPath );
      }
    } );

    navigationBar.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_LEFT );
    navigationBar.add( navigationListBox );
    navigationBar.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_LEFT );
    navigationBar.add( upDirImage );
    navigationBar.setCellWidth( upDirImage, "100%" );
    DOM.setStyleAttribute( upDirImage.getElement(), "marginLeft", "4px" );
    navigationBar.setWidth( "100%" );

    locationBar.add( navigationBar );
    locationBar.setWidth( "100%" );

    Label filenameLabel = new Label( FileChooserEntryPoint.messages.getString( "filename" ) );
    filenameLabel.setWidth( "550px" );
    add( filenameLabel );
    fileNameTextBox.setWidth( "300px" );
    add( fileNameTextBox );
    add( locationBar );
    add( buildFilesList( selectedTreeItem ) );
  }

  public Widget buildFilesList( TreeItem parentTreeItem ) {
    VerticalPanel filesListPanel = new VerticalPanel();
    filesListPanel.setWidth( "100%" );

    ScrollPanel filesScroller = new ScrollPanel();

    filesScroller.setStyleName( "fileChooser-scrollPanel" );

    FlexTable filesListTable = new FlexTable();
    filesListTable.setWidth( "100%" );
    filesListTable.setCellSpacing( 0 );

    Label nameLabel = new Label( FileChooserEntryPoint.messages.getString( "name" ), false );
    nameLabel.setStyleName( "fileChooserHeader" );

    Label typeLabel = new Label( FileChooserEntryPoint.messages.getString( "type" ), false );
    typeLabel.setStyleName( "fileChooserHeader" );

    Label dateLabel = new Label( FileChooserEntryPoint.messages.getString( "dateModified" ), false );
    dateLabel.setStyleName( "fileChooserHeader" );

    ElementUtils.preventTextSelection( nameLabel.getElement() );
    ElementUtils.preventTextSelection( typeLabel.getElement() );
    ElementUtils.preventTextSelection( dateLabel.getElement() );

    filesListTable.setWidget( 0, 0, nameLabel );
    filesListTable.getCellFormatter().setWidth( 0, 0, "100%" );
    filesListTable.setWidget( 0, 1, typeLabel );
    filesListTable.setWidget( 0, 2, dateLabel );

    List<TreeItem> treeItems = new ArrayList<TreeItem>();
    for ( int i = 0; i < parentTreeItem.getChildCount(); i++ ) {
      treeItems.add( parentTreeItem.getChild( i ) );
    }
    Collections.sort( treeItems, new TreeItemComparator() ); // BISERVER-9599 - custom sort

    int row = 0;
    for ( final TreeItem childItem : treeItems ) {
      RepositoryFileTree repositoryFileTree = (RepositoryFileTree) childItem.getUserObject();
      RepositoryFile repositoryFile = repositoryFileTree.getFile();
      if ( repositoryFile.isFolder()
        && !( repositoryFile.getName() != null && repositoryFile.getName().equals( ETC_FOLDER ) ) ) {
        addFileToList( repositoryFileTree, childItem, filesListTable, row++ );
      }
    }
    for ( final TreeItem childItem : treeItems ) {
      RepositoryFileTree repositoryFileTree = (RepositoryFileTree) childItem.getUserObject();
      RepositoryFile repositoryFile = repositoryFileTree.getFile();
      if ( !repositoryFile.isFolder() ) {
        addFileToList( repositoryFileTree, childItem, filesListTable, row++ );
      }
    }

    filesListTable.setWidth( "100%" );
    filesScroller.setWidget( filesListTable );
    filesListTable.setWidth( "100%" );
    filesListPanel.add( filesScroller );

    return filesListPanel;
  }

  private void addFileToList( final RepositoryFileTree repositoryFileTree, final TreeItem item,
                              final FlexTable filesListTable, int row ) {
    Label myDateLabel = null;
    RepositoryFile file = repositoryFileTree.getFile();
    Date lastModDate = file.getLastModifiedDate();
    String fileName = file.getName();

    final Boolean isDir = file.isFolder();
    if ( lastModDate != null ) {
      myDateLabel = new Label( dateFormat.format( lastModDate ), false );
    }

    String finalFileName;
    if ( showLocalizedFileNames ) {
      finalFileName = file.getTitle();
    } else {
      finalFileName = fileName;
    }

    final Label myNameLabel = new Label( finalFileName, false ) {
      public void onBrowserEvent( Event event ) {
        switch ( event.getTypeInt() ) {
          case Event.ONCLICK:
          case Event.ONDBLCLICK:
            handleFileClicked( item, isDir, event, this.getElement() );
            break;
          case Event.ONMOUSEOVER:
            this.addStyleDependentName( "over" );
            break;
          case Event.ONMOUSEOUT:
            this.removeStyleDependentName( "over" );
            break;
        }
      }
    };

    // biserver-2719: concatenate the name with fileChooser_ so the ids are unique in Mantle
    myNameLabel.getElement().setAttribute( "id", "fileChooser_".concat( file.getId() ) );
    myNameLabel.sinkEvents( Event.ONDBLCLICK | Event.ONCLICK );
    myNameLabel.sinkEvents( Event.ONMOUSEOVER | Event.ONMOUSEOUT );
    myNameLabel.setTitle( file.getTitle() );
    myNameLabel.setStyleName( "fileChooserCellLabel" );

    HorizontalPanel fileNamePanel = new HorizontalPanel();
    Image fileImage = new Image() {
      public void onBrowserEvent( Event event ) {
        handleFileClicked( item, isDir, event, myNameLabel.getElement() );
      }
    };

    fileImage.setUrl( GWT.getModuleBaseURL() + "images/spacer.gif" );
    fileImage.addStyleName( "icon-small" );
    fileImage.addStyleName( "clickable" );
    fileImage.sinkEvents( Event.ONDBLCLICK | Event.ONCLICK );

    if ( isDir ) {
      fileImage.addStyleName( "icon-folder" );
    } else {

      if ( fileName.endsWith( "waqr.xaction" ) ) {
        fileImage.addStyleName( "icon-waqr-report" );
      } else if ( fileName.endsWith( "analysisview.xaction" ) ) {
        fileImage.addStyleName( "icon-analysis" );
      } else if ( fileName.endsWith( ".url" ) ) {
        fileImage.addStyleName( "icon-url" );
      } else if ( fileName.endsWith( "xanalyzer" ) ) {
        fileImage.addStyleName( "icon-analyzer" );
      } else if ( fileName.endsWith( "prpti" ) ) {
        fileImage.addStyleName( "icon-pir-report" );
      } else if ( fileName.endsWith( "prpt" ) ) {
        fileImage.addStyleName( "icon-prpt-report" );
      } else if ( fileName.endsWith( "xdash" ) ) {
        fileImage.addStyleName( "icon-dashboard" );
      } else {
        fileImage.addStyleName( "icon-xaction" );

        Integer extensionIndex = fileName.lastIndexOf( "." );
        if ( extensionIndex >= 0 ) {
          String extension = fileName.substring( extensionIndex + 1 );
          if ( extension.length() > 0 && !extension.equalsIgnoreCase( "xaction" ) ) {
            fileImage.addStyleName( "icon-unknown" );

            fileImage.addStyleName( "icon-" + CssUtils.escape( extension ) );
          }
        }
      }
    }

    fileNamePanel.add( fileImage );
    fileNamePanel.add( myNameLabel );
    DOM.setStyleAttribute( myNameLabel.getElement(), "cursor", "default" );

    Label typeLabel =
      new Label(
        isDir
          ? FileChooserEntryPoint.messages.getString( "folder" ) : FileChooserEntryPoint.messages.getString( "file" ),
        false );

    ElementUtils.preventTextSelection( myNameLabel.getElement() );
    ElementUtils.preventTextSelection( typeLabel.getElement() );
    if ( myDateLabel != null ) {
      ElementUtils.preventTextSelection( myDateLabel.getElement() );
    }
    fileNamePanel.setStyleName( "fileChooserCell" );
    typeLabel.setStyleName( "fileChooserCell" );
    if ( myDateLabel != null ) {
      myDateLabel.setStyleName( "fileChooserCell" );
    }
    filesListTable.setWidget( row + 1, 0, fileNamePanel );
    filesListTable.setWidget( row + 1, 1, typeLabel );
    if ( myDateLabel != null ) {
      filesListTable.setWidget( row + 1, 2, myDateLabel );
    }
  }

  private void handleFileClicked( final TreeItem item, final boolean isDir, final Event event,
                                  com.google.gwt.user.client.Element sourceElement ) {
    boolean eventWeCareAbout = false;
    TreeItem tmpItem;
    if ( ( DOM.eventGetType( event ) & Event.ONDBLCLICK ) == Event.ONDBLCLICK
      || ( DOM.eventGetType( event ) & Event.ONCLICK ) == Event.ONCLICK ) {
      eventWeCareAbout = true;
    }

    if ( eventWeCareAbout ) {
      setFileSelected( true );
      selectedTreeItem = tmpItem = item;

      List<String> parentSegments = new ArrayList<String>();
      while ( tmpItem != null ) {
        RepositoryFileTree tree = (RepositoryFileTree) tmpItem.getUserObject();
        RepositoryFile file = tree.getFile();
        if ( file != null && file.getName() != null ) {
          parentSegments.add( file.getName() );
        }
        tmpItem = tmpItem.getParentItem();
      }
      Collections.reverse( parentSegments );
      String myPath = "";
      for ( int i = 0; isDir ? i < parentSegments.size() : i < parentSegments.size() - 1; i++ ) {
        String segment = parentSegments.get( i );
        if ( segment != null && segment.length() > 0 ) {
          myPath += "/" + segment;
        }
      }
      setSelectedPath( myPath );
      if ( !isDir ) {
        RepositoryFileTree tree = (RepositoryFileTree) selectedTreeItem.getUserObject();
        if ( tree.getFile() != null ) {
          fileNameTextBox.setText( tree.getFile().getTitle() );
          actualFileName = tree.getFile().getName();
        }
      }
    }

    // double click
    if ( ( DOM.eventGetType( event ) & Event.ONDBLCLICK ) == Event.ONDBLCLICK ) {
      if ( isDir ) {
        if ( !isLazy ) {
          initUI();
        } else {
          loadDirectory( this.selectedPath );
        }
      } else {
        fireFileSelected();
      }
    } else if ( ( DOM.eventGetType( event ) & Event.ONCLICK ) == Event.ONCLICK ) {
      fireFileSelectionChanged();
      // single click
      // highlight row
      if ( lastSelectedFileElement != null ) {
        com.google.gwt.dom.client.Element parentRow =
          ElementUtils.findElementAboveByTagName( lastSelectedFileElement, "table" );
        parentRow.removeClassName( "pentaho-file-chooser-selection" );
      }
      com.google.gwt.dom.client.Element parentRow =
        ElementUtils.findElementAboveByTagName( sourceElement, "table" );
      parentRow.addClassName( "pentaho-file-chooser-selection" );
      lastSelectedFileElement = sourceElement;
    }
  }

  private void setFileSelected( boolean selected ) {
    fileSelected = selected;
  }

  public boolean isFileSelected() {
    return fileSelected;
  }

  public TreeItem getTreeItem( List<String> pathSegments ) {
    // find the tree node whose location matches the pathSegment paths
    TreeItem selectedItem = repositoryTree.getItem( 0 );
    for ( String segment : pathSegments ) {
      TreeItem childItem = getChildTreeItem( segment, selectedItem );
      if ( childItem != null ) {
        selectedItem = childItem;
      } else {
        break;
      }
    }

    return selectedItem;
  }

  private TreeItem getChildTreeItem( String itemName, TreeItem parentItem ) {
    for ( int i = 0; i < parentItem.getChildCount(); i++ ) {
      TreeItem item = parentItem.getChild( i );
      RepositoryFileTree tree = (RepositoryFileTree) item.getUserObject();
      if ( itemName.equals( tree.getFile().getName() ) ) {
        return item;
      }
    }

    return null;
  }

  public FileChooserMode getMode() {
    return mode;
  }

  public void setMode( FileChooserMode mode ) {
    this.mode = mode;
  }

  public String getSelectedPath() {
    return selectedPath;
  }

  public void setSelectedPath( String selectedPath ) {
    this.selectedPath = selectedPath;
  }

  public void setFileChooserRepositoryFileTree( RepositoryFileTree fileChooserRepositoryFileTree ) {
    this.fileTree = fileChooserRepositoryFileTree;
    repositoryTree = TreeBuilder.buildSolutionTree( fileChooserRepositoryFileTree, showHiddenFiles,
      showLocalizedFileNames, fileFilter );
    initUI();
  }

  public void fireFileSelected( RepositoryFile file ) {
    for ( FileChooserListener listener : listeners ) {
      listener.fileSelected( file, file.getPath(), ( mode != null && mode.equals( FileChooserMode.SAVE )
        ? actualFileName : file.getName() ), file.getTitle() );
    }
  }

  public void fireFileSelected() {
    for ( FileChooserListener listener : listeners ) {
      RepositoryFileTree tree = (RepositoryFileTree) selectedTreeItem.getUserObject();
      RepositoryFile file = tree.getFile();
      listener.fileSelected( file, file.getPath(), ( mode != null && mode.equals( FileChooserMode.SAVE )
        ? actualFileName : file.getName() ), file.getTitle() );
    }
  }

  public void fireFileSelectionChanged() {
    for ( FileChooserListener listener : listeners ) {
      RepositoryFileTree tree = (RepositoryFileTree) selectedTreeItem.getUserObject();
      RepositoryFile file = tree.getFile();
      listener.fileSelectionChanged( file, file.getPath(), ( mode != null && mode.equals( FileChooserMode.SAVE )
        ? actualFileName : file.getName() ), file.getTitle() );
    }
  }

  public String getSolution() {
    if ( getSelectedPath().indexOf( "/", 1 ) == -1 ) {
      return getSelectedPath().substring( 1 );
    } else {
      return getSelectedPath().substring( 1, getSelectedPath().indexOf( "/", 1 ) );
    }
  }

  /**
   * Get the names of all files in the given path.
   *
   * @param path Path to query for files
   * @return List of file names in the given path.
   */
  public List<String> getFilesInPath( final RepositoryFileTree fileTreeItem ) {
    List<String> fileNames = new ArrayList<String>();
    for ( RepositoryFileTree treeItem : fileTreeItem.getChildren() ) {
      RepositoryFile file = treeItem.getFile();
      if ( file != null && !file.isFolder() ) {
        fileNames.add( file.getName() );
      }
    }

    return fileNames;
  }

  public boolean doesFileExist( final String path ) {
    return search( this.fileTree, path ) != null;
  }

  public String getActualFileName() {
    return actualFileName;
  }

  public String getLocalizedFileName() {
    return fileNameTextBox.getText();
  }

  public void addFileChooserListener( FileChooserListener listener ) {
    listeners.add( listener );
  }

  public void removeFileChooserListener( FileChooserListener listener ) {
    listeners.remove( listener );
  }

  public FileFilter getFileFilter() {
    return fileFilter;
  }

  public void setFileFilter( FileFilter fileFilter ) {
    this.fileFilter = fileFilter;

    repositoryTree = TreeBuilder.buildSolutionTree( fileTree, showHiddenFiles, showLocalizedFileNames, fileFilter );

    initUI();
  }

  public boolean doesSelectedFileExist() {
    return doesSelectedFileExist( null );
  }

  public boolean doesSelectedFileExist( String ext ) {
    String fileName = this.selectedPath + "/" + this.actualFileName;
    if ( !StringUtils.isEmpty( ext ) && !fileName.endsWith( ext ) ) {
      fileName = fileName + ext;
    }

    return search( fileTree, fileName ) != null;
  }

  public void setShowLocalizedFileNames( boolean showLocalizedFileNames ) {
    this.showLocalizedFileNames = showLocalizedFileNames;
    initUI();
  }

  public void changeToPath( String path ) {
    setSelectedPath( path );

    if ( !isLazy ) {
      initUI();
      fireFileSelectionChanged();
    } else {
      loadDirectory( path );
    }
  }

  public void setSubmitOnEnter( boolean submitOnEnter ) {
    this.submitOnEnter = submitOnEnter;
  }

  public boolean isSubmitOnEnter() {
    return submitOnEnter;
  }

  public boolean isLazy() {
    return isLazy;
  }

  public void setLazy( boolean lazy ) {
    isLazy = lazy;
  }

  public FileChooserTreeListener getTreeListener() {
    return treeListener;
  }

  public void setTreeListener( FileChooserTreeListener treeListener ) {
    this.treeListener = treeListener;
  }

  /**
   * Safari Mobile behaves differently than browsers on a computer. These rules may extend to other mobile browsers.
   *
   * @return
   */
  public native boolean isMobileSafari()/*-{
    return (window.orientation !== undefined);
  }-*/;

  public native String getWebAppRoot()/*-{
    if ($wnd.CONTEXT_PATH) {
      return $wnd.CONTEXT_PATH;
    }
    return "";
  }-*/;

}
