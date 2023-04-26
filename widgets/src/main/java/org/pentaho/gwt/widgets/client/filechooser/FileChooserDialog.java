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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import org.pentaho.gwt.widgets.client.dialogs.IDialogCallback;
import org.pentaho.gwt.widgets.client.dialogs.IDialogValidatorCallback;
import org.pentaho.gwt.widgets.client.dialogs.MessageDialogBox;
import org.pentaho.gwt.widgets.client.dialogs.PromptDialogBox;
import org.pentaho.gwt.widgets.client.filechooser.FileChooser.FileChooserMode;
import org.pentaho.gwt.widgets.client.utils.NameUtils;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;

import com.google.gwt.user.client.ui.KeyboardListener;

public class FileChooserDialog extends PromptDialogBox implements FileChooserListener {

  private static String OPEN = FileChooserEntryPoint.messages.getString( "Open" );
  private static String SAVE = FileChooserEntryPoint.messages.getString( "Save" );

  private static String lastOpenLocation = "";
  private static boolean isDirty = false;

  private ArrayList<FileChooserListener> listeners = new ArrayList<FileChooserListener>();

  private FileChooser fileChooser;
  private FileFilter filter;

  private boolean submitOnEnter = true;

  public FileChooserDialog( FileChooserMode mode, String selectedPath, boolean autoHide, boolean modal ) {
    this( mode, selectedPath, autoHide, modal, mode == FileChooserMode.OPEN ? OPEN : SAVE,
      mode == FileChooserMode.OPEN ? OPEN : SAVE );
  }

  public FileChooserDialog( FileChooserMode mode, String selectedPath, boolean autoHide, boolean modal,
                            String title, String okText ) {
    super( title, okText, FileChooserEntryPoint.messages.getString( "Cancel" ), false, true );

    addStyleName( "fileChooserDialog" );

    setResponsive( true );
    setSizingMode( DialogSizingMode.FILL_VIEWPORT_WIDTH );
    setWidthCategory( DialogWidthCategory.SMALL );

    setupNativeHooks();

    fileChooser = new FileChooser( mode, selectedPath, new IDialogCallback() {
      public void cancelPressed() {

      }

      public void okPressed() {
        center();
      }
    } );

    this.setContent( fileChooser );

    fileChooser.setWidth( "100%" );
    setValidatorCallback( new IDialogValidatorCallback() {
      public boolean validate() {
        return isFileNameValid();
      }
    } );

    IDialogCallback callback = new IDialogCallback() {

      public void cancelPressed() {
        for ( FileChooserListener listener : listeners ) {
          listener.dialogCanceled();
        }
      }

      public void okPressed() {
        fileChooser.fireFileSelected();
      }

    };

    setCallback( callback );
    fileChooser.addFileChooserListener( this );
    fileChooser.setTreeListener( () -> center() );
  }

  private static native String getDefaultFolderLocation()
    /*-{
        return $wnd.top.DEFAULT_FOLDER;
    }-*/;

  private static native String getHomeFolderLocation()
  /*-{
      return $wnd.top.HOME_FOLDER;
  }-*/;

  public static native void setupNativeHooks()
  /*-{
      $wnd.mantle_setIsRepoDirty = function(isDirty) {
          @org.pentaho.gwt.widgets.client.filechooser.FileChooserDialog::setIsDirty(Z)(isDirty);
      }
      $wnd.mantle_getIsRepoDirty = function() {
          return @org.pentaho.gwt.widgets.client.filechooser.FileChooserDialog::getIsDirty()();
      }
  }-*/;

  public static boolean getIsDirty() {
    return isDirty;
  }

  public static void setIsDirty( boolean isDirty ) {
    FileChooserDialog.isDirty = isDirty;
  }

  private static String getLastOpenLocation() {
    return lastOpenLocation;
  }

  private static void setLastOpenLocation( String path ) {
    lastOpenLocation = path;
  }

