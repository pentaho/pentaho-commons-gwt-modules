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
 * Copyright (c) 2002 - 2023 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.gwt.widgets.client.filechooser;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.pentaho.gwt.widgets.client.buttons.ThemeableImageButton;
import org.pentaho.gwt.widgets.client.dialogs.IDialogCallback;
import org.pentaho.gwt.widgets.client.dialogs.MessageDialogBox;
import org.pentaho.gwt.widgets.client.panel.HorizontalFlexPanel;
import org.pentaho.gwt.widgets.client.panel.ScrollFlexPanel;
import org.pentaho.gwt.widgets.client.panel.VerticalFlexPanel;
import org.pentaho.gwt.widgets.client.utils.ElementUtils;
import org.pentaho.gwt.widgets.client.utils.NameUtils;
import org.pentaho.gwt.widgets.client.utils.string.CssUtils;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;
import org.pentaho.mantle.client.environment.EnvironmentHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
@SuppressWarnings( "deprecation" )
public class FileChooser extends VerticalFlexPanel {

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

    addStyleName( "fileChooser" );
    addStyleName( "with-scroll-child" );

    fileNameTextBox.getElement().setId( "fileNameTextBox" );
    fileNameTextBox.addStyleName( "fileNameTextBox" );

    // workaround webkit browsers quirk of not being able to set focus in a widget by clicking on it
    fileNameTextBox.addClickHandler( new ClickHandler() {
      public void onClick( ClickEvent event ) {
        fileNameTextBox.setFocus( true );
      }
    } );

