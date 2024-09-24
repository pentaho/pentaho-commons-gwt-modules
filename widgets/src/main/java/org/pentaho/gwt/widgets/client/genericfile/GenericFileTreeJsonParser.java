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

package org.pentaho.gwt.widgets.client.genericfile;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * Parses a JSON string into a {@link GenericFileTree} object.
 */
public class GenericFileTreeJsonParser {
  @NonNull
  private final String jsonText;

  public GenericFileTreeJsonParser( @NonNull String jsonText ) {
    Objects.requireNonNull( jsonText );
    this.jsonText = jsonText;
  }

  /**
   * Parses the file tree JSON and returns the corresponding file tree object.
   *
   * @return The file tree object.
   */
  @NonNull
  public GenericFileTree getTree() {
    JSONObject jsonFileTree = JSONParser.parseStrict( jsonText ).isObject();
    return parseFileTree( jsonFileTree );
  }

  @NonNull
  private static GenericFileTree parseFileTree( @NonNull JSONObject jsonFileTree ) {

    GenericFile file = parseFile( getFieldValueAsJSONObject( jsonFileTree, "file" ) );

    GenericFileTree fileTree = new GenericFileTree( file );

    JSONArray jsonChildren = getFieldValueAsJSONArray( jsonFileTree, "children" );
    if ( jsonChildren != null ) {
      // Make sure that an empty list is reflected.
      fileTree.setChildren( new ArrayList<>() );

      for ( int i = 0; i < jsonChildren.size(); i++ ) {
        fileTree.addChild( parseFileTree( jsonChildren.get( i ).isObject() ) );
      }
    }

    return fileTree;
  }

  @NonNull
  private static GenericFile parseFile( @NonNull JSONObject jsonFile ) {

    GenericFile file = new GenericFile();

    // TODO: check this is needed.
    // There are times the web service wraps the json RepositoryFile with `file` and times when it does not.
    // The following line mitigates this, so it doesn't care what form it is getting.
    JSONObject fileJSON = jsonFile.get( "file" ) != null ? jsonFile.get( "file" ).isObject() : jsonFile;

    file.setProvider( getFieldValueAsString( fileJSON, "provider" ) );
    file.setName( getFieldValueAsString( fileJSON, "name" ) );
    file.setNameDecoded( getFieldValueAsString( fileJSON, "nameDecoded" ) );
    file.setTitle( getFieldValueAsString( fileJSON, "title" ) );
    file.setDescription( getFieldValueAsString( fileJSON, "description" ) );
    file.setPath( getFieldValueAsString( fileJSON, "path" ) );
    file.setParentPath( getFieldValueAsString( fileJSON, "parentPath" ) );
    file.setType( getFieldValueAsString( fileJSON, "type" ) );
    file.setModifiedDate( getFieldValueAsDate( fileJSON, "modifiedDate" ) );
    file.setHidden( getFieldValueAsBoolean( fileJSON, "hidden", false ) );

    if ( file.isFolder() ) {
      file.setCanAddChildren( getFieldValueAsBoolean( fileJSON, "canAddChildren", false ) );
    }

    return file;
  }

  @Nullable
  private static JSONObject getFieldValueAsJSONObject( @NonNull JSONObject jso, @NonNull String fieldName ) {
    JSONValue jsonValue = jso.get( fieldName );
    return jsonValue != null ? jsonValue.isObject() : null;
  }

  @Nullable
  private static JSONArray getFieldValueAsJSONArray( @NonNull JSONObject jsonFileTree, @NonNull String fieldName ) {
    JSONValue jsonValue = jsonFileTree.get( fieldName );
    return jsonValue != null ? jsonValue.isArray() : null;
  }

  @Nullable
  private static String getFieldValueAsString( @NonNull JSONObject jso, @NonNull String fieldName ) {
    JSONValue jsonValue = jso.get( fieldName );
    return jsonValue != null && jsonValue.isString() != null
      ? jsonValue.isString().stringValue()
      : null;
  }

  @Nullable
  private static Long getFieldValueAsLong( @NonNull JSONObject jso, @NonNull String fieldName ) {
    JSONValue jsonValue = jso.get( fieldName );
    return jsonValue != null && jsonValue.isNumber() != null
      ? (long) jsonValue.isNumber().doubleValue()
      : null;
  }

  @Nullable
  private static Date getFieldValueAsDate( @NonNull JSONObject jso, @NonNull String fieldName ) {
    Long value = getFieldValueAsLong( jso, fieldName );
    return value != null ? new Date( value ) : null;
  }

  private static boolean getFieldValueAsBoolean( @NonNull JSONObject jso, @NonNull String fieldName,
                                                 boolean defaultValue ) {
    JSONValue jsonValue = jso.get( fieldName );
    return jsonValue != null && jsonValue.isBoolean() != null
      ? jsonValue.isBoolean().booleanValue()
      : defaultValue;
  }
}
