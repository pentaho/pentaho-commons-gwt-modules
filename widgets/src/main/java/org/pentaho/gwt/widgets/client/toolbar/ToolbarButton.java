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
 * Copyright (c) 2002-2023 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.gwt.widgets.client.toolbar;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.Widget;
import org.pentaho.gwt.widgets.client.text.ToolTip;
import org.pentaho.gwt.widgets.client.utils.ElementUtils;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages a PushButton in the common toolbar {@link Toolbar}.
 * 
 * Note, it's not a subclass of PushButton as the PushButton api does not allow the changing of the image after
 * instantiation. It is not a decorator because the GWT PushButton does not implement an interface. If these limitations
 * change, please change this class.
 * 
 * @author nbaker
 * 
 */
@SuppressWarnings( "deprecation" )
public class ToolbarButton {
  protected DockPanel button = new DockPanel();

  protected boolean enabled = true;

  protected boolean visible = true;

  protected String text;

  protected Image image;

  protected Image disabledImage;

  protected Image currentImage;

  protected Label label = new Label();

  protected FocusPanel eventWrapper = new FocusPanel();

  protected String stylePrimaryName = "toolbar-button"; //$NON-NLS-1$

  protected Command command;

  protected ToolTip toolTipWidget;

  protected String toolTip;

  protected Image downImage;

  protected Image downImageDisabled;

  String className = ""; //$NON-NLS-1$

  protected List<HandlerRegistration> imageRegistrations = new ArrayList<>();

  /**
   * Constructs a toolbar button with an image and a label
   * 
   * @param img
   *          GWT Image object
   * @param label
   *          String containing an option label
   */
  public ToolbarButton( Image img, String label ) {
    this( img );
    this.label.setText( label );
    button.add( this.label, DockPanel.EAST );
  }

  /**
   * Constructs a toolbar button with an enabled image, disabled image and a label
   * 
   * @param img
   *          GWT Image object
   * @param disabledImage
   *          GWT Image object
   * @param label
   *          String containing an option label
   */
  public ToolbarButton( Image img, Image disabledImage, String label ) {
    this( img, label );
    this.disabledImage = disabledImage;
  }

  /**
   * Constructs a toolbar button with an enabled image, disabled image and a label
   * 
   * @param img
   *          GWT Image object
   * @param disabledImage
   *          GWT Image object
   */
  public ToolbarButton( Image img, Image disabledImage ) {
    this( img );
    this.disabledImage = disabledImage;
  }

  /**
   * Constructs a toolbar button with an image, currently hardcoded to 16x16
   * 
   * @param img
   *          GWT Image object
   */
  public ToolbarButton( Image img ) {
    this.image = img;
    this.currentImage = img;

    button.add( this.image, DockPanel.CENTER );
    button.setCellHorizontalAlignment( this.image, DockPanel.ALIGN_CENTER );
    button.setCellVerticalAlignment( this.image, DockPanel.ALIGN_MIDDLE );

    button.setStyleName( stylePrimaryName );
    Roles.getPresentationRole().set( button.getElement() );

    eventWrapper.add( button );
    eventWrapper.addStyleName( stylePrimaryName + "-focus-panel" );
    Roles.getButtonRole().set( eventWrapper.getElement() );

    addStyleMouseListener();
  }

  public void setId( String id ) {
    if ( ( button != null ) && ( button.getElement() != null ) ) {
      button.getElement().setId( id.concat( "_btn" ) ); //$NON-NLS-1$
    }
    if ( ( eventWrapper != null ) && ( eventWrapper.getElement() != null ) ) {
      eventWrapper.getElement().setId( id );
    }

  }

  public void setStylePrimaryName( String styleName ) {
    this.stylePrimaryName = styleName;
    button.setStylePrimaryName( styleName );

  }

  protected void addStyleMouseListener() {
    // a click listener is more appropriate here to fire the click events
    // rather than a mouse-up because the focus panel can (and does) sometimes
    // receive mouse up events if a widget 'above' it has been clicked and
    // dismissed (on mouse-down). The ensures that only a true click will
    // fire a button's command
    eventWrapper.addClickListener( new ClickListener() {
      public void onClick( Widget sender ) {
        if ( !enabled ) {
          // ElementUtils.blur(ToolbarButton.this.eventWrapper.getElement());
          return;
        }
        button.removeStyleName( stylePrimaryName + "-down" ); //$NON-NLS-1$
        button.removeStyleName( stylePrimaryName + "-hovering" ); //$NON-NLS-1$
        command.execute();
        // ElementUtils.blur(ToolbarButton.this.eventWrapper.getElement());
      }
    } );
    eventWrapper.addMouseListener( new MouseListener() {
      public void onMouseDown( Widget w, int arg1, int arg2 ) {
        if ( !enabled ) {
          return;
        }
        button.addStyleName( stylePrimaryName + "-down" ); //$NON-NLS-1$
      }

      public void onMouseEnter( Widget arg0 ) {
        if ( !enabled ) {
          return;
        }
        button.addStyleName( stylePrimaryName + "-hovering" ); //$NON-NLS-1$
      }

      public void onMouseLeave( Widget arg0 ) {
        if ( !enabled ) {
          return;
        }
        button.removeStyleName( stylePrimaryName + "-hovering" ); //$NON-NLS-1$
      }

      public void onMouseUp( Widget arg0, int arg1, int arg2 ) {
        if ( !enabled ) {
          // ElementUtils.blur(ToolbarButton.this.eventWrapper.getElement());
          return;
        }
        button.removeStyleName( stylePrimaryName + "-down" ); //$NON-NLS-1$
        button.removeStyleName( stylePrimaryName + "-hovering" ); //$NON-NLS-1$
        // ElementUtils.blur(ToolbarButton.this.eventWrapper.getElement());
      }

      public void onMouseMove( Widget arg0, int arg1, int arg2 ) {
      }
    } );

    addKeyboardListener();
  }

