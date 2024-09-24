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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.gwt.widgets.client.controls;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import org.pentaho.gwt.widgets.client.ui.ICallback;
import org.pentaho.gwt.widgets.client.ui.IChangeHandler;
import org.pentaho.gwt.widgets.client.utils.TimeUtil.TimeOfDay;

import java.util.Date;

/**
 * @author Steven Barkdull
 * 
 */

public class DateTimePicker extends FlowPanel implements IChangeHandler {
  private DefaultFormat format = new DefaultFormat( DateTimeFormat.getFormat( PredefinedFormat.DATE_SHORT ) );
  protected DatePickerEx datePicker = new DatePickerEx( format );
  protected TimePicker timePicker = new TimePicker();
  private ICallback<IChangeHandler> onChangeHandler = null;

  public enum Layout {
    HORIZONTAL, VERTICAL
  }

  public DateTimePicker( Layout layout ) {
    super();
    CellPanel p = ( Layout.HORIZONTAL == layout ) ? new HorizontalPanel() : new VerticalPanel();
    add( p );
    datePicker.getDatePicker().setWidth( "12ex" ); //$NON-NLS-1$
    p.add( datePicker.getDatePicker() );
    p.setCellVerticalAlignment( datePicker.getDatePicker(), HasVerticalAlignment.ALIGN_MIDDLE );
    // timePicker.setWidth( "100%" );
    p.add( timePicker );
    p.setCellVerticalAlignment( timePicker, HasVerticalAlignment.ALIGN_MIDDLE );
    configureOnChangeHandler();
  }

  public void setOnChangeHandler( ICallback<IChangeHandler> handler ) {
    this.onChangeHandler = handler;
  }

  private void changeHandler() {
    if ( null != onChangeHandler ) {
      onChangeHandler.onHandle( this );
    }
  }

  private void configureOnChangeHandler() {
    final DateTimePicker localThis = this;

    ICallback<IChangeHandler> handler = new ICallback<IChangeHandler>() {
      public void onHandle( IChangeHandler o ) {
        localThis.changeHandler();
      }
    };

    datePicker.setOnChangeHandler( handler );
    timePicker.setOnChangeHandler( handler );
  }

  private boolean enabled = true;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled( boolean enabled ) {
    this.enabled = enabled;
    datePicker.getDatePicker().setEnabled( enabled );
    timePicker.setEnabled( enabled );
  }

  @SuppressWarnings( "deprecation" )
  public Date getDate() {
    Date date = datePicker.getSelectedDate();
    if ( timePicker.getTimeOfDay().equals( TimeOfDay.AM ) ) {
      date.setHours( Integer.parseInt( timePicker.getHour() ) );
    } else {
      date.setHours( Integer.parseInt( timePicker.getHour() ) + 12 );
    }
    date.setMinutes( Integer.parseInt( timePicker.getMinute() ) );
    return date;
  }

}
