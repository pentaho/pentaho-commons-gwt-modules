/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.gwt.widgets.client.filechooser;

import java.util.EventListener;

public interface FileChooserListener extends EventListener {
  public void fileSelected( RepositoryFile file, String filePath, String fileName, String title );

  public void fileSelectionChanged( RepositoryFile file, String filePath, String fileName, String title );

  public void dialogCanceled();
}
