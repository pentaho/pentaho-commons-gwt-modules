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

package org.pentaho.gwt.widgets.client.filechooser;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import org.pentaho.gwt.widgets.client.filechooser.FileChooser.FileChooserMode;
import org.pentaho.gwt.widgets.client.utils.i18n.IResourceBundleLoadCallback;
import org.pentaho.gwt.widgets.client.utils.i18n.ResourceBundle;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;

public class FileChooserEntryPoint implements EntryPoint, IResourceBundleLoadCallback {

  public static ResourceBundle messages = null;

  public void onModuleLoad() {
    if ( messages == null ) {
      String localeName = getSessionLocaleName();
      if ( StringUtils.isEmpty( localeName ) ) {
        messages = new ResourceBundle();
      } else {
        messages = new ResourceBundle( localeName );
      }
    }
    messages
        .loadBundle( GWT.getModuleBaseURL() + "messages/", "filechooser_messages", true, FileChooserEntryPoint.this );
  }

  public void bundleLoaded( String bundleName ) {
    setupNativeHooks( this );
  }

  public native void notifyCallback( JavaScriptObject callback, RepositoryFile file, String filePath, String fileName,
      String title )
  /*-{
   try {
     callback.fileSelected(file, filePath, fileName, title);
   } catch (ex) {
   }
  }-*/;

  public native void notifyCallbackCanceled( JavaScriptObject callback )
  /*-{
   try {
     callback.dialogCanceled();
   } catch (ex) {
     alert(ex);
   }
  }-*/;

  public void openFileChooserDialog( final JavaScriptObject callback, String selectedPath ) {
    FileChooserDialog dialog = new FileChooserDialog( FileChooserMode.OPEN, selectedPath, false, true );
    addFileChooserListener( dialog, callback );
  }

  public void openFolderChooserDialog( final JavaScriptObject callback, String selectedPath ) {
    FileChooserDialog dialog = new FileChooserDialog( FileChooserMode.OPEN, selectedPath, false, true );
    addFileChooserListener( dialog, callback );

    dialog.setFileFilter( new FileFilter() {
      @Override
      public boolean accept( String name, boolean isDirectory, boolean isVisible ) {
        return isDirectory;
      }
    } );
  }

  public void saveFileChooserDialog( final JavaScriptObject callback, String selectedPath ) {
    FileChooserDialog dialog = null;
    if ( selectedPath == null ) {
      // Loads whole repository from top level
      dialog = new FileChooserDialog( FileChooserMode.SAVE, selectedPath, false, true );
    } else {
      // Loads repository just at the selectedPath
      dialog = new FileChooserDialog( FileChooserMode.SAVE, selectedPath, null, false, true );
    }
    addFileChooserListener( dialog, callback );
  }

  public void saveAsFileChooserDialog( final JavaScriptObject callback, String selectedPath ) {
    FileChooserDialog dialog = null;
    if ( selectedPath == null ) {
      // Loads whole repository from top level
      dialog = new FileChooserDialog( FileChooserMode.SAVE, selectedPath, false, true,
          messages.getString( "SaveAs" ), messages.getString( "Save" ) );
    } else {
      // Loads repository just at the selectedPath
      dialog = new FileChooserDialog( FileChooserMode.SAVE, selectedPath, null, false, true,
          messages.getString( "SaveAs" ), messages.getString( "Save" ) );
    }
    addFileChooserListener( dialog, callback );
  }

  private void addFileChooserListener( FileChooserDialog dialog, final JavaScriptObject callback ) {
    dialog.addFileChooserListener( new FileChooserListener() {
      public void fileSelected( RepositoryFile file, String filePath, String fileName, String title ) {
        notifyCallback( callback, file, filePath, fileName, title );
      }

      public void fileSelectionChanged( RepositoryFile file, String filePath, String fileName, String title ) {
      }

      public void dialogCanceled() {
        notifyCallbackCanceled( callback );
      }
    } );
  }

  public static native String getSessionLocaleName()
  /*-{
        return (typeof $wnd.SESSION_LOCALE === 'undefined' || $wnd.SESSION_LOCALE === null) ? '' : $wnd.SESSION_LOCALE;
  }-*/;

  public native void setupNativeHooks( FileChooserEntryPoint fileChooserEntryPoint )
  /*-{
    $wnd.openFileChooserDialog = function(callback, selectedPath) {
      fileChooserEntryPoint.@org.pentaho.gwt.widgets.client.filechooser.FileChooserEntryPoint::openFileChooserDialog(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(callback, selectedPath);
    }
    $wnd.openFolderChooserDialog = function(callback, selectedPath) {
      fileChooserEntryPoint.@org.pentaho.gwt.widgets.client.filechooser.FileChooserEntryPoint::openFolderChooserDialog(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(callback, selectedPath);
    }
    $wnd.saveFileChooserDialog = function(callback, selectedPath) {
      fileChooserEntryPoint.@org.pentaho.gwt.widgets.client.filechooser.FileChooserEntryPoint::saveFileChooserDialog(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(callback, selectedPath);
    }
    $wnd.saveAsFileChooserDialog = function(callback, selectedPath) {
      fileChooserEntryPoint.@org.pentaho.gwt.widgets.client.filechooser.FileChooserEntryPoint::saveAsFileChooserDialog(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(callback, selectedPath);
    }
  }-*/;

}