  protected void addKeyboardListener() {
    eventWrapper.addKeyDownHandler( keyDownEvent -> {
      switch ( keyDownEvent.getNativeKeyCode() ) {
        case KeyCodes.KEY_SPACE:
          keyDownEvent.preventDefault();
          break;
        case KeyCodes.KEY_ENTER:
          keyDownEvent.preventDefault();
          ElementUtils.click( ToolbarButton.this.button.getElement() );
          break;
      }
    } );

    eventWrapper.addKeyUpHandler( keyUpEvent -> {
      if ( KeyCodes.KEY_SPACE == keyUpEvent.getNativeKeyCode() ) {
        keyUpEvent.preventDefault();
        ElementUtils.click( ToolbarButton.this.button.getElement() );
      }
    } );
  }

  public HandlerRegistration addKeyDownHandler( KeyDownHandler handler ) {
    return this.button.addDomHandler( handler, KeyDownEvent.getType() );
  }

  public HandlerRegistration addKeyUpHandler( KeyUpHandler handler ) {
    return this.button.addDomHandler( handler, KeyUpEvent.getType() );
  }

  /**
   * Gets the enabled status of the button.
   * 
   * @return boolean flag
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Sets the enabled status of the button.
   * 
   * @param enabled
   *          boolean flag
   */
  public void setEnabled( boolean enabled ) {
    boolean prevState = this.enabled;
    this.enabled = enabled;
    if ( enabled ) {
      button.removeStyleName( stylePrimaryName + "-disabled" ); //$NON-NLS-1$
      this.eventWrapper.setTabIndex( 0 );
      if ( prevState == false && disabledImage != null ) {
        // was disabled, remove old image and put in the enabled one
        button.remove( currentImage );
        button.add( calculateApporiateImage(), DockPanel.CENTER );
        button.setCellHorizontalAlignment( this.image, DockPanel.ALIGN_CENTER );
        button.setCellVerticalAlignment( this.image, DockPanel.ALIGN_MIDDLE );
      }

    } else {
      button.addStyleName( stylePrimaryName + "-disabled" ); //$NON-NLS-1$
      this.eventWrapper.setTabIndex( -1 );
      if ( prevState == true && disabledImage != null ) {
        // was enabled, remove old image and put in the disabled one
        button.remove( currentImage );
        button.add( calculateApporiateImage(), DockPanel.CENTER );
        button.setCellHorizontalAlignment( this.disabledImage, DockPanel.ALIGN_CENTER );
        button.setCellVerticalAlignment( this.disabledImage, DockPanel.ALIGN_MIDDLE );
      }
    }
  }

  public void setTempDisabled( boolean disable ) {
    button.setStyleName( this.enabled && !disable ? stylePrimaryName : stylePrimaryName + "-disabled" //$NON-NLS-1$
    );
  }

  /**
   * Gets the visibility of the button]
   * 
   * @return boolean flag
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Sets the visibility of the button
   * 
   * @param visible
   *          boolean flag
   */
  public void setVisible( boolean visible ) {
    this.visible = visible;
    button.setVisible( visible );
    eventWrapper.getElement().setTabIndex( visible ? 0 : -1 );
  }

  /**
   * Returns the managed PushButton object
   * 
   * @return PushButton concreate object
   */
  public FocusPanel getPushButton() {
    return eventWrapper;
  }

  /**
   * Returns the image displayed on this button.
   * 
   * @return GWT Image
   */
  public Image getImage() {
    return image;
  }

  /**
   * Returns the alternative image text.
   * @return null if image is null
   */
  public String getImageAltText() {
    return ( this.image != null ) ? this.image.getAltText() : null;
  }

  /**
   * Returns the image displayed on this button.
   * 
   * @return GWT Image
   */
  public Image getDisabledImage() {
    return disabledImage;
  }

  /**
   * Sets the image displayed on this button.
   * 
   * @param image GWT Image
   */
  public void setImage( Image image ) {
    image.addStyleName( "icon-zoomable" );
    this.image = image;

    button.remove( currentImage );
    Image curImage = calculateApporiateImage();
    button.add( curImage, DockPanel.CENTER );
    button.setCellHorizontalAlignment( curImage, DockPanel.ALIGN_CENTER );
    button.setCellVerticalAlignment( curImage, DockPanel.ALIGN_MIDDLE );

    updateImages();
  }

