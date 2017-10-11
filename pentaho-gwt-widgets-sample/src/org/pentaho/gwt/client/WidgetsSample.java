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

package org.pentaho.gwt.client;

import java.util.EnumSet;

import org.pentaho.gwt.widgets.client.containers.SimpleGroupBox;
import org.pentaho.gwt.widgets.client.controls.ErrorLabel;
import org.pentaho.gwt.widgets.client.controls.TimePicker;
import org.pentaho.gwt.widgets.client.controls.schededitor.RecurrenceEditor;
import org.pentaho.gwt.widgets.client.controls.schededitor.ScheduleEditor;
import org.pentaho.gwt.widgets.client.ui.ICallback;
import org.pentaho.gwt.widgets.client.ui.IChangeHandler;
import org.pentaho.gwt.widgets.client.utils.EnumException;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WidgetsSample implements EntryPoint {

  private RecurrenceEditor recurrenceEditor = null;
  private ListBox recurrenceCombo = null;
  
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    
    Panel root = RootPanel.get();
    
    Label l = new Label( "Time Picker: " );
    ErrorLabel el = new ErrorLabel( l );
    root.add(el);
    
    TimePicker tp = new TimePicker();
    root.add(tp);
    
    SimpleGroupBox gb = new SimpleGroupBox( "ScheduleEditor Sample");
    ScheduleEditor se = new ScheduleEditor();
    ICallback<IChangeHandler> chHandler = new ICallback<IChangeHandler>() {
      public void onHandle(IChangeHandler se ) {
//        MessageDialogBox d = new MessageDialogBox( "got it", "you changed something", false, true, true );
//        d.show();
        System.out.println( "something changed" );
      }
    };
    se.setOnChangeHandler( chHandler );
    gb.add( se );
    root.add(gb);

    SimpleGroupBox recurrenceGb = new SimpleGroupBox( "Recurrence Editor" );
    recurrenceCombo = createRecurrenceCombo();
    recurrenceGb.add( recurrenceCombo );
    recurrenceEditor = new RecurrenceEditor();
    recurrenceGb.add( recurrenceEditor );
    
    root.add(recurrenceGb);
  }
  
  private ListBox createRecurrenceCombo() {
    final WidgetsSample localThis = this;
    ListBox lb = new ListBox();
    lb.setVisibleItemCount( 1 );
    lb.setStyleName("scheduleCombo"); //$NON-NLS-1$
    lb.addChangeListener( new ChangeListener() {
      public void onChange(Widget sender) {
        localThis.handleRecurrenceChange();
      }
    });
    // add all schedule types to the combobox
    for (RecurrenceEditor.TemporalValue reType : EnumSet.range(RecurrenceEditor.TemporalValue.SECONDS, RecurrenceEditor.TemporalValue.YEARLY)) {
      lb.addItem( reType.toString() );
    }
    lb.setItemSelected( 0, true );

    return lb;
  }

  private void handleRecurrenceChange() throws EnumException {
    RecurrenceEditor.TemporalValue reType = RecurrenceEditor.TemporalValue.get( recurrenceCombo.getSelectedIndex() );
    recurrenceEditor.setTemporalState( reType );
  }
}
