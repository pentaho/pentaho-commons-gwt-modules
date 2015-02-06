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

package org.pentaho.gwt.widgets.client.filechooser;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.gwt.widgets.client.dialogs.GlassPane;
import org.pentaho.gwt.widgets.client.dialogs.IDialogCallback;
import org.pentaho.gwt.widgets.client.dialogs.IDialogValidatorCallback;
import org.pentaho.gwt.widgets.client.dialogs.MessageDialogBox;
import org.pentaho.gwt.widgets.client.dialogs.PromptDialogBox;
import org.pentaho.gwt.widgets.client.filechooser.FileChooser.FileChooserMode;
import org.pentaho.gwt.widgets.client.utils.NameUtils;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;

import com.google.gwt.user.client.ui.KeyboardListener;

public class FileChooserDialog extends PromptDialogBox implements FileChooserListener {

  //private static final String ILLEGAL_NAME_CHARS = "\\\'/?%*:|\"<>&"; //$NON-NLS-1$
  private static String lastOpenLocation = "";
  private static boolean isDirty = false;

  private ArrayList<FileChooserListener> listeners = new ArrayList<FileChooserListener>();

  private FileChooser fileChooser;
  private FileFilter filter;

  private boolean submitOnEnter = true;

  public FileChooserDialog( FileChooserMode mode, String selectedPath, boolean autoHide, boolean modal ) {
    this(
        mode,
        selectedPath,
        autoHide,
        modal,
        mode == FileChooserMode.OPEN
            ? FileChooserEntryPoint.messages.getString( "Open" ) : FileChooserEntryPoint.messages.getString( "Save" ), mode == FileChooserMode.OPEN ? FileChooserEntryPoint.messages.getString( "Open" ) : FileChooserEntryPoint.messages.getString( "Save" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
  }

  public FileChooserDialog( FileChooserMode mode, String selectedPath, boolean autoHide, boolean modal, String title,
      String okText ) {
    super( title, okText, FileChooserEntryPoint.messages.getString( "Cancel" ), false, true );
    showGlassPane();
    setupNativeHooks();
    fileChooser = new FileChooser( mode, selectedPath, new IDialogCallback() {
      public void cancelPressed() {

      }

      public void okPressed() {
        center();
      }
    } );
    this.setContent( fileChooser );

    fileChooser.setWidth( "100%" ); //$NON-NLS-1$
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
  }

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
      return null;
    }
    return null;
  }

  public FileChooserDialog( FileChooserMode mode, String selectedPath, RepositoryFileTree fileTree, boolean autoHide,
      boolean modal, String title, String okText ) {
    this( mode, selectedPath, fileTree, autoHide, modal, title, okText, false );
  }

  public FileChooserDialog( FileChooserMode mode, String selectedPath, RepositoryFileTree fileTree, boolean autoHide,
      boolean modal, String title, String okText, boolean showHiddenFiles ) {
    super( title, okText, FileChooserEntryPoint.messages.getString( "Cancel" ), false, true ); //$NON-NLS-1$
    fileChooser = new FileChooser( showHiddenFiles );
    setContent( fileChooser );
    fileChooser.setWidth( "100%" ); //$NON-NLS-1$
    fileChooser.setMode( mode );
    setupNativeHooks();
    if ( mode.equals( FileChooserMode.OPEN ) ) {
      if ( getLastOpenLocation() == null ) {
        fileChooser.setSelectedPath( getHomeFolderLocation() );
      } else {
        fileChooser.setSelectedPath( getLastOpenLocation() );
        if ( doesFolderExist( fileTree, fileChooser.selectedPath ) == null ) {
          fileChooser.setSelectedPath( getHomeFolderLocation() );
        }
      }
    }
    if ( mode.equals( FileChooserMode.SAVE ) ) {
      if ( StringUtils.isEmpty( selectedPath ) ) {
        fileChooser.setSelectedPath( getHomeFolderLocation() );
      } else {
        fileChooser.setSelectedPath( selectedPath );
      }
    }

    fileChooser.fileTree = fileTree;
    fileChooser.repositoryTree =
        TreeBuilder.buildSolutionTree( fileTree, fileChooser.showHiddenFiles, fileChooser.showLocalizedFileNames,
            filter );
    fileChooser.selectedTreeItem = fileChooser.repositoryTree.getItem( 0 );
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
    fileChooser.initUI();
  }

  public FileChooserDialog( FileChooserMode mode, String selectedPath, RepositoryFileTree fileTree, boolean autoHide,
      boolean modal ) {
    this(
        mode,
        selectedPath,
        fileTree,
        autoHide,
        modal,
        mode == FileChooserMode.OPEN
            ? FileChooserEntryPoint.messages.getString( "Open" ) : FileChooserEntryPoint.messages.getString( "Save" ), mode == FileChooserMode.OPEN ? FileChooserEntryPoint.messages.getString( "Open" ) : FileChooserEntryPoint.messages.getString( "Save" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
  }

  public FileChooserDialog( FileChooserMode mode, String selectedPath, RepositoryFileTree fileTree, boolean autoHide,
      boolean modal, boolean showHiddenFiles ) {
    this(
        mode,
        selectedPath,
        fileTree,
        autoHide,
        modal,
        mode == FileChooserMode.OPEN
            ? FileChooserEntryPoint.messages.getString( "Open" ) : FileChooserEntryPoint.messages.getString( "Save" ), mode == FileChooserMode.OPEN ? FileChooserEntryPoint.messages.getString( "Open" ) : FileChooserEntryPoint.messages.getString( "Save" ), showHiddenFiles ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
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
    boolean result = false;
    if ( StringUtils.isEmpty( ext ) ) {
      result = fileChooser.doesSelectedFileExist();
    } else {
      result = fileChooser.doesSelectedFileExist( ext );
    }
    return result;
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
    if ( actualFileName != null && !"".equals( actualFileName ) ) { //$NON-NLS-1$
      return actualFileName;
    } else {
      return "";
    }
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

  @Override
  public void hide() {
    GlassPane.getInstance().hide();
    super.hide();
  }

  @Override
  public void center() {
    super.center();
    setFocus();
  }

  private void showGlassPane() {
    GlassPane.getInstance().show();
    super.initializePageBackground();
    super.block();
  }

  private void setFocus() {
    fileChooser.fileNameTextBox.setFocus( true );
  }

  public boolean isSubmitOnEnter() {
    return submitOnEnter;
  }

  public void setSubmitOnEnter( boolean submitOnEnter ) {
    this.submitOnEnter = submitOnEnter;
    fileChooser.setSubmitOnEnter( submitOnEnter );
  }

  @Override
  public boolean onKeyDownPreview( char key, int modifiers ) {
    // Use the popup's key preview hooks to close the dialog when either
    // enter or escape is pressed.
    switch ( key ) {
      case KeyboardListener.KEY_ENTER:
        if ( isSubmitOnEnter() ) {
          onOk();
        }
        break;
      case KeyboardListener.KEY_ESCAPE:
        onCancel();
        break;
    }
    return true;
  }
}
