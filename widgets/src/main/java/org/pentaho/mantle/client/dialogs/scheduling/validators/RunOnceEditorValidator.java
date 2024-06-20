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
 * Copyright (c) 2002-2024 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.mantle.client.dialogs.scheduling.validators;

import com.google.gwt.i18n.client.DateTimeFormat;
import org.pentaho.gwt.widgets.client.utils.TimeUtil;
import org.pentaho.mantle.client.dialogs.scheduling.RunOnceEditor;

import java.util.Date;

public class RunOnceEditorValidator implements IUiValidator {

  private RunOnceEditor editor = null;

  public RunOnceEditorValidator( RunOnceEditor editor ) {
    this.editor = editor;
  }

  public boolean isValid() {
    boolean isValid = true;
    if ( null == editor.getStartDate() ) {
      isValid = false;
    } else {

      final DateTimeFormat format = DateTimeFormat.getFormat( "MM-dd-yyyy" );

      //BISERVER-14912 - Date.before() does not work as expected in GWT, so we need a custom validation to check the day
      if ( isBefore( editor.getStartDate(), new Date() ) ) {
        isValid = false;
      } else if ( isBefore( new Date() , editor.getStartDate()) ) {
        //if the date is after today
        isValid = true;
      } else {
        //here we are validating current day
        String time = editor.getStartTime();  //format of time is "hh:mm:ss a"



        time = normalizeTime( time );

        if (isBefore( time, new Date() )) {
          isValid = false;
        }
      }
    }
    return isValid;
  }

  private boolean isBefore( String timeOfDay, Date date ) {

    String[] blocks = timeOfDay.split( ":" );
    Integer hours =  Integer.parseInt( blocks[0]);
    Integer minutes =  Integer.parseInt( blocks[1]);
    Integer seconds =  Integer.parseInt( blocks[2].substring( 0,2 ));

    if(timeOfDay.endsWith( "pm" ) || timeOfDay.endsWith( "PM" )) {
      hours = hours + 12 ; //am pm taken care of
    }

    int secondsTime = hours * 3600 + minutes * 60 + seconds;
    int secondsDateNow = date.getHours() * 3600 + date.getMinutes() * 60 + date.getSeconds();

    return secondsTime < secondsDateNow;
  }

  private static boolean isBefore( Date a, Date b ) {
    if ( a.getYear() < b.getYear() ) {
      return true;
    } else if ( a.getYear() == b.getYear() && a.getMonth() < b.getMonth() ) {
      return true;
    } else if ( a.getYear() == b.getYear() && a.getMonth() == b.getMonth() ) {
      return a.getDate() < b.getDate();
    }
    return false;
  }

  public void clear() {
  }

  private String normalizeTime( String time ) {
    if ( time == null || time.isEmpty() ) {
      return null;
    }
    if ( time.endsWith( TimeUtil.TimeOfDay.AM.toString() ) ) {
      time = time.replace( TimeUtil.TimeOfDay.AM.toString(), "am" );
    } else if ( time.endsWith( TimeUtil.TimeOfDay.PM.toString() ) ) {
      time = time.replace( TimeUtil.TimeOfDay.PM.toString(), "pm" );
    }
    return time;
  }
}
