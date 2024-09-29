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

import com.google.gwt.aria.client.Roles;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.impl.FocusImpl;
import org.pentaho.gwt.widgets.client.utils.ElementUtils;

/**
 * Clickable image with enable/disable functionality built in.
 * 
 * @deprecated use {@link ThemeableImageButton}
 */
@Deprecated
public class ImageButton extends Image implements Focusable {

  private boolean isEnabled = true;

  private String enabledUrl;

  private String disabledUrl;

  private static final FocusImpl focusImpl = FocusImpl.getFocusImplForWidget();

  String imagePressedStyle = "image-button-pressed"; //$NON-NLS-1$

  String disabeldImagePressedStyle = "disabled-image-button-pressed"; //$NON-NLS-1$

  String imageHoverStyle = "image-button-over"; //$NON-NLS-1$

  String disabledImageHoverStyle = "disabled-image-button-over"; //$NON-NLS-1$

  String imageStyle = "image-button"; //$NON-NLS-1$

  String disabledImageStyle = "disabled-image-button"; //$NON-NLS-1$

  String themeImageDownStyle = "pentaho-imagebutton-down"; //$NON-NLS-1$

  String themeImageHoverStyle = "pentaho-imagebutton-hover"; //$NON-NLS-1$

  String themeDisabledImageStyle = "pentaho-imagebutton-disabled"; //$NON-NLS-1$

  public ImageButton( String enabledUrl, String disabledUrl, String tooltip ) {
    this( enabledUrl, disabledUrl, tooltip, 0, 0 );
  }

  public ImageButton() {
    super();

    Roles.getButtonRole().set( getElement() );
    setTabIndex(0);

    setStyleName( imageStyle );

    this.addMouseDownHandler( new MouseDownHandler() {
      public void onMouseDown( MouseDownEvent event ) {
        removeStyleNames( imagePressedStyle, disabeldImagePressedStyle, themeImageDownStyle );

        if ( isEnabled ) {
          addStyleNames( imagePressedStyle, themeImageDownStyle );
        } else {
          addStyleNames( disabeldImagePressedStyle );
        }

        // This is required to prevent a drag & drop of the Image in the edit text.
        event.preventDefault();
      }
    } );
    this.addMouseUpHandler( new MouseUpHandler() {
      public void onMouseUp( MouseUpEvent event ) {
        removeStyleNames( imagePressedStyle, disabeldImagePressedStyle, themeImageDownStyle );
        updateStyles();

        // This is required to prevent a drag & drop of the Image in the edit text.
        event.preventDefault();
      }
    } );

    this.addMouseOverHandler( new MouseOverHandler() {
      public void onMouseOver( MouseOverEvent event ) {
        removeStyleNames( imageHoverStyle, disabledImageHoverStyle, themeImageHoverStyle );

        if ( isEnabled ) {
          addStyleNames( imageHoverStyle, themeImageHoverStyle );
        } else {
          addStyleName( disabledImageHoverStyle );
        }

        // This is required to prevent a drag & drop of the Image in the edit text.
        event.preventDefault();
      }
    } );

    this.addMouseOutHandler( new MouseOutHandler() {
      public void onMouseOut( MouseOutEvent event ) {
        removeStyleNames( imageHoverStyle, disabledImageHoverStyle, themeImageHoverStyle );
        updateStyles();

        // This is required to prevent a drag & drop of the Image in the edit text.
        event.preventDefault();
      }
    } );

    // Based on https://www.w3.org/WAI/ARIA/apg/example-index/button/js/button.js. pattern
    this.addKeyDownHandler(event -> {
      switch ( event.getNativeKeyCode() ) {
        // The action button is activated by space on the keyup event, but the
        // default action for space is already triggered on keydown. It needs to be
        // prevented to stop scrolling the page before activating the button.
        case KeyCodes.KEY_SPACE:
          event.preventDefault();
          break;
        case KeyCodes.KEY_ENTER:
          event.preventDefault();
          ElementUtils.click( ImageButton.this.getElement() );
          break;
      }
    });

    this.addKeyUpHandler(event -> {
      if ( event.getNativeKeyCode() == KeyCodes.KEY_SPACE ) {
        event.preventDefault();
        ElementUtils.click( ImageButton.this.getElement() );
      }
    });
  }

