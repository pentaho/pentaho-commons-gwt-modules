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


package org.pentaho.gwt.widgets.client.dialogs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import org.pentaho.gwt.widgets.client.utils.FrameUtils;

public class ResizableDialogBox {

  private AbsolutePanel boundaryPanel;
  private WindowPanel windowPanel;
  private IDialogValidatorCallback validatorCallback;
  private IDialogCallback callback;
  private Widget content;

  public ResizableDialogBox( final String headerText, String okText, String cancelText, final Widget content,
      final boolean modal ) {
    this.content = content;
    boundaryPanel = new AbsolutePanel() {
      public void onBrowserEvent( Event event ) {
        super.onBrowserEvent( event );
        if ( !modal && event.getTypeInt() == Event.ONCLICK ) {
          hide();
        }
      }
    };
    boundaryPanel.setSize( "100%", Window.getClientHeight() + Window.getScrollTop() + "px" ); //$NON-NLS-1$ //$NON-NLS-2$
    boundaryPanel.setVisible( true );
    RootPanel.get().add( boundaryPanel, 0, 0 );
    boundaryPanel.sinkEvents( Event.ONCLICK );
    boundaryPanel.getElement().getStyle().setProperty( "cursor", "wait" ); //$NON-NLS-1$ //$NON-NLS-2$

    // initialize window controller which provides drag and resize windows
    WindowController windowController = new WindowController( boundaryPanel );

    // content wrapper
    Button ok = new Button( okText );
    ok.setStylePrimaryName( "pentaho-button" );
    ok.getElement().setAttribute( "id", "okButton" ); //$NON-NLS-1$ //$NON-NLS-2$
    ok.addClickHandler( new ClickHandler() {

      @Override
      public void onClick( ClickEvent event ) {
        if ( validatorCallback == null || ( validatorCallback != null && validatorCallback.validate() ) ) {
          try {
            if ( callback != null ) {
              callback.okPressed();
            }
          } catch ( Throwable dontCare ) {
            //ignore
          }
          hide();
        }
      }
    } );
    final HorizontalPanel dialogButtonPanel = new HorizontalPanel();
    dialogButtonPanel.setSpacing( 2 );
    dialogButtonPanel.add( ok );
    if ( cancelText != null ) {
      Button cancel = new Button( cancelText );
      cancel.setStylePrimaryName( "pentaho-button" );
      cancel.getElement().setAttribute( "id", "cancelButton" ); //$NON-NLS-1$ //$NON-NLS-2$
      cancel.addClickHandler( new ClickHandler() {

        public void onClick( ClickEvent event ) {
          try {
            if ( callback != null ) {
              callback.cancelPressed();
            }
          } catch ( Throwable dontCare ) {
            //ignore
          }
          hide();
        }
      } );
      dialogButtonPanel.add( cancel );
    }
    HorizontalPanel dialogButtonPanelWrapper = new HorizontalPanel();
    if ( okText != null && cancelText != null ) {
      dialogButtonPanelWrapper.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_RIGHT );
    } else {
      dialogButtonPanelWrapper.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
    }
    dialogButtonPanelWrapper.setStyleName( "dialogButtonPanel" ); //$NON-NLS-1$
    dialogButtonPanelWrapper.setWidth( "100%" ); //$NON-NLS-1$
    dialogButtonPanelWrapper.add( dialogButtonPanel );

    Grid dialogContent = new Grid( 2, 1 );
    dialogContent.setCellPadding( 0 );
    dialogContent.setCellSpacing( 0 );
    dialogContent.getCellFormatter().setVerticalAlignment( 1, 0, HasVerticalAlignment.ALIGN_TOP );
    dialogContent.getCellFormatter().setHorizontalAlignment( 1, 0, HasHorizontalAlignment.ALIGN_LEFT );
    // add content
    dialogContent.setWidget( 0, 0, content );
    dialogContent.getCellFormatter().setVerticalAlignment( 0, 0, HasVerticalAlignment.ALIGN_TOP );
    // add button panel
    dialogContent.setWidget( 1, 0, dialogButtonPanelWrapper );
    dialogContent.getCellFormatter().setVerticalAlignment( 1, 0, HasVerticalAlignment.ALIGN_BOTTOM );
    dialogContent.setWidth( "100%" ); //$NON-NLS-1$
    dialogContent.setHeight( "100%" ); //$NON-NLS-1$

    windowPanel = new WindowPanel( windowController, headerText, dialogContent, true );
  }

  public void hide() {
    boundaryPanel.clear();
    RootPanel.get().remove( boundaryPanel );
    // show <embeds>
    FrameUtils.toggleEmbedVisibility( true );
  }

  public void center() {
    boundaryPanel.clear();
    int left = ( Window.getClientWidth() - windowPanel.getOffsetWidth() ) >> 1;
    int top = ( Window.getClientHeight() - windowPanel.getOffsetHeight() ) >> 1;
    boundaryPanel.add( windowPanel, Window.getScrollLeft() + left, Window.getScrollTop() + top );
    left = ( Window.getClientWidth() - windowPanel.getOffsetWidth() ) >> 1;
    top = ( Window.getClientHeight() - windowPanel.getOffsetHeight() ) >> 1;
    boundaryPanel.clear();
    boundaryPanel.add( windowPanel, Window.getScrollLeft() + left, Window.getScrollTop() + top );
    // hide <embeds>
    FrameUtils.toggleEmbedVisibility( false );
  }

  public void show() {
    center();
  }

  public IDialogValidatorCallback getValidatorCallback() {
    return validatorCallback;
  }

  public void setValidatorCallback( IDialogValidatorCallback validatorCallback ) {
    this.validatorCallback = validatorCallback;
  }

  public IDialogCallback getCallback() {
    return callback;
  }

  public void setCallback( IDialogCallback callback ) {
    this.callback = callback;
  }

  public Widget getContent() {
    return content;
  }

  public void setText( String text ) {
    windowPanel.setText( text );
  }

  public void setTitle( String title ) {
    windowPanel.setTitle( title );
  }

  public void setPixelSize( int width, int height ) {
    windowPanel.setPixelSize( width, height );
  }

}
