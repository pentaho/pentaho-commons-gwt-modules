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

package org.pentaho.gwt.widgets.client.dialogs;

import com.google.gwt.aria.client.Id;
import com.google.gwt.aria.client.Role;
import com.google.gwt.aria.client.Roles;
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
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;

import static org.pentaho.gwt.widgets.client.utils.ElementUtils.ensureId;

@SuppressWarnings( "deprecation" )
public class DialogBox extends com.google.gwt.user.client.ui.DialogBox implements PopupListener {

  private static FocusPanel pageBackground = null;
  private static int clickCount = 0;
  private static int dialogDepthCount = 0;
  private Focusable focusWidget = null;
  boolean autoHide = false;
  boolean modal = true;
  boolean centerCalled = false;

  public DialogBox( boolean autoHide, boolean modal ) {
    super( autoHide, modal );
    this.autoHide = autoHide;
    this.modal = modal;
    addPopupListener( this );
    setStylePrimaryName( "pentaho-dialog" );

    // ARIA
    setAriaRole( (Role) null );

    setDomModal( this.isModal() );

    // aria-labelledby
    Widget labelWidget = getCaption().asWidget();
    Roles.getHeadingRole().set( labelWidget.getElement() );
    Roles.getHeadingRole().setAriaLevelProperty( labelWidget.getElement(), 2 );

    Roles.getDialogRole().setAriaLabelledbyProperty( getElement(), Id.of( ensureId( labelWidget ) ) );

    // Layout
    // This should be the contained decorator panel's layout table.
    // div > div > tab
    Roles.getPresentationRole().set( getElement().getFirstChildElement().getFirstChildElement() );
  }

  // region aria role property
  /**
   * Gets the ARIA role of the dialog.
   */
  public String getAriaRole() {
    return getElement().getAttribute( "role" );
  }

  /**
   * Sets the ARIA role of the dialog given as a string.
   *
   * @param ariaRole The new ARIA <code>role</code> attribute.
   *                 When <code>null</code> or empty, it is set to the default value of <code>dialog</code>.
   */
  public void setAriaRole( String ariaRole ) {
    if ( StringUtils.isEmpty( ariaRole ) ) {
      ariaRole = "dialog";
    }

    getElement().setAttribute( "role", ariaRole );
  }

  /**
   * Sets the ARIA role of the dialog given as a {@link Role} instance.
   *
   * @param ariaRole The new ARIA <code>role</code> attribute.
   *                 When <code>null</code>, it is set to the default value of {@link Roles#getDialogRole()}.
   */
  protected void setAriaRole( Role ariaRole ) {
    if ( ariaRole == null ) {
      ariaRole = Roles.getDialogRole();
    }

    ariaRole.set( getElement() );
  }
  // endregion

  // region ariaDescribedBy property
  /**
   * Gets the identifier of the ARIA description element.
   * @return The value of the <code>aria-describedby</code> attribute.
   */
  public String getAriaDescribedBy() {
    return Roles.getDialogRole().getAriaDescribedbyProperty( getElement() );
  }

  /**
   * Sets the identifier of the ARIA description element.
   * <p>
   * Sets the value of the <code>aria-describedby</code> attribute.
   * </p>
   *
   * @param describedById The description element identifier.
   */
  public void setAriaDescribedBy( String describedById ) {
    if ( StringUtils.isEmpty( describedById ) ) {
      Roles.getDialogRole().removeAriaDescribedbyProperty( getElement() );
    } else {
      Roles.getDialogRole().setAriaDescribedbyProperty( getElement(), Id.of( describedById ) );
    }
  }
  // endregion

  // region modal property
  @Override
  public void setModal( boolean modal ) {
    super.setModal( modal );
    setDomModal( modal );
  }

  private void setDomModal( boolean modal ) {
    if ( modal ) {
      addStyleName( "modal" );
    } else {
      removeStyleName( "modal" );
    }

    getElement().setAttribute( "aria-modal", Boolean.toString( modal ) );
  }
  // endregion

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

  // region Focus
  /**
   * Gets the widget explicitly set to receive the focus when the dialog is shown.
   * @return The widget to receive focus, if any; <code>null</code>, otherwise.
   * @see #getAutoFocusWidget()
   */
  public Focusable getFocusWidget() {
    return focusWidget;
  }

  /**
   * Explicitly sets the focus widget that should receive keyboard focus when the dialog is shown.
   * @param widget The focus widget.
   * @see #doAutoFocus()
   * @see #getAutoFocusWidget()
   * @deprecated Use {@link #setFocusWidget(Focusable)} instead.
   */
  @Deprecated
  public void setFocusWidget( FocusWidget widget ) {
    setFocusWidget( (Focusable) widget );
  }

  /**
   * Explicitly sets the focusable widget that should receive keyboard focus when the dialog is shown.
   * @param widget A focusable widget or <code>null</code>.
   * @see #doAutoFocus()
   * @see #getAutoFocusWidget()
   */
  public void setFocusWidget( Focusable widget ) {
    focusWidget = widget;
    doAutoFocus();
  }

  /**
   * Gets the widget to automatically receive the keyboard focus when the dialog is shown,
   * or when explicitly calling {@link #doAutoFocus()}.
   * <p>
   *   If a fixed {@link #setFocusWidget(FocusWidget)} has been set, then that is returned.
   *   Otherwise, the default automatic focus widget is the first <i>currently</i> focusable
   *   descendant of the dialog's {@link #getWidget()} widget, possibly itself,
   *   as determined by {@link ElementUtils#findFirstKeyboardFocusableDescendant(Widget)}.
   * </p>
   * @return The automatic focus widget, if any; <code>null</code>, otherwise.
   */
  public Focusable getAutoFocusWidget() {
    if ( focusWidget != null ) {
      return focusWidget;
    }

    return getDefaultAutoFocusWidget();
  }

  /**
   * Explicitly sets the focus to the automatic focus widget, if any.
   * <p>
   *   The automatic focus widget is determined by {@link #getAutoFocusWidget()}.
   * </p>
   * <p>
   *   This is automatically called as part of showing a dialog, either via {@link #show()}, {@link #center()} or
   *   {@link #setPopupPositionAndShow(PositionCallback)}.
   *   It is useful in cases where the dialog content is not finalized at this time,
   *   allowing it to be called afterwards.
   * </p>
   * <p>
   *   This method has no effect if the dialog is not showing, as per {@link #isShowing()},
   *   or is not currently {@link #isVisible()}.
   *   When the methods {@link #center()} and {@link #setPopupPositionAndShow(PositionCallback)} are used to
   *   show the dialog, the dialog is only made visible <i>after</i> the internal call to {@link #show()} has ended.
   * </p>
   */
  public void doAutoFocus() {
    if ( isShowing() && isVisible() ) {
      Focusable autoFocusable = getAutoFocusWidget();
      if ( autoFocusable != null ) {
        autoFocusable.setFocus( true );
      }
    }
  }

  /**
   * Gets the default automatic focus widget.
   * <p>
   *   Override when the rules for determining the default automatic focus widget don't
   *   strictly follow document order.
   * </p>
   * @return The default automatic focus widget, if any; <code>null</code>, otherwise.
   */
  protected Focusable getDefaultAutoFocusWidget() {
    Widget root = getWidget();
    if ( root != null ) {
      return ElementUtils.findFirstKeyboardFocusableDescendant( root );
    }

    return null;
  }
  // endregion

  public void show() {
    super.show();
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
