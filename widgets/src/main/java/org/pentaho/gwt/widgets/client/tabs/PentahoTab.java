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


package org.pentaho.gwt.widgets.client.tabs;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.FocusImpl;
import org.pentaho.gwt.widgets.client.utils.ImageUtil;

import com.google.gwt.user.client.Event;

public class PentahoTab extends SimplePanel implements Focusable {

  public static final String SELECTED = "selected";
  private PentahoTabPanel tabPanel;
  private Widget content;
  protected Label label = new Label();
  private boolean solutionBrowserShowing;
  private boolean closeable;

  public boolean isCloseable() {
    return closeable;
  }

  private static final FocusImpl focusImpl = FocusImpl.getFocusImplForWidget();

  public PentahoTab( String text, String tooltip, PentahoTabPanel tabPanel, Widget content, boolean closeable ) {
    this.content = content;
    this.tabPanel = tabPanel;
    setStylePrimaryName( "pentaho-tabWidget" );
    Roles.getButtonRole().set( getElement() );
    sinkEvents( Event.ONDBLCLICK | Event.ONMOUSEUP | Event.ONKEYDOWN );

    if ( closeable ) {
      this.closeable = true;
      final Image closeTabImage =
              ImageUtil
                      .getThemeableImage( "pentaho-tabWidget-close", "pentaho-closebutton", "pentaho-imagebutton-disabled" );
      closeTabImage.addClickHandler( new ClickHandler() {
        public void onClick( ClickEvent event ) {
          event.getNativeEvent().stopPropagation();
          closeTab();
        }
      } );
      closeTabImage.addMouseOverHandler( new MouseOverHandler() {
        public void onMouseOver( MouseOverEvent event ) {
          closeTabImage.removeStyleName( "pentaho-imagebutton-disabled" );
          closeTabImage.addStyleName( "pentaho-imagebutton-hover" );
        }
      } );
      closeTabImage.addMouseOutHandler( new MouseOutHandler() {

        public void onMouseOut( MouseOutEvent event ) {
          closeTabImage.removeStyleName( "pentaho-imagebutton-hover" );
          closeTabImage.addStyleName( "pentaho-imagebutton-disabled" );
        }
      } );

      HorizontalPanel p = new HorizontalPanel();
      setupLabel( text, tooltip );
      p.add( label );
      p.add( closeTabImage );
      setWidget( p );
    } else {
      setupLabel( text, tooltip );
      setWidget( label );
    }
  }

  public void setupLabel( String text, String tooltip ) {
    label.setText( text );
    label.setTitle( tooltip );
    label.setStylePrimaryName( "pentaho-tabWidgetLabel" );
  }

  public Widget getContent() {
    return content;
  }

  public void setContent( Widget content ) {
    this.content = content;
  }

  protected PentahoTabPanel getTabPanel() {
    return tabPanel;
  }

  protected void setTabPanel( PentahoTabPanel tabPanel ) {
    this.tabPanel = tabPanel;
  }

  public void onBrowserEvent( Event event ) {
    if ( ( event.getTypeInt() & Event.ONDBLCLICK ) == Event.ONDBLCLICK ) {
      onDoubleClick( event );
    } else if ( ( event.getTypeInt() & Event.ONMOUSEUP ) == Event.ONMOUSEUP ) {
      if ( event.getButton() == Event.BUTTON_LEFT  && ( event.getEventTarget().toString().toLowerCase().indexOf( "image" ) == -1 ) ) {
        fireTabSelected();
      } else if ( event.getButton() == Event.BUTTON_RIGHT ) {
        onRightClick( event );
      }
    } else if ( ( event.getTypeInt() & Event.ONKEYDOWN ) == Event.ONKEYDOWN  ) {
      if ( KeyCodes.KEY_LEFT == event.getKeyCode() ) {
        moveFocusToPreviousTab();
      } else if ( KeyCodes.KEY_RIGHT == event.getKeyCode() ) {
        moveFocusToNextTab();
      } else if ( isCloseable() && KeyCodes.KEY_DELETE == (char) event.getKeyCode() ) {
        closeTab();
      } else if ( KeyCodes.KEY_F10 == (char) event.getKeyCode() && event.getShiftKey() ) {
        onRightClick( event );
      }
    }
    super.onBrowserEvent( event );
  }

  protected void moveFocusToNextTab() {
    PentahoTab tab = getTabPanel().getTab( getTabPanel().getSelectedTabIndex()  + 1 );
    if ( null != tab ) {
      tabPanel.selectTab( tab, true );
    }
  }

  protected void moveFocusToPreviousTab() {
    PentahoTab tab = getTabPanel().getTab( getTabPanel().getSelectedTabIndex()  - 1 );
    if ( null != tab ) {
      tabPanel.selectTab( tab, true );
    }
  }

  public void onDoubleClick( Event event ) {
  }

  public void onRightClick( Event event ) {
  }

  public void setSelected( boolean selected ) {
    if ( selected ) {
      addStyleDependentName( SELECTED );
    } else {
      removeStyleDependentName( SELECTED );
    }
  }

  public String getLabelText() {
    return label.getText();
  }

  public void setLabelText( String text ) {
    label.setText( text );
  }

  public void setLabelTooltip( String tooltip ) {
    label.setTitle( tooltip );
  }

  public String getLabelTooltip() {
    return label.getTitle();
  }

  protected void closeTab() {
    tabPanel.closeTab( this, true );
  }

  protected void fireTabSelected() {
    tabPanel.selectTab( this, true );
  }

  public boolean isSolutionBrowserShowing() {
    return solutionBrowserShowing;
  }

  public void setSolutionBrowserShowing( boolean solutionBrowserShowing ) {
    this.solutionBrowserShowing = solutionBrowserShowing;
  }

  @Override
  public int getTabIndex() {
    return focusImpl.getTabIndex( this.getElement() );
  }

  @Override
  public void setAccessKey( char key ) {
    this.getElement().setPropertyString( "accessKey", "" + key );
  }

  @Override
  public void setFocus( boolean focus ) {
    if ( focus ) {
      focusImpl.focus( this.getElement() );
    } else {
      focusImpl.blur( this.getElement() );
    }
  }

  @Override
  public void setTabIndex( int index ) {
    focusImpl.setTabIndex( this.getElement(), index );
  }
}
