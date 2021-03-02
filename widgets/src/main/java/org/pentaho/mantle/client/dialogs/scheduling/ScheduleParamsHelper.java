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
 * Copyright (c) 2021 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.mantle.client.dialogs.scheduling;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ScheduleParamsHelper {

  public static final String AUTO_CREATE_UNIQUE_FILENAME_KEY = "autoCreateUniqueFilename";

  private ScheduleParamsHelper() { }

  private static final String APPEND_DATE_FORMAT_KEY = "appendDateFormat";
  private static final String OVERWRITE_FILE_KEY = "overwriteFile";

  public static JSONArray getScheduleParams( JSONObject jobSchedule ) {
    List<JSONObject> schedulingParams = new ArrayList<JSONObject>();
    return getScheduleParams( jobSchedule, schedulingParams );
  }

  public static JSONArray getScheduleParams( JSONObject jobSchedule, List<JSONObject> schedulingParams ) {
    JSONArray params = new JSONArray();

    for ( int i = 0; i < schedulingParams.size(); i++ ) {
      params.set( i, schedulingParams.get( i ) );
    }

    if ( jobSchedule.get( APPEND_DATE_FORMAT_KEY ) != null ) {
      String dateFormat = jobSchedule.get( APPEND_DATE_FORMAT_KEY ).toString();
      dateFormat = dateFormat.substring( 1, dateFormat.length() - 1 ); // get rid of ""
      JsArrayString appendDateFormat = (JsArrayString) JavaScriptObject.createArray().cast();
      appendDateFormat.push( dateFormat );
      JsSchedulingParameter jspDateFormat = (JsSchedulingParameter) JavaScriptObject.createObject().cast();
      jspDateFormat.setName( APPEND_DATE_FORMAT_KEY );
      jspDateFormat.setStringValue( appendDateFormat );
      jspDateFormat.setType( "string" );

      params.set( params.size(), new JSONObject( jspDateFormat ) );
    }

    if ( jobSchedule.get( OVERWRITE_FILE_KEY ) != null ) {
      String overwriteFile = jobSchedule.get( OVERWRITE_FILE_KEY ).toString();
      overwriteFile = overwriteFile.substring( 1, overwriteFile.length() - 1 );
      boolean overwrite = Boolean.parseBoolean( overwriteFile );

      if ( overwrite ) {
        JsArrayString autoCreateUniqueFilenameValue = (JsArrayString) JavaScriptObject.createArray().cast();
        autoCreateUniqueFilenameValue.push( String.valueOf( !overwrite ) );

        JsSchedulingParameter jspOverwrite = (JsSchedulingParameter) JavaScriptObject.createObject().cast();
        jspOverwrite.setName( AUTO_CREATE_UNIQUE_FILENAME_KEY );
        jspOverwrite.setStringValue( autoCreateUniqueFilenameValue );
        jspOverwrite.setType( "boolean" );
        params.set( params.size(), new JSONObject( jspOverwrite ) );
      }
    }

    return params;
  }

  public static JSONObject generateLineageId( String lineageId ) {
    JsArrayString lineageIdValue = (JsArrayString) JavaScriptObject.createArray().cast();
    lineageIdValue.push( lineageId );
    JsSchedulingParameter p = (JsSchedulingParameter) JavaScriptObject.createObject().cast();
    p.setName( "lineage-id" );
    p.setType( "string" );
    p.setStringValue( lineageIdValue );

    return new JSONObject( p );
  }
}
