package org.pentaho.mantle.client.dialogs;

import org.pentaho.gwt.widgets.client.utils.i18n.IResourceBundleLoadCallback;
import org.pentaho.gwt.widgets.client.utils.i18n.ResourceBundle;
import org.pentaho.mantle.client.messages.Messages;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class SelectFolderDialogEntryPoint implements EntryPoint, IResourceBundleLoadCallback {

	@Override
	  public void onModuleLoad() {
	    ResourceBundle messages = new ResourceBundle();
	    Messages.setResourceBundle( messages );
	    messages.loadBundle(
	        GWT.getModuleBaseURL() + "messages/", "mantleMessages", true, SelectFolderDialogEntryPoint.this ); //$NON-NLS-1$ //$NON-NLS-2$
	  }

	  @Override
	  public void bundleLoaded( String bundleName ) {
	    setupNativeHooks( this );
	  }

	  public void openSelectFolderDialog() {
	    SelectFolderDialog dialog = new SelectFolderDialog();
	    dialog.center();
	  }

	  public native void setupNativeHooks( SelectFolderDialogEntryPoint selectFolderEntryPoint )
	  /*-{
	    $wnd.openSelectFolderDialog = function() {
	      selectFolderEntryPoint.@org.pentaho.mantle.client.dialogs.SelectFolderDialogEntryPoint::openSelectFolderDialog()();
	    }
	  }-*/;
	
}
