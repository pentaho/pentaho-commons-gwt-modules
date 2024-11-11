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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.pentaho.gwt.widgets.client.utils.ButtonHelper;
import org.pentaho.gwt.widgets.client.utils.ButtonHelper.ButtonLabelType;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings( "deprecation" )
public class CustomButton extends Widget {

  private String baseStyleName = "customButton"; //$NON-NLS-1$
  private Command command;
  private boolean enabled = true;
  private Image image;
  private String text;
  private ButtonLabelType type;
  private List<ClickListener> listeners = new ArrayList<ClickListener>();
  HorizontalPanel buttonPanel;

  public CustomButton() {
  }

  public CustomButton( Image image, String text, ButtonLabelType type ) {
    SimplePanel spacer = new SimplePanel();
    spacer.setWidth( "10px" ); //$NON-NLS-1$
    buttonPanel = new HorizontalPanel();
    buttonPanel.add( spacer );
    buttonPanel.add( ButtonHelper.createButtonElement( image, text, type ) );
    buttonPanel.add( spacer );
    this.setElement( buttonPanel.getElement() );
    buttonPanel.setStylePrimaryName( baseStyleName );
    sinkEvents( Event.MOUSEEVENTS );
    sinkEvents( Event.ONDBLCLICK );
  }

  public CustomButton( Image image, String text, ButtonLabelType type, String className ) {
    SimplePanel spacer = new SimplePanel();
    spacer.setWidth( "10px" ); //$NON-NLS-1$
    buttonPanel = new HorizontalPanel();
    buttonPanel.add( spacer );
    buttonPanel.add( ButtonHelper.createButtonElement( image, text, type, className ) );
    buttonPanel.add( spacer );
    this.setElement( buttonPanel.getElement() );
    buttonPanel.setStylePrimaryName( baseStyleName );
    sinkEvents( Event.MOUSEEVENTS );
    sinkEvents( Event.ONDBLCLICK );
  }

  public CustomButton( Image image, String text, ButtonLabelType type, Command command ) {
    this( image, text, type );
    setCommand( command );
  }

  private void setCommand( Command command ) {
    this.command = command;
  }

  @Override
  public void setStylePrimaryName( String style ) {
    super.setStylePrimaryName( style );
    baseStyleName = style;
    buttonPanel.setStylePrimaryName( style );
  }

  @Override
  public void addStyleDependentName( String style ) {
    super.addStyleDependentName( style );
    buttonPanel.addStyleDependentName( style );
  }

  @Override
  public void removeStyleDependentName( String style ) {
    super.removeStyleDependentName( style );
    buttonPanel.removeStyleDependentName( style );
  }

  public String getText() {
    return text;
  }

  public void setText( String text ) {
    this.text = text;
  }

  public void setEnabled( boolean enabled ) {
    boolean prevVal = this.enabled;
    this.enabled = enabled;

    if ( prevVal && enabled ) {
      return;
    } else if ( prevVal && !enabled ) {
      this.addStyleDependentName( "disabled" ); //$NON-NLS-1$
    } else {
      this.removeStyleDependentName( "disabled" ); //$NON-NLS-1$
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void onBrowserEvent( Event event ) {
    switch ( event.getTypeInt() ) {
      case Event.ONDBLCLICK:
      case Event.ONCLICK:
      case Event.ONMOUSEUP:
        if ( CustomButton.this.isEnabled() ) {
          fireClicked();
          if ( command != null ) {
            command.execute();
          }
          event.cancelBubble( true );
          event.preventDefault();
        }
        break;
      case Event.ONMOUSEOVER:
        if ( CustomButton.this.isEnabled() ) {
          CustomButton.this.addStyleDependentName( "hover" ); //$NON-NLS-1$
        }
        break;
      case Event.ONMOUSEOUT:
        if ( CustomButton.this.isEnabled() ) {
          CustomButton.this.removeStyleDependentName( "hover" ); //$NON-NLS-1$
        }
        break;
      case Event.ONMOUSEDOWN:
        if ( CustomButton.this.isEnabled() ) {
          CustomButton.this.addStyleDependentName( "down" ); //$NON-NLS-1$
        }
        break;
    }
  }

  private void fireClicked() {
    for ( ClickListener listener : listeners ) {
      listener.onClick( this );
    }
  }

  public void addClickListener( ClickListener listener ) {
    listeners.add( listener );
  }

  public void removeClickListener( ClickListener listener ) {
    listeners.remove( listener );
  }

  public void setFocus( boolean focus ) {
    this.setFocus( focus );
  }

  public Image getImage() {
    return image;
  }

  public void setImage( Image image ) {
    this.image = image;
  }

  public ButtonLabelType getType() {
    return type;
  }

  public void setType( ButtonLabelType type ) {
    this.type = type;
  }

  public Command getCommand() {
    return command;
  }

}
