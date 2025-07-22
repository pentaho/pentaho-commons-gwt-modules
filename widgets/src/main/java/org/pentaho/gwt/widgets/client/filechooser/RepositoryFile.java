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

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONBoolean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * JAXB-safe version of {@code RepositoryFile}. ({@code RepositoryFile} has no zero-arg constructor and no public
 * mutators.)
 *
 * @see RepositoryFileAdapter
 *
 * @author mlowery
 */

public class RepositoryFile implements Serializable {

  private static final long serialVersionUID = 1271027425735179424L;

  public RepositoryFile() {
    super();
  }

  protected String name;
  protected String id;
  protected Date createdDate;
  protected Date lastModifiedDate;
  protected boolean folder;
  protected String path;
  protected boolean hidden;
  protected boolean versioned;
  protected String versionId;
  protected boolean locked;
  protected String lockOwner;
  protected String lockMessage;
  protected Date lockDate;
  protected long fileSize;
  protected String owner;
  protected int ownerType = -1;
  protected String title;
  protected String description;
  protected String locale;
  protected String originalParentFolderPath;
  protected String originalParentFolderId;
  protected Date deletedDate;
  protected List<LocalizedStringMapEntry> titleMapEntries;
  protected List<LocalizedStringMapEntry> descriptionMapEntries;
  protected String creatorId;

  /**
   * This Constructor will create the object from a JsonObject
   * 
   * @param jso
   *          a JSON String representing the RepositoryFile Object
   */
  public RepositoryFile( JSONObject jso ) {
    super();
    JSONObject rftdo = jso.isObject();
    // There are times the web service wraps the json RepositoryFile with repsitoryFileDto and times when it does not.
    // The following line mitigates this so it doesn't care what form it is getting.
    JSONObject repositoryFileJSON =
        rftdo.get( "repositoryFileDto" ) != null ? rftdo.get( "repositoryFileDto" ).isObject() : jso;

    folder = JSONValueToBoolean( repositoryFileJSON, "folder" );
    creatorId = JSONValueToString( repositoryFileJSON, "creatorId" );
    this.createdDate = JSONValueToDate( repositoryFileJSON, "createdDate" );
    this.fileSize = JSONValueToLong( repositoryFileJSON, "fileSize" );
    this.hidden = JSONValueToBoolean( repositoryFileJSON, "hidden" );
    this.id = JSONValueToString( repositoryFileJSON, "id" );
    this.locale = JSONValueToString( repositoryFileJSON, "locale" );
    this.locked = JSONValueToBoolean( repositoryFileJSON, "locked" );
    this.name = JSONValueToString( repositoryFileJSON, "name" );
    this.ownerType = JSONValueToInt( repositoryFileJSON, "ownerType" );
    this.path = JSONValueToString( repositoryFileJSON, "path" );
    this.title = JSONValueToString( repositoryFileJSON, "title" );
    this.versionId = JSONValueToString( repositoryFileJSON, "versionId" );
    this.description = JSONValueToString( repositoryFileJSON, "description" );
    if ( !folder ) {
      this.deletedDate = JSONValueToDate( repositoryFileJSON, "deletedDate" );
      this.lastModifiedDate = JSONValueToDate( repositoryFileJSON, "lastModifiedDate" );
      this.lockDate = JSONValueToDate( repositoryFileJSON, "lockDate" );
      this.lockMessage = JSONValueToString( repositoryFileJSON, "lockMessage" );
      this.lockOwner = JSONValueToString( repositoryFileJSON, "lockOwner" );
      this.originalParentFolderId = JSONValueToString( repositoryFileJSON, "originalParentFolderId" );
      this.originalParentFolderPath = JSONValueToString( repositoryFileJSON, "originalParentFolderPath" );
      this.owner = JSONValueToString( repositoryFileJSON, "owner" );
      this.versioned = JSONValueToBoolean( repositoryFileJSON, "versioned" );
    }
  }

  private static String JSONValueToString( JSONObject jso, String fieldName ) {
    JSONValue temp = jso.get( fieldName );
    if ( temp instanceof JSONString ) {
      return temp.isString().stringValue();
    }
    return null;
  }

  private static Date JSONValueToDate( JSONObject jso, String fieldName ) {
    JSONValue temp = jso.get( fieldName );
    if ( temp instanceof  JSONString ) {
      return parseDateTime( temp.isString().stringValue() );
    }
    return null;
  }

