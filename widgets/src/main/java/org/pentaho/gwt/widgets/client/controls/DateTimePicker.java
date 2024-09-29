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
