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

import java.io.Serializable;
import java.util.List;

public class RepositoryFileTree implements Serializable {

  private static final long serialVersionUID = -7120153935975907226L;

  RepositoryFile file;

  List<RepositoryFileTree> children;

  public RepositoryFile getFile() {
    return file;
  }

  public void setFile( RepositoryFile file ) {
    this.file = file;
  }

  public List<RepositoryFileTree> getChildren() {
    return children;
  }

  public void setChildren( List<RepositoryFileTree> children ) {
    this.children = children;
  }
}
