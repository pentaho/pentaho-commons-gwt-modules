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

package org.pentaho.gwt.widgets.client.text;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings( "deprecation" )
public class ToolTip extends PopupPanel implements MouseListener {

  private Timer timer = new Timer() {
    public void run() {
      PositionCallback callback = new PositionCallback() {

        @Override
        public void setPosition( int offsetWidth, int offsetHeight ) {

          if ( sender != null ) {
            int clientWidth = Window.getClientWidth();

            int left = sender.getAbsoluteLeft();
            int top = sender.getAbsoluteTop();
            int width = sender.getOffsetWidth();
            int height = sender.getOffsetHeight();

            int topPos = top + height + 2;
            int leftPos = left + width - 3;

            if ( leftPos + offsetWidth > clientWidth ) {
              setPopupPosition( leftPos - offsetWidth, topPos );
            } else {
              setPopupPosition( leftPos, topPos );
            }
          }
        }
      };
      setPopupPositionAndShow( callback );
    }
  };
  private int delay;
  private Widget sender;

  public ToolTip( String message, int delay ) {
    super( true );
    Label lbl = new Label( message );
    lbl.setStyleName( "tooltip-label" ); //$NON-NLS-1$
    setWidget( lbl );
    this.delay = delay;
    this.setStyleName( "tooltip" ); //$NON-NLS-1$
  }

  public void onMouseDown( Widget sender, int x, int y ) {
    timer.cancel();
    hide();
  }

  public void onMouseEnter( final Widget sender ) {
    timer.schedule( delay );
    this.sender = sender;
  }

  public void onMouseLeave( Widget sender ) {
    timer.cancel();
    hide();
  }

  public void onMouseMove( Widget sender, int x, int y ) {
  }

  public void onMouseUp( Widget sender, int x, int y ) {
  }

}
