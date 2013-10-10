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
 * Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.gwt.widgets.client.buttons;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;

/**
 * Clickable image with enable/disable functionality built in. Makes use of css styles to provide the image and sizing.
 */
public class ThemeableImageButton extends Image {

  private boolean isEnabled = true;
  private Set<String> enabledStyles = new HashSet<String>();
  private Set<String> disabledStyles = new HashSet<String>();

  private static final String BLANK_IMAGE = GWT.getHostPageBaseURL()
      + "content/common-ui/resources/themes/images/spacer.gif";
  private static final String BASE_CLASS = "image-button";
  private static final String DISABLED_CLASS = "disabled-image-button";
  private static final String PRESSED_CLASS = "image-button-pressed";
  private static final String PRESSED_DISABLED_CLASS = "disabled-image-button-pressed";
  private static final String MOUSEOVER_CLASS = "image-button-over";
  private static final String MOUSEOVER_DISABLED_CLASS = "disabled-image-button-over";

  public ThemeableImageButton() {
    this( new String[] {}, new String[] {}, "" );
  }

  public ThemeableImageButton( String enabledStyle, String disabledStyle, String tooltip ) {
    this( new String[] { enabledStyle }, new String[] { disabledStyle }, tooltip );
  }

  public ThemeableImageButton( String[] enabledStyles, String[] disabledStyles, String tooltip ) {
    super( BLANK_IMAGE );
    initStyles();
    if ( enabledStyles != null && enabledStyles.length > 0 ) {
      addEnabledStyle( enabledStyles );
    }
    if ( disabledStyles != null && enabledStyles.length > 0 ) {
      addDisabledStyle( disabledStyles );
    }
    if ( tooltip != null && tooltip.length() > 0 ) {
      setTitle( tooltip );
    }
    enable();
    setUpHandlers();
  }

  public void addEnabledStyle( String... style ) {
    for ( String s : style ) {
      enabledStyles.add( s );
    }
  }

  public void addDisabledStyle( String... style ) {
    for ( String s : style ) {
      disabledStyles.add( s );
    }
  }

  private void initStyles() {
    enabledStyles.clear();
    disabledStyles.clear();

    enabledStyles.add( BASE_CLASS );
    disabledStyles.add( DISABLED_CLASS );
  }

  private void setUpHandlers() {
    this.addMouseDownHandler( new MouseDownHandler() {
      public void onMouseDown( MouseDownEvent event ) {
        if ( isEnabled ) {
          removeStyleName( PRESSED_DISABLED_CLASS );
          addStyleName( PRESSED_CLASS ); //$NON-NLS-1$
        } else {
          removeStyleName( PRESSED_CLASS ); //$NON-NLS-1$
          addStyleName( PRESSED_DISABLED_CLASS );
        }
      }
    } );
    this.addMouseUpHandler( new MouseUpHandler() {
      public void onMouseUp( MouseUpEvent event ) {
        updateStyles();
      }
    } );

    this.addMouseOverHandler( new MouseOverHandler() {
      public void onMouseOver( MouseOverEvent event ) {
        if ( isEnabled ) {
          removeStyleName( MOUSEOVER_DISABLED_CLASS );
          addStyleName( MOUSEOVER_CLASS );
        } else {
          removeStyleName( MOUSEOVER_CLASS );
          addStyleName( MOUSEOVER_DISABLED_CLASS );
        }
      }
    } );

    this.addMouseOutHandler( new MouseOutHandler() {
      public void onMouseOut( MouseOutEvent event ) {
        updateStyles();
      }
    } );
  }

  private void updateStyles() {
    if ( isEnabled ) {
      enable();
    } else {
      disable();
    }
  }

  private void enable() {
    setStyleName( "" );
    for ( String style : enabledStyles ) {
      addStyleName( style );
    }
  }

  private void disable() {
    setStyleName( "" );
    for ( String style : disabledStyles ) {
      addStyleName( style );
    }
  }

  public void onBrowserEvent( Event event ) {
    super.onBrowserEvent( event );

    // This is required to prevent a drag & drop of the Image in the edit text.
    DOM.eventPreventDefault( event );
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  public void setEnabled( boolean isEnabled ) {
    if ( this.isEnabled == isEnabled ) {
      return;
    }
    this.isEnabled = isEnabled;

    if ( isEnabled ) {
      enable();
    } else {
      disable();
    }
  }

  public void setFocus( boolean focus ) {
    this.setFocus( focus );
  }

}
