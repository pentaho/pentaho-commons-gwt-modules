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

package org.pentaho.gwt.widgets.client.dialogs;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import org.pentaho.gwt.widgets.client.utils.ElementUtils;
import org.pentaho.gwt.widgets.client.utils.FrameUtils;

@SuppressWarnings( "deprecation" )
public class DialogBox extends com.google.gwt.user.client.ui.DialogBox implements PopupListener {

  private static FocusPanel pageBackground = null;
  private static int clickCount = 0;
  private static int dialogDepthCount = 0;
  private FocusWidget focusWidget = null;
  boolean autoHide = false;
  boolean modal = true;
  boolean centerCalled = false;

  public DialogBox( boolean autoHide, boolean modal ) {
    super( autoHide, modal );
    this.autoHide = autoHide;
    this.modal = modal;
    addPopupListener( this );
    setStylePrimaryName( "pentaho-dialog" );
  }

  public boolean onKeyDownPreview( char key, int modifiers ) {
    // Use the popup's key preview hooks to close the dialog when either
    // enter or escape is pressed.
    switch ( key ) {
      case KeyboardListener.KEY_ESCAPE:
        hide();
        break;
    }
    return true;
  }


  protected void initializePageBackground() {
    // IE6 has problems with 100% height so is better a huge size
    // pageBackground.setSize("100%", "100%");
    if ( pageBackground == null ) {
      pageBackground = new FocusPanel();
      pageBackground.setStyleName( "glasspane" ); //$NON-NLS-1$
      pageBackground.addClickListener( new ClickListener() {

        public void onClick( Widget sender ) {
          clickCount++;
          if ( clickCount > 2 ) {
            clickCount = 0;
            pageBackground.setVisible( false );
            pageBackground.getElement().getStyle().setDisplay( Display.NONE );
          }
        }
      } );
      RootPanel.get().add( pageBackground, 0, 0 );
    }
  }

  protected  void block() {
    pageBackground.setSize( "100%", Window.getClientHeight() + Window.getScrollTop() + "px" ); //$NON-NLS-1$ //$NON-NLS-2$
    pageBackground.setVisible( true );
    pageBackground.getElement().getStyle().setDisplay( Display.BLOCK );

  }

  public void center() {
    initializePageBackground();
    super.center();
    if ( modal && !centerCalled ) {
      block();
      dialogDepthCount++;
    }
    doAutoFocus();
    // hide <embeds>
    // TODO: migrate to GlassPane Listener
    FrameUtils.toggleEmbedVisibility( false );

    // Notify listeners that we're showing a dialog (hide PDFs, flash).
    if ( !centerCalled ) {
      GlassPane.getInstance().show();
    }
    centerCalled = true;
  }

  public Focusable getAutoFocusWidget() {
    if ( focusWidget != null ) {
      return focusWidget;
    }

    return getDefaultFocusWidget();
  }

  private void doAutoFocus() {
    Focusable autoFocusWidget = getAutoFocusWidget();
    if ( autoFocusWidget != null ) {
      autoFocusWidget.setFocus( true );
    }
  }

  private Focusable getDefaultFocusWidget() {
    Widget root = getWidget();
    if ( root != null ) {
      return ElementUtils.findFirstKeyboardFocusableDescendant( root );
    }
    return null;
  }

  public void show() {
    super.show();
    doAutoFocus();
  }

  public void setFocusWidget( FocusWidget widget ) {
    focusWidget = widget;
    doAutoFocus();
  }

  public void onPopupClosed( PopupPanel sender, boolean autoClosed ) {
    if ( modal ) {
      dialogDepthCount--;
      centerCalled = false;
      if ( dialogDepthCount <= 0 ) {
        pageBackground.setVisible( false );

        // reshow <embeds>
        if ( this.isVisible() ) {
          // TODO: migrate to glasspane listener
          FrameUtils.toggleEmbedVisibility( true );
        }

        // just make sure it is zero
        dialogDepthCount = 0;
      }
    }

    // Notify listeners that we're hiding a dialog (re-show PDFs, flash).
    GlassPane.getInstance().hide();
  }

  protected static FocusPanel getPageBackground() {
    return pageBackground;
  }
}