  private RepositoryFile doesFolderExist( RepositoryFileTree tree, String folderPath ) {
    try {
      RepositoryFile file = tree.getFile();
      if ( file != null && file.getPath().equals( folderPath ) ) {
        return file;
      }

      if ( file != null ) {
        for ( RepositoryFileTree treeItem : tree.getChildren() ) {
          file = doesFolderExist( treeItem, folderPath );
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

  public FileChooserDialog( FileChooserMode mode, String selectedPath,
                            boolean autoHide, boolean modal, boolean showHiddenFiles ) {
    this( mode, selectedPath, null, autoHide, modal, mode == FileChooserMode.OPEN ? OPEN : SAVE,
      mode == FileChooserMode.OPEN ? OPEN : SAVE, showHiddenFiles );
  }

  public FileChooserDialog( FileChooserMode mode, String selectedPath, RepositoryFileTree fileTree,
                            boolean autoHide, boolean modal ) {
    this( mode, selectedPath, fileTree, autoHide, modal, mode == FileChooserMode.OPEN ? OPEN : SAVE,
      mode == FileChooserMode.OPEN ? OPEN : SAVE );
  }

  public FileChooserDialog( FileChooserMode mode, String selectedPath, RepositoryFileTree fileTree,
                            boolean autoHide, boolean modal, String title, String okText ) {
    this( mode, selectedPath, fileTree, autoHide, modal, title, okText, false );
  }

  public FileChooserDialog( FileChooserMode mode, String selectedPath, RepositoryFileTree fileTree,
                            boolean autoHide, boolean modal, boolean showHiddenFiles ) {
    this( mode, selectedPath, fileTree, autoHide, modal, mode == FileChooserMode.OPEN ? OPEN : SAVE,
      mode == FileChooserMode.OPEN ? OPEN : SAVE, showHiddenFiles );
  }

  public FileChooserDialog( FileChooserMode mode, String selectedPath, RepositoryFileTree fileTree,
                            boolean autoHide, boolean modal, String title, String okText, boolean showHiddenFiles ) {
    super( title, okText, FileChooserEntryPoint.messages.getString( "Cancel" ), false, true );

    addStyleName( "fileChooserDialog" );

    setResponsive( true );
    setSizingMode( DialogSizingMode.FILL_VIEWPORT_WIDTH );
    setWidthCategory( DialogWidthCategory.SMALL );

    fileChooser = new FileChooser( showHiddenFiles );

    setContent( fileChooser );

    fileChooser.setMode( mode );

    final boolean isLazy = fileTree == null;
    fileChooser.setLazy( isLazy );

    setupNativeHooks();

    if ( mode.equals( FileChooserMode.OPEN ) ) {
      if ( getLastOpenLocation() == null ) {
        fileChooser.setSelectedPath( getDefaultFolderLocation() );
      } else {
        fileChooser.setSelectedPath( getLastOpenLocation() );
        if ( doesFolderExist( fileTree, fileChooser.selectedPath ) == null ) {
          fileChooser.setSelectedPath( getDefaultFolderLocation() );
        }
      }
    }

    if ( mode.equals( FileChooserMode.SAVE ) ) {
      final String path = StringUtils.isEmpty( selectedPath ) ? getDefaultFolderLocation() : selectedPath;

      fileChooser.setSelectedPath( path );
    }

    setValidatorCallback( new IDialogValidatorCallback() {
      public boolean validate() {
        return isFileNameValid();
      }
    } );

    IDialogCallback callback = new IDialogCallback() {

      public void cancelPressed() {
        for ( FileChooserListener listener : listeners ) {
          listener.dialogCanceled();
        }
      }

      public void okPressed() {
        fileChooser.fireFileSelected();
      }

    };

    setCallback( callback );

    fileChooser.addFileChooserListener( this );
    fileChooser.setTreeListener( new FileChooserTreeListener() {
      @Override
      public void loaded() {
        center();
      }
    } );

    if ( fileTree == null ) {
      try {
        fileChooser.fetchRepositoryDirectory( fileChooser.getSelectedPath(), 1, null );
      } catch ( RequestException e ) {
        Window.alert( "Unabled to load " + fileChooser.getSelectedPath() );
      }
    } else {
      fileChooser.fileTree = fileTree;
      fileChooser.repositoryTree = TreeBuilder.buildSolutionTree(
        fileTree, fileChooser.showHiddenFiles, fileChooser.showLocalizedFileNames, filter );
      fileChooser.selectedTreeItem = fileChooser.repositoryTree.getItem( 0 );
      fileChooser.initUI();
    }
  }

  public void addFileChooserListener( FileChooserListener listener ) {
    listeners.add( listener );
  }

  public void removeFileChooserListener( FileChooserListener listener ) {
    if ( listeners.contains( listener ) ) {
      listeners.remove( listener );
    }
  }

  public boolean doesSelectedFileExist() {
    return doesSelectedFileExist( null );
  }

  public boolean doesSelectedFileExist( String ext ) {
    if ( StringUtils.isEmpty( ext ) ) {
      return fileChooser.doesSelectedFileExist();
    }

    return fileChooser.doesSelectedFileExist( ext );
  }

  public List<String> getFilesInPath( final RepositoryFileTree fileTreeItem ) {
    return fileChooser.getFilesInPath( fileTreeItem );
  }

  public boolean doesFileExist( final String path ) {
    return fileChooser.doesFileExist( path );
  }

  public FileFilter getFileFilter() {
    return filter;
  }

  public void setFileFilter( FileFilter filter ) {
    this.filter = filter;

    fileChooser.setFileFilter( filter );
  }

  /**
   * This method get the actual file name of the selected file
   *
   * @return the actual file name
   */
  private String getActualFileName() {
    final String actualFileName = fileChooser.getActualFileName();
    if ( actualFileName != null && !"".equals( actualFileName ) ) {
      return actualFileName;
    }

    return "";
  }

  /*
   * If the file name is empty or null then return false, else return true.
   */
  private boolean isFileNameValid() {

    // don't allow saving in the root of the solution repository
    String solution = fileChooser.getSolution();
    if ( ( fileChooser.mode == FileChooserMode.SAVE ) && ( ( solution == null || solution.trim().length() == 0 ) ) ) {
      MessageDialogBox dialogBox =
          new MessageDialogBox( FileChooserEntryPoint.messages.getString( "error" ), FileChooserEntryPoint.messages
              .getString( "noSolutionSelected" ), false, false, true );
      dialogBox.center();
      return false;
    }

    final String fileName = getActualFileName();
    if ( StringUtils.isEmpty( fileName ) ) {
      MessageDialogBox dialogBox =
          new MessageDialogBox( FileChooserEntryPoint.messages.getString( "error" ), FileChooserEntryPoint.messages
              .getString( "noFilenameEntered" ), false, false, true );
      dialogBox.center();
      return false;
    } else if ( !NameUtils.isValidFileName( fileName ) ) {
      MessageDialogBox dialogBox =
          new MessageDialogBox( FileChooserEntryPoint.messages.getString( "error" ),
            FileChooserEntryPoint.messages.getString( "invalidFilename",
              "Invalid Filename",  // default value if key isn't found
              NameUtils.reservedCharListForDisplay() ),
            false, false, true );
      dialogBox.center();
      return false;
    }
    return true;
  }

  public void fileSelected( RepositoryFile file, String filePath, String fileName, String title ) {
    if ( isFileNameValid() ) {
      for ( FileChooserListener listener : listeners ) {
        listener.fileSelected( file, filePath, fileName, title );
        if ( fileChooser.mode == FileChooserMode.OPEN ) {
          setLastOpenLocation( fileChooser.selectedPath );
        }
      }

      this.hide();
    }
  }

  public void fileSelectionChanged( RepositoryFile file, String filePath, String fileName, String title ) {
    for ( FileChooserListener listener : listeners ) {
      listener.fileSelectionChanged( file, filePath, fileName, title );
    }
  }

  public void dialogCanceled() {
    for ( FileChooserListener listener : listeners ) {
      listener.dialogCanceled();
    }
  }

  public boolean isSubmitOnEnter() {
    return submitOnEnter;
  }

  public void setSubmitOnEnter( boolean submitOnEnter ) {
    this.submitOnEnter = submitOnEnter;
    fileChooser.setSubmitOnEnter( submitOnEnter );
  }

  @Override
  @Deprecated
  public boolean onKeyDownPreview( char key, int modifiers ) {
    // Use the popup's key preview hooks to close the dialog when either
    // enter or escape is pressed.
    if ( key == (char) KeyboardListener.KEY_ESCAPE ) {
      onCancel();
    }
    return true;
  }
}
