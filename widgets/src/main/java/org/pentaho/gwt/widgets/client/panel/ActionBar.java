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

package org.pentaho.gwt.widgets.client.panel;

import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.adamtacy.client.ui.effects.core.NMorphStyle;
import org.adamtacy.client.ui.effects.impl.css.Rule;

public class ActionBar extends SimplePanel {

  public static final int DEFAULT_HEIGHT = 50;
  public static final double DURATION = 0.5;

  public enum State {
    EXPAND, COLLAPSE
  }

  protected HorizontalPanel buttonPanel;
  NMorphStyle collapseEffect;
  NMorphStyle expandEffect;
  int height = -1;
  protected State state;

  public ActionBar() {
    buttonPanel = new HorizontalPanel();
    setStylePrimaryName( "action-bar" );
    add( buttonPanel );
    buttonPanel.setHeight( "100%" );
    buttonPanel.setWidth( "100%" );
    buttonPanel.setStylePrimaryName( "action-button-bar" );
  }

  public void addWidget( Widget widget, HorizontalAlignmentConstant align ) {
    buttonPanel.add( widget );
    buttonPanel.setCellHorizontalAlignment( widget, align );
    buttonPanel.setCellVerticalAlignment( widget, HorizontalPanel.ALIGN_MIDDLE );
  }

  public void collapse( int delay ) {
    if ( state != State.COLLAPSE ) {
      height = this.getOffsetHeight();
      if ( height <= 0 ) {
        height = DEFAULT_HEIGHT;
      }
      collapseEffect =
          new NMorphStyle( new Rule( "start{height:" + ( height ) + "px;}" ), new Rule( "end{height: 0px;}" ) );
      collapseEffect.setEffectElement( this.getElement() );
      collapseEffect.setDuration( DURATION );
      collapseEffect.play( delay );
      setState( State.COLLAPSE );
    }
  }

  public void expand( int delay ) {
    if ( state != State.EXPAND ) {
      height = this.getOffsetHeight();
      if ( height <= 0 ) {
        height = DEFAULT_HEIGHT;
      }
      expandEffect =
          new NMorphStyle( new Rule( "start{height: 0px;}" ), new Rule( "end{height:" + ( height ) + "px;}" ) );
      expandEffect.setEffectElement( this.getElement() );
      expandEffect.setDuration( DURATION );
      expandEffect.play( delay );
      setState( State.EXPAND );
    }
  }

  public State getState() {
    return state;
  }

  public void setState( State state ) {
    this.state = state;
  }

}
