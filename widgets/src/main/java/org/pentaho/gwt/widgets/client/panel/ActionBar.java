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
