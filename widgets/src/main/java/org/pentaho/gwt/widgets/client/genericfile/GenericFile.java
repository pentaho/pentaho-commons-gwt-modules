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
 * Copyright (c) 2023 Hitachi Vantara. All rights reserved.
 */

package org.pentaho.gwt.widgets.client.genericfile;

import org.pentaho.gwt.widgets.client.utils.string.StringUtils;

import java.util.Date;

public class GenericFile {
  public static final String TYPE_FOLDER = "folder";
  public static final String TYPE_FILE = "file";

  private String name;
  private String title;
  private String description;
  private String path;
  private String parentPath;
  private String provider;
  private Date lastModifiedDate;

  /**
   * The type of file node. One of <code>file</code>, <code>folder</code>.
   */
  private String type;
  private boolean hidden;

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle( String title ) {
    this.title = title;
  }

  public String getTitleOrName() {
    return StringUtils.isEmpty( title ) ? name : title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
  }

  public String getPath() {
    return path;
  }

  public void setPath( String path ) {
    this.path = path;
  }

  public String getParentPath() {
    return parentPath;
  }

  public void setParentPath( String parentPath ) {
    this.parentPath = parentPath;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider( String provider ) {
    this.provider = provider;
  }

  public Date getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate( Date lastModifiedDate ) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public String getType() {
    return type;
  }

  public void setType( String type ) {
    this.type = type;
  }

  public boolean isFolder() {
    return TYPE_FOLDER.equals( this.type );
  }

  public boolean isFile() {
    return TYPE_FILE.equals( this.type );
  }

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden( boolean hidden ) {
    this.hidden = hidden;
  }
}
