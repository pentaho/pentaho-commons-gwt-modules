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

package org.pentaho.gwt.widgets.client.buttons;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;
import org.pentaho.gwt.widgets.client.utils.ElementUtils;
import org.pentaho.mantle.client.environment.EnvironmentHelper;

/**
 * Clickable image with enable/disable functionality built in. Makes use of css styles to provide the image and sizing.
 */
public class ThemeableImageButton extends Image {

  private boolean isEnabled = true;
  private final Set<String> enabledStyles = new HashSet<>();
  private final Set<String> disabledStyles = new HashSet<>();

  private static final String BLANK_IMAGE = EnvironmentHelper.getFullyQualifiedURL()
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
    Roles.getButtonRole().set( getElement() );
    getElement().setTabIndex( 0 );

    if ( enabledStyles != null && enabledStyles.length > 0 ) {
      addEnabledStyle( enabledStyles );
    }

    if ( disabledStyles != null && disabledStyles.length > 0 ) {
      addDisabledStyle( disabledStyles );
    }

    if ( tooltip != null && !tooltip.isEmpty() ) {
      setTitle( tooltip );
    }

    enable();
    setUpHandlers();
  }

  public void addEnabledStyle( String... styles ) {
    addEnabledStyle( false, styles );
  }

  public void addEnabledStyle( boolean update, String... styles ) {
    Collections.addAll( enabledStyles, styles );

    if ( update ) {
      updateStyles();
    }
  }

  public void addDisabledStyle( String... styles ) {
    addDisabledStyle( false, styles );
  }

  public void addDisabledStyle( boolean update, String... styles ) {
    Collections.addAll( disabledStyles, styles );

    if ( update ) {
      updateStyles();
    }
  }

  private void initStyles() {
    enabledStyles.clear();
    disabledStyles.clear();

    enabledStyles.add( BASE_CLASS );
    disabledStyles.add( DISABLED_CLASS );
  }

  private void setUpHandlers() {
    this.addMouseDownHandler( event -> {
      if ( isEnabled ) {
        removeStyleName( PRESSED_DISABLED_CLASS );
        addStyleName( PRESSED_CLASS );
      } else {
        removeStyleName( PRESSED_CLASS );
        addStyleName( PRESSED_DISABLED_CLASS );
      }

      event.preventDefault();
    } );

    this.addMouseUpHandler( event -> {
      updateStyles();
      event.preventDefault();
    } );

    this.addMouseOverHandler( event -> {
      if ( isEnabled ) {
        removeStyleName( MOUSEOVER_DISABLED_CLASS );
        addStyleName( MOUSEOVER_CLASS );
      } else {
        removeStyleName( MOUSEOVER_CLASS );
        addStyleName( MOUSEOVER_DISABLED_CLASS );
      }

      event.preventDefault();
    } );

    this.addKeyDownHandler( keyDownEvent -> {
      switch ( keyDownEvent.getNativeKeyCode() ) {
        case KeyCodes.KEY_SPACE:
          keyDownEvent.preventDefault();
          break;
        case KeyCodes.KEY_ENTER:
          keyDownEvent.preventDefault();
          ElementUtils.click( ThemeableImageButton.this.getElement() );
          break;
      }
    } );

    this.addKeyUpHandler( keyUpEvent -> {
      if ( KeyCodes.KEY_SPACE == keyUpEvent.getNativeKeyCode() ) {
        keyUpEvent.preventDefault();
        ElementUtils.click( ThemeableImageButton.this.getElement() );
      }
    } );

    this.addMouseOutHandler( event -> updateStyles() );
  }

  public HandlerRegistration addKeyDownHandler( KeyDownHandler handler ) {
    return this.addDomHandler( handler, KeyDownEvent.getType() );
  }

  public HandlerRegistration addKeyUpHandler( KeyUpHandler handler ) {
    return this.addDomHandler( handler, KeyUpEvent.getType() );
  }

  /* Visible for testing */
  void updateStyles() {
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
}