  protected HandlerRegistration addKeyDownHandler( KeyDownHandler handler ) {
    return addDomHandler(handler, KeyDownEvent.getType());
  }

  protected HandlerRegistration addKeyUpHandler( KeyUpHandler handler ) {
    return addDomHandler( handler, KeyUpEvent.getType() );
  }

  private void updateStyles() {
    removeStyleNames( imageStyle, disabledImageStyle, themeDisabledImageStyle );

    if ( isEnabled ) {
      addStyleName( imageStyle );
    } else {
      addStyleNames( disabledImageStyle, themeDisabledImageStyle );
    }
  }

  private void updateTabIndex() {
    this.setTabIndex( isEnabled ? 0 : -1);
  }

  public ImageButton( String enabledUrl, String disabledUrl, String tooltip, int width, int height ) {
    super( enabledUrl );

    Roles.getButtonRole().set( getElement() );
    setTabIndex(0);

    setSize( width + "px", height + "px" ); //$NON-NLS-1$ //$NON-NLS-2$

    this.enabledUrl = enabledUrl;
    this.disabledUrl = disabledUrl;

    if ( tooltip != null && tooltip.length() > 0 ) {
      setTitle( tooltip );
    }

  }

  public void setEnabledUrl( String url ) {
    if ( this.enabledUrl != null && this.enabledUrl.equals( url ) ) {
      return;
    }

    this.enabledUrl = url;

    // only change the url if it's different and not null
    if ( isEnabled && this.getUrl().equals( enabledUrl ) == false ) {
      this.setSrc( enabledUrl );
    } else if ( !isEnabled && disabledUrl != null && this.getUrl().equals( disabledUrl ) == false ) {
      this.setSrc( disabledUrl );
    }
  }

  public void setDisabledUrl( String url ) {
    if ( this.disabledUrl != null && this.disabledUrl.equals( url ) ) {
      return;
    }
    this.disabledUrl = url;

    // only change the url if it's different and not null
    if ( isEnabled && enabledUrl != null && this.getUrl().equals( enabledUrl ) == false ) {
      this.setSrc( enabledUrl );
    } else if ( !isEnabled && this.getUrl().equals( disabledUrl ) == false ) {
      this.setSrc( disabledUrl );
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
      this.setSrc( enabledUrl );
    } else if ( disabledUrl != null ) {
      this.setSrc( disabledUrl );
    }
    this.updateStyles();
    this.updateTabIndex();
  }

  /**
   * We're manipulating the DOM element directly instead of using the setUrl() method as setUrl(), which does a lot of
   * deferred loading / caching magic, was causing issues with IE.
   * 
   * @TODO Re-evaluate the need for this after the next GWT release.
   * @param src
   */
  private void setSrc( String src ) {
    this.getElement().setAttribute( "src", src ); //$NON-NLS-1$
  }

  @Override
  public int getTabIndex() {
    return focusImpl.getTabIndex( this.getElement() );
  }

  @Override
  public void setAccessKey( char key ) {
    this.getElement().setPropertyString( "accessKey", "" + key );
  }

  public void setFocus( boolean focus ) {
    if (focus) {
      focusImpl.focus( this.getElement() );
    } else {
      focusImpl.blur( this.getElement() );
    }
  }

  @Override
  public void setTabIndex( int index ) {
    focusImpl.setTabIndex( this.getElement(), index );
  }

  public void removeStyleNames( String... names ) {
    for ( String name : names ) {
      removeStyleName( name );
    }
  }

  public void addStyleNames( String... names ) {
    for ( String name : names ) {
      addStyleName( name );
    }
  }

}