  private static long JSONValueToLong( JSONObject jso, String fieldName ) {
    JSONValue temp = jso.get( fieldName );
    if ( temp instanceof JSONString ) {
      return Long.valueOf( temp.isString().stringValue() );
    } else if ( temp instanceof JSONNumber ) {
      return Long.valueOf( temp.isNumber().toString() );
    }
    return 0;
  }

  private static int JSONValueToInt( JSONObject jso, String fieldName ) {
    JSONValue temp = jso.get( fieldName );
    if ( temp instanceof JSONString ) {
      return Integer.valueOf( temp.isString().stringValue() );
    } else if ( temp instanceof JSONNumber ) {
      return Integer.valueOf( temp.isNumber().toString() );
    }
    return 0;
  }

  private static boolean JSONValueToBoolean( JSONObject jso, String fieldName ) {
    JSONValue temp = jso.get( fieldName );
    if ( temp instanceof JSONString ) {
      return Boolean.valueOf( temp.isString().stringValue() );
    } else if ( temp instanceof JSONBoolean ) {
      return temp.isBoolean().booleanValue();
    }
    return false;
  }

  private static Date parseDateTime( String dateTimeString ) {
    // parse the date
    Date date = null;
    try {
      // date = DateTimeFormat.getFormat("MM/dd/yyyy HH:mm:ss").parse(dateTimeString);
      Long timeInMillis = Long.parseLong( dateTimeString );
      date = new Date( timeInMillis );
    } catch ( Exception e ) {
      return null;
    }
    return date;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate( Date createdDate ) {
    this.createdDate = createdDate;
  }

  public Date getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate( Date lastModifiedDate ) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public boolean isFolder() {
    return folder;
  }

  public void setFolder( boolean folder ) {
    this.folder = folder;
  }

  public String getPath() {
    return path;
  }

  public void setPath( String path ) {
    this.path = path;
  }

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden( boolean hidden ) {
    this.hidden = hidden;
  }

  public boolean isVersioned() {
    return versioned;
  }

  public void setVersioned( boolean versioned ) {
    this.versioned = versioned;
  }

  public String getVersionId() {
    return versionId;
  }

  public void setVersionId( String versionId ) {
    this.versionId = versionId;
  }

  public boolean isLocked() {
    return locked;
  }

  public void setLocked( boolean locked ) {
    this.locked = locked;
  }

  public String getLockOwner() {
    return lockOwner;
  }

  public void setLockOwner( String lockOwner ) {
    this.lockOwner = lockOwner;
  }

  public String getLockMessage() {
    return lockMessage;
  }

  public void setLockMessage( String lockMessage ) {
    this.lockMessage = lockMessage;
  }

  public Date getLockDate() {
    return lockDate;
  }

  public void setLockDate( Date lockDate ) {
    this.lockDate = lockDate;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner( String owner ) {
    this.owner = owner;
  }

  public long getFileSize() {
    return fileSize;
  }

  public void setFileSize( long fileSize ) {
    this.fileSize = fileSize;
  }

  public int getOwnerType() {
    return ownerType;
  }

  public void setOwnerType( int ownerType ) {
    this.ownerType = ownerType;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle( String title ) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale( String locale ) {
    this.locale = locale;
  }

  public String getOriginalParentFolderPath() {
    return originalParentFolderPath;
  }

  public void setOriginalParentFolderPath( String originalParentFolderPath ) {
    this.originalParentFolderPath = originalParentFolderPath;
  }

  public String getOriginalParentFolderId() {
    return originalParentFolderId;
  }

  public void setOriginalParentFolderId( String originalParentFolderId ) {
    this.originalParentFolderId = originalParentFolderId;
  }

  public Date getDeletedDate() {
    return deletedDate;
  }

  public void setDeletedDate( Date deletedDate ) {
    this.deletedDate = deletedDate;
  }

  public List<LocalizedStringMapEntry> getTitleMapEntries() {
    return titleMapEntries;
  }

  public void setTitleMapEntries( List<LocalizedStringMapEntry> titleMapEntries ) {
    this.titleMapEntries = titleMapEntries;
  }

  public List<LocalizedStringMapEntry> getDescriptionMapEntries() {
    return descriptionMapEntries;
  }

  public void setDescriptionMapEntries( List<LocalizedStringMapEntry> descriptionMapEntries ) {
    this.descriptionMapEntries = descriptionMapEntries;
  }
}