    fileNameTextBox.addKeyDownHandler( new KeyDownHandler() {
      public void onKeyDown( KeyDownEvent event ) {
        actualFileName = fileNameTextBox.getText();
        if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER && isSubmitOnEnter() ) {
          if ( mode != FileChooserMode.SAVE ) {
            fireFileSelected( search( fileTree, actualFileName ) );
          } else {
            fireFileSelected();
          }
          event.preventDefault();
        }
      }
    } );

    fileNameTextBox.addValueChangeHandler( new ValueChangeHandler<String>() {
      @Override
      public void onValueChange( ValueChangeEvent<String> valueChangeEvent ) {
       actualFileName = fileNameTextBox.getText();
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
    // Encode special chars for URL
    String encodedFolder = NameUtils.URLEncode( folder == null ? ":" : folder.replace( "/", ":" ) );

    if ( filter == null ) {
      filter = "*";
    }

    return EnvironmentHelper.getFullyQualifiedURL() + "api/repo/files/" + encodedFolder + "/tree?"
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

    VerticalPanel locationBar = new VerticalFlexPanel();
    locationBar.addStyleName( "locationFieldGroup" );

    Label locationLabel = new Label( FileChooserEntryPoint.messages.getString( "location" ) );

    navigationListBox = new ListBox();
    navigationListBox.getElement().setId( "navigationListBox" );
    navigationListBox.addStyleName( "navigationListBox" );
    navigationListBox.addStyleName( "height-auto" );

    navigationListBox.setWidth( "350px" );
    Roles.getListboxRole().set( navigationListBox.getElement() );
    ElementUtils.setAriaLabelledBy( navigationListBox, locationLabel );

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

    locationBar.add( locationLabel );

    HorizontalPanel navigationBar = new HorizontalFlexPanel();
    navigationBar.addStyleName( "locationFieldAndUpDirGroup" );

    final Image upDirImage = new ThemeableImageButton( "pentaho-filechooseupbutton", null, FileChooserEntryPoint.messages.getString( "upOneLevel" ) );
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
    ElementUtils.setAriaLabelledBy( fileNameTextBox, filenameLabel );

    filenameLabel.setWidth( "550px" );
    add( filenameLabel );
    fileNameTextBox.setWidth( "300px" );
    add( fileNameTextBox );
    add( locationBar );
    add( buildFilesList( selectedTreeItem ) );
  }

  public Widget buildFilesList( TreeItem parentTreeItem ) {
    VerticalFlexPanel filesListPanel = new VerticalFlexPanel();
    filesListPanel.addStyleName( "fileChooserFilesListContainer" );
    filesListPanel.setWidth( "100%" );

    ScrollFlexPanel filesScroller = new ScrollFlexPanel();
    Roles.getListboxRole().set( filesScroller.getElement() );
    Roles.getListboxRole().setTabindexExtraAttribute( filesScroller.getElement(), 0 );

    filesScroller.addStyleName( "fileChooser-scrollPanel" );

    FlexTable filesListTable = new FlexTable();
    filesListTable.addStyleName( "fileChooserFilesList" );
    filesListTable.setWidth( "100%" );
    filesListTable.setCellSpacing( 0 );

    // Table column header labels
    // Ellipsis is shown on overflow, so set title with original text as fallback.
    Label nameLabel = new Label( FileChooserEntryPoint.messages.getString( "name" ), false );
    nameLabel.setTitle( nameLabel.getText() );
    nameLabel.setStyleName( "fileChooserHeader" );

    Label typeLabel = new Label( FileChooserEntryPoint.messages.getString( "type" ), false );
    typeLabel.setStyleName( "fileChooserHeader" );

    Label dateLabel = new Label( FileChooserEntryPoint.messages.getString( "dateModified" ), false );
    dateLabel.setTitle( dateLabel.getText() );
    dateLabel.setStyleName( "fileChooserHeader" );

    ElementUtils.preventTextSelection( nameLabel.getElement() );
    ElementUtils.preventTextSelection( typeLabel.getElement() );
    ElementUtils.preventTextSelection( dateLabel.getElement() );

    // Ensure colgroup columns exist and can be styled via CSS.
    filesListTable.getColumnFormatter().addStyleName( 0, "fileNameColumn" );
    filesListTable.getColumnFormatter().addStyleName( 1, "fileOrFolderColumn" );
    filesListTable.getColumnFormatter().addStyleName( 2, "fileDateModifiedColumn" );

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
        addFileToList( repositoryFileTree, childItem, filesListTable, row++, treeItems.size(), filesScroller );
      }
    }
    for ( final TreeItem childItem : treeItems ) {
      RepositoryFileTree repositoryFileTree = (RepositoryFileTree) childItem.getUserObject();
      RepositoryFile repositoryFile = repositoryFileTree.getFile();
      if ( !repositoryFile.isFolder() ) {
        addFileToList( repositoryFileTree, childItem, filesListTable, row++, treeItems.size(), filesScroller );
      }
    }

    filesListTable.setWidth( "100%" );
    filesScroller.setWidget( filesListTable );
    filesListTable.setWidth( "100%" );
    filesListPanel.add( filesScroller );

    return filesListPanel;
  }

  private void addFileToList( final RepositoryFileTree repositoryFileTree, final TreeItem item,
                             final FlexTable filesListTable, int row, int size, ScrollFlexPanel fileScroller ) {
    Label myDateLabel = null;
    RepositoryFile file = repositoryFileTree.getFile();
    Date lastModDate = file.getLastModifiedDate();
    String fileName = file.getName();

    final Boolean isDir = file.isFolder();
    if ( lastModDate != null ) {
      String lastModifiedDateText = dateFormat.format( lastModDate );
      myDateLabel = new Label( lastModifiedDateText, false );
      myDateLabel.setTitle( lastModifiedDateText );
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

    HorizontalPanel fileNamePanel = new HorizontalFlexPanel() {
      @Override
      public void onBrowserEvent( Event event ) {
        switch ( event.getKeyCode() ) {
          case KeyCodes.KEY_ENTER:
            handleFileClicked( item, isDir, event, this.getElement() );
            event.preventDefault();
            break;
          case KeyCodes.KEY_UP:
            if ( row > 0 && row <= size - 1 ) {
              this.getElement().removeAttribute( "tabindex" );
              Widget previousFileNamePanel = filesListTable.getWidget( row, 0 );
              previousFileNamePanel.getElement().setTabIndex( 0 );
              previousFileNamePanel.getElement().focus();
              event.preventDefault();
              fileScroller.ensureVisible( previousFileNamePanel );
            }
            break;
          case KeyCodes.KEY_DOWN:
            if ( row >= 0 && row < size - 1 ) {
              this.getElement().removeAttribute( "tabindex" );
              Widget nextFileNamePanel = filesListTable.getWidget( row + 2, 0 );
              nextFileNamePanel.getElement().setTabIndex( 0 );
              nextFileNamePanel.getElement().focus();
              event.preventDefault();
              fileScroller.ensureVisible( nextFileNamePanel );
            }
            break;
          case KeyCodes.KEY_HOME:
            this.getElement().removeAttribute( "tabindex" );
            Widget firstFileNamePanel = filesListTable.getWidget( 1, 0 );
            firstFileNamePanel.getElement().setTabIndex( 0 );
            firstFileNamePanel.getElement().focus();
            event.preventDefault();
            fileScroller.ensureVisible( firstFileNamePanel );
            break;
          case KeyCodes.KEY_END:
            this.getElement().removeAttribute( "tabindex" );
            Widget lastFileNamePanel = filesListTable.getWidget( size, 0 );
            lastFileNamePanel.getElement().setTabIndex( 0 );
            lastFileNamePanel.getElement().focus();
            event.preventDefault();
            fileScroller.ensureVisible( lastFileNamePanel );
            break;
        }
      }
    };
    fileNamePanel.sinkEvents( Event.ONKEYDOWN );
    Roles.getOptionRole().set( fileNamePanel.getElement() );
    if ( row == 0 ) {
      Roles.getOptionRole().setTabindexExtraAttribute( fileNamePanel.getElement(), 0 );
    }

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
      // Show title on image with the contents of `typeLabel` (see below),
      // to maintain some accessibility when the Type column is hidden via CSS.
      fileImage.setTitle( FileChooserEntryPoint.messages.getString( "folder" ) );
    } else {
      fileImage.setTitle( FileChooserEntryPoint.messages.getString( "file" ) );

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
    fileNamePanel.addStyleName( "fileChooserCell" );
    fileNamePanel.addStyleName( "fileChooserCellFileName" );

    typeLabel.setStyleName( "fileChooserCell" );
    typeLabel.addStyleName( "fileChooserCellFileOrFolder" );

    if ( myDateLabel != null ) {
      myDateLabel.setStyleName( "fileChooserCell" );
      myDateLabel.addStyleName( "fileChooserCellFileDateModified" );
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
      || ( DOM.eventGetType( event ) & Event.ONCLICK ) == Event.ONCLICK
      || event.getKeyCode() == KeyCodes.KEY_ENTER ) {
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
    if ( ( DOM.eventGetType( event ) & Event.ONDBLCLICK ) == Event.ONDBLCLICK
      || event.getKeyCode() == KeyCodes.KEY_ENTER ) {
      if ( isDir ) {
        changeToPath( this.selectedPath );
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
    if ( file == null ) {
      MessageDialogBox dialogBox = new MessageDialogBox( FileChooserEntryPoint.messages.getString( "error" ), FileChooserEntryPoint.messages
                      .getString( "noFilenameEntered" ), false, false, true );
      dialogBox.center();
      return;
    }
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

      if ( treeListener != null ) {
        treeListener.loaded();
      }
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
}