  /**
   * Set image alternative text.
   * @param altText alternative text
   */
  public void setImageAltText( String altText ) {
    if ( this.image != null ) {
      this.image.setAltText( altText );
    }
  }

  /**
   * Sets the image to be displayed on this button when disabled (greyed out).
   * 
   * @param img
   *          GWT Image
   */
  public void setDisabledImage( Image img ) {
    if ( !isEnabled() ) {
      // was enabled, remove old image and put in the disabled one
      this.disabledImage = img;
      button.remove( currentImage );
      Image curImage = calculateApporiateImage();
      button.add( curImage, DockPanel.CENTER );
      button.setCellHorizontalAlignment( curImage, DockPanel.ALIGN_CENTER );
      button.setCellVerticalAlignment( curImage, DockPanel.ALIGN_MIDDLE );

      updateImages();
    }
  }

  /**
   * Returns the optional text to be displayed on this button.
   * 
   * @return String
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the optional text to be displayed on this button.
   * 
   * @param text
   *          String to be displayed
   */
  public void setText( String text ) {
    this.text = text;
    label.setText( text );
  }

  /**
   * Returns the click listener attached to the button instance.
   * 
   * @return ClickListener
   */
  public Command getCommand() {
    return command;
  }

  /**
   * Sets the ClickListener on the button. If a ClickListener was previously added to the button, it will be removed
   * (let Nick know if you don't like this behavior).
   * 
   * @param clickListener
   */
  public void setCommand( Command cmd ) {
    this.command = cmd;
  }

  /**
   * Sets the text to be displayed in a hover-tip when a user hovers over the button
   * 
   * @param toolTip
   *          String
   */
  public void setToolTip( String toolTip ) {
    this.toolTip = toolTip;
    if ( toolTipWidget != null ) {
      eventWrapper.removeMouseListener( toolTipWidget );
    }
    toolTipWidget = new ToolTip( toolTip, 1000 );
    eventWrapper.addMouseListener( toolTipWidget );
  }

  /**
   * Gets the text to be displayed in a hover-tip when a user hovers over the button
   * 
   * @return String tooltip
   */
  public String getToolTip() {
    return toolTip;
  }

  protected Image calculateApporiateImage() {
    currentImage = ( !enabled && disabledImage != null ) ? disabledImage : image;
    return currentImage;
  }

  /**
   * Gets the image to be displayed on this button when depressed
   * 
   */
  public Image getDownImage() {

    return downImage;
  }

  /**
   * Sets the image to be displayed on this button when depressed
   * 
   * @param img
   *          GWT Image
   */
  public void setDownImage( Image downImage ) {

    this.downImage = downImage;
  }

  public Image getDownImageDisabled() {

    return downImageDisabled;
  }

  public void setDownImageDisabled( Image downImageDisabled ) {

    this.downImageDisabled = downImageDisabled;
  }

  public void addClassName( String className ) {
    this.className = className;

    updateImages();
  }

  /**
   * Updates images to have appropriate CSS class names.
   * <p>
   *   To be called whenever any of the fields <code>image</code>, <code>disabledImage</code>, or <code>className</code>
   *   change.
   * </p>
   */
  protected void updateImages() {

    disposeImageRegistrations();

    updateImg( this.image, true );

    if ( this.disabledImage != null ) {
      updateImg( this.disabledImage, false );
    }
  }

  void updateImg( final Image img, boolean enabled ) {

    if ( !StringUtils.isEmpty( className ) ) {
      img.addStyleName( className );
    }

    String disabledStyle = "pentaho-imagebutton-disabled"; //$NON-NLS-1$
    if ( !enabled ) {
      img.addStyleName( disabledStyle );
    } else {
      img.removeStyleName( disabledStyle );
    }

    final String overStyle = "pentaho-imagebutton-hover"; //$NON-NLS-1$

    // Mouse over
    imageRegistrations.add( img.addMouseOverHandler( new MouseOverHandler() {

      @Override
      public void onMouseOver( MouseOverEvent event ) {
        img.addStyleName( overStyle );

      }
    } ) );

    // Mouse Out
    imageRegistrations.add( img.addMouseOutHandler( new MouseOutHandler() {

      @Override
      public void onMouseOut( MouseOutEvent event ) {
        img.removeStyleName( overStyle );
      }
    } ) );

    final String downStyle = "pentaho-imagebutton-down"; //$NON-NLS-1$

    // Mouse Down
    imageRegistrations.add( img.addMouseDownHandler( new MouseDownHandler() {

      @Override
      public void onMouseDown( MouseDownEvent event ) {
        img.addStyleName( downStyle );
      }
    } ) );

    // Mouse Up
    imageRegistrations.add( img.addMouseUpHandler( new MouseUpHandler() {

      @Override
      public void onMouseUp( MouseUpEvent event ) {
        img.removeStyleName( downStyle );
      }
    } ) );
  }

  private void disposeImageRegistrations() {
    for ( HandlerRegistration registration : imageRegistrations ) {
      registration.removeHandler();
    }

    imageRegistrations.clear();
  }
}
