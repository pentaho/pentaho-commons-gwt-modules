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
 * Copyright (c) 2021-2023 Hitachi Vantara. All rights reserved.
 */

package org.pentaho.mantle.client.dialogs.scheduling;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import org.pentaho.mantle.client.workspace.JsJob;

import java.util.ArrayList;
import java.util.List;

public class ScheduleParamsHelper {

  public static final String AUTO_CREATE_UNIQUE_FILENAME_KEY = "autoCreateUniqueFilename";
  public static final String APPEND_DATE_FORMAT_KEY = "appendDateFormat";
  public static final String OVERWRITE_FILE_KEY = "overwriteFile";
  public static final String ACTION_USER_KEY = "ActionAdapterQuartzJob-ActionUser";
  public static final String JOB_PARAMETERS_KEY = "jobParameters";

  private ScheduleParamsHelper() { }

  public static JSONObject buildScheduleParam( String name, String value, String type ) {
    JsArrayString paramValue = JavaScriptObject.createArray().cast();
    paramValue.push( value );

    JsSchedulingParameter param = JavaScriptObject.createObject().cast();
    param.setName( name );
    param.setType( type );
    param.setStringValue( paramValue );

    return new JSONObject( param );
  }

  public static JSONArray getScheduleParams( JSONObject jobSchedule ) {
    List<JSONObject> schedulingParams = new ArrayList<>();
    JSONArray jobParameters = (JSONArray) jobSchedule.get( JOB_PARAMETERS_KEY );
    if ( jobParameters != null ) {
      for ( int i = 0; i < jobParameters.size(); i++ ) {
        schedulingParams.add( (JSONObject) jobParameters.get( i ) );
      }
    }

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

      params.set( params.size(), buildScheduleParam( APPEND_DATE_FORMAT_KEY, dateFormat, "string" ) );
    }

    if ( jobSchedule.get( OVERWRITE_FILE_KEY ) != null ) {
      String overwriteFile = jobSchedule.get( OVERWRITE_FILE_KEY ).toString();
      overwriteFile = overwriteFile.substring( 1, overwriteFile.length() - 1 );

      boolean overwrite = Boolean.parseBoolean( overwriteFile );
      if ( overwrite ) {
        params.set( params.size(), buildScheduleParam( AUTO_CREATE_UNIQUE_FILENAME_KEY, "false",  "boolean" ) );
      }
    }

    return params;
  }

  public static JSONObject generateLineageId( JsJob job ) {
    return buildScheduleParam( "lineage-id", job.getJobParamValue( "lineage-id" ), "string" );
  }

  public static JSONObject generateActionUser( JsJob job ) {
    String actionUser = job.getJobParamValue( ACTION_USER_KEY );

    return buildScheduleParam( ACTION_USER_KEY, actionUser, "string" );
  }
}
