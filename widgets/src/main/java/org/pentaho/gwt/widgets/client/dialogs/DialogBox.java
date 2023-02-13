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
 * Copyright (c) 2002-2023 Hitachi Vantara. All rights reserved.
 */

package org.pentaho.gwt.widgets.client.dialogs;

import com.google.gwt.aria.client.Id;
import com.google.gwt.aria.client.Role;
import com.google.gwt.aria.client.Roles;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
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

  /**
   * The layout sizing modes supported by a responsive dialog.
   *
   * @see DialogBox#isResponsive()
   * @see DialogBox#getSizingMode()
   * @see DialogBox#setSizingMode(DialogSizingMode)
   */
  public enum DialogSizingMode {
    /**
     * The dialog is sized to fit the content, both horizontally and vertically.
     * <p>
     *   The dialog respects predefined safe margins relative to the viewport that limit its maximum size.
     *   The dialog can also be assigned an explicit maximum <i>width</i> category.
     * </p>
     * <p>
     *   This sizing mode can be used for simple and/or static dialogs,
     *   i.e. whose content size does not change dynamically, in response to user interaction.
     *   A good use case is message boxes.
     *   In this case, the dialog's maximum width is constrained for text legibility reasons.
     * </p>
     */
    SIZE_TO_CONTENT( null ),

    /**
     * The dialog is sized to fill the viewport, horizontally, and to fit the content, vertically.
     * <p>
     *   The dialog respects predefined safe margins relative to the viewport that limit its maximum size.
     *   The dialog is typically assigned an explicit maximum <i>width</i> category.
     * </p>
     * <p>
     *   This sizing mode is best for dialogs whose content size changes dynamically,
     *   in response to user interaction, but which do not honor filling the entire viewport,
     *   as in {@link DialogSizingMode#FULL_VIEWPORT}.
     * </p>
     */
    FILL_VIEWPORT_WIDTH( "ds-fill-viewport-width" ),

    /**
     * The dialog is sized to fill the viewport, both horizontally and vertically.
     * <p>
     *   The dialog respects predefined safe margins relative to the viewport that limit its maximum size.
     *   The dialog is typically assigned an explicit maximum <i>width</i> category.
     * </p>
     * <p>
     *   This sizing mode is best for dialogs whose content size changes dynamically,
     *   in response to user interaction, affecting both dimensions,
     *   but which do not honor filling the entire viewport,
     *   as in {@link DialogSizingMode#FULL_VIEWPORT}.
     * </p>
     */
    FILL_VIEWPORT( "ds-fill-viewport" ),

    /**
     * The dialog is sized to fully fill the viewport, both horizontally and vertically.
     * <p>
     *   The maximum <i>width</i> category has no effect.
     * </p>
     * <p>
     *   This sizing mode is best for complex and/or dynamic dialogs,
     *   whose content size changes dynamically, in response to user interaction,
     *   and when the user's attention should be completely focused on the task at hand.
     *   A good use case is that of wizards.
     * </p>
     */
    FULL_VIEWPORT( "ds-full-viewport" );

    private final String styleName;
    DialogSizingMode( String styleName ) {
      this.styleName = styleName;
    }

    public String getStyleName() {
      return styleName;
    }
  }

  /**
   * The dialog width categories.
   *
   * @see DialogBox#getWidthCategory()
   * @see DialogBox#setWidthCategory(DialogWidthCategory)
   */
  public enum DialogWidthCategory {
    /**
     * The size of the smallest viewport.
     * <p>
     * Currently, this matches the width which accessible dialogs must be able to fit in.
     * According to WCAG 2.1's "Reflow" criterion, a viewport whose width is 320px.
     * </p>
     */
    MINIMUM( "dw-min" ),

    /**
     * The size required for optimal text reading.
     * <p>
     *   Currently, this corresponds to the size of dialog whose content contains text
     *   having no more than 75 characters per line, to satisfy optimum readability constraints.
     *   This size is useful for message box dialogs.
     * </p>
     */
    TEXT( "dw-text" ),

    /**
     * A size which fits in an extra-small viewport.
     * <p>
     * An extra-small viewport has a width between 321px and 575px.
     * </p>
     */
    EXTRA_SMALL( "dw-xs" ),

    /**
     * A size which fits in a small viewport.
     * <p>
     * A small viewport has a width between 576px and 767px.
     * </p>
     */
    SMALL( "dw-sm" ),

    /**
     * A size which fits in a medium viewport.
     * <p>
     * A medium viewport has a width between 768px and 991px.
     * </p>
     */
    MEDIUM( "dw-md" ),

    /**
     * A size which fits in a large viewport.
     * <p>
     * A large viewport has a width between 992px and 1199px.
     * </p>
     */
    LARGE( "dw-lg" ),

    /**
     * A size which fits in an extra-large viewport.
     * <p>
     * An extra-large viewport has a width of at least 1200px.
     * </p>
     */
    EXTRA_LARGE( "dw-xl" ),

    /**
     * The size of the current viewport excluding any applicable design safe margins.
     */
    MAXIMUM( null );

    private final String styleName;

    DialogWidthCategory( String styleName ) {
      this.styleName = styleName;
    }

    public String getStyleName() {
      return styleName;
    }
  }

  /**
   * The dialog's minimum-height categories.
   * <p>
   *   When the height of the dialog is less than the specified minimum height,
   *   the dialog's outer vertical scrollbar is displayed.
   * </p>
   *
   * @see DialogBox#getMinimumHeightCategory()
   * @see DialogBox#setMinimumHeightCategory(DialogMinimumHeightCategory)
   */
  public enum DialogMinimumHeightCategory {
    /**
     * The height of the content.
     * <p>
     * In this mode, the dialog's vertical <i>body</i> scrollbar is effectively disabled,
     * and the dialog's <i>outer</i> vertical scrollbar is displayed
     * whenever the dialog's height cannot accommodate its body's minimum height.
     * </p>
     * <p>
     * Best for dialogs with small content, such as is usually the case in message box dialogs.
     * </p>
     */
    CONTENT( "dmh-content" ),

    /**
     * The height considered minimum to be able to interact with a dialog's body section.
     * <p>
     * In this mode,
     * when the dialog's height cannot accommodate its body's minimum height,
     * the dialog's vertical <i>body</i> scrollbar is displayed.
     * The dialog's <i>outer</i> vertical scrollbar is displayed
     * when the dialog's height is smaller than that of the minimum height category.
     * </p>
     */
    MINIMUM( null );

    private final String styleName;

    DialogMinimumHeightCategory( String styleName ) {
      this.styleName = styleName;
    }

    public String getStyleName() {
      return styleName;
    }
  }

  private static FocusPanel pageBackground = null;
  private static int clickCount = 0;
  private static int dialogDepthCount = 0;
  private FocusWidget focusWidget = null;
  private boolean responsive;
  private DialogSizingMode sizingMode = DialogSizingMode.SIZE_TO_CONTENT;
  private DialogWidthCategory widthCategory = DialogWidthCategory.MAXIMUM;
  private DialogMinimumHeightCategory minimumHeightCategory = DialogMinimumHeightCategory.MINIMUM;
  private HandlerRegistration resizeRegistration;

  public DialogBox() {
    this( false, true );
  }

  public DialogBox( boolean autoHide, boolean modal ) {
    super( autoHide, modal );

    addPopupListener( this );
    setStylePrimaryName( "pentaho-dialog" );

    // Other, non-GWT code also uses the class .pentaho-dialog for similar purposes.
    // This helps only applying rules for the case of Pentaho's GWT classes.
    addStyleName( "pentaho-gwt" );

    updateDomSizingMode( sizingMode, null );
    updateDomWidthCategory( widthCategory, null );
    updateMinimumHeightCategory( minimumHeightCategory, null );

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

  // region responsive property
  /**
   * Gets a value that indicates if the dialog is operating in responsive mode.
   */
  public boolean isResponsive() {
    return responsive;
  }

  /**
   * Sets a value that indicates if the dialog is operating in responsive mode.
   * @param responsive <code>true</code> if the dialog is responsive mode; <code>false</code>, otherwise.
   */
  public void setResponsive( boolean responsive ) {
    if ( this.responsive != responsive ) {
      this.responsive = responsive;
      if ( responsive ) {
        addStyleName( "responsive" );
      } else {
        removeStyleName( "responsive" );
      }
    }
  }
  // endregion

  // region sizingMode property
  /**
   * Gets the sizing mode of a responsive dialog.
   */
  public DialogSizingMode getSizingMode() {
    return sizingMode;
  }

  /**
   * Sets the sizing mode of a responsive dialog.
   *
   * <p>
   *   The default dialog sizing mode is {@link DialogSizingMode#SIZE_TO_CONTENT}.
   * </p>
   * <p>
   *   This property has no effect when the dialog is not set as responsive.
   * </p>
   *
   * @param sizingMode The new dialog sizing mode. Cannot be <code>null</code>.
   * @see #setWidthCategory(DialogWidthCategory)
   * @see #setResponsive(boolean)
   */
  public void setSizingMode( DialogSizingMode sizingMode ) {
    if ( sizingMode == null ) {
      throw new IllegalArgumentException( "Argument 'sizingMode' cannot be null." );
    }

    // When testing, due to using mocks, this.sizingMode can be `null`.
    if ( !sizingMode.equals( this.sizingMode ) ) {
      updateDomSizingMode( sizingMode, this.sizingMode );
      this.sizingMode = sizingMode;
    }
  }

  private void updateDomSizingMode( DialogSizingMode newSizingMode, DialogSizingMode oldSizingMode ) {
    replaceDomStyleName(
      newSizingMode.getStyleName(),
      oldSizingMode != null ? oldSizingMode.getStyleName() : null );
  }
  // endregion

  // region widthCategory property
  /**
   * Gets the width category of a responsive dialog.
   */
  public DialogWidthCategory getWidthCategory() {
    return widthCategory;
  }

  /**
   * Sets the width category of a responsive dialog.
   * <p>
   *   The default dialog width category is {@link DialogWidthCategory#MAXIMUM}.
   * </p>
   * <p>
   *   This property has no effect when the dialog is not set as responsive.
   * </p>
   * <p>
   *   When the sizing mode is {@link DialogSizingMode#FULL_VIEWPORT},
   *   this property is ignored.
   *   The dialog is sized to occupy the whole viewport.
   * </p>
   * <p>
   *   When the sizing mode is {@link DialogSizingMode#SIZE_TO_CONTENT},
   *   this property establishes the maximum width that the content may force the dialog to take.
   *   Whatever the width category,
   *   the width of a dialog is always implicitly constrained by {@link DialogWidthCategory#MAXIMUM}.
   * </p>
   * <p>
   *   When the sizing mode is {@link DialogSizingMode#FILL_VIEWPORT_WIDTH},
   *   this property establishes the actual width of the dialog.
   *   Whatever the width category,
   *   the width of a dialog is always implicitly constrained by {@link DialogWidthCategory#MAXIMUM}.
   * </p>
   * @param widthCategory The new width category. Cannot be <code>null</code>.
   * @see #setResponsive(boolean)
   */
  public void setWidthCategory( DialogWidthCategory widthCategory ) {
    if ( widthCategory == null ) {
      throw new IllegalArgumentException( "Argument 'widthCategory' cannot be null." );
    }

    if ( !this.widthCategory.equals( widthCategory ) ) {
      updateDomWidthCategory( widthCategory, this.widthCategory );
      this.widthCategory = widthCategory;
    }
  }

  private void updateDomWidthCategory( DialogWidthCategory newWidthCategory, DialogWidthCategory oldWidthCategory ) {
    replaceDomStyleName(
      newWidthCategory != null ? newWidthCategory.getStyleName() : null,
      oldWidthCategory != null ? oldWidthCategory.getStyleName() : null );
  }
  // endregion

  // region minimumHeightCategory property
  /**
   * Gets the minimum height category of a responsive dialog.
   */
  public DialogMinimumHeightCategory getMinimumHeightCategory() {
    return minimumHeightCategory;
  }

  /**
   * Sets the minimum height category of a responsive dialog.
   * <p>
   *   The default is {@link DialogMinimumHeightCategory#MINIMUM}.
   * </p>
   * <p>
   *   This property has no effect when the dialog is not set as responsive.
   * </p>
   * @param minimumHeightCategory The new minimum height category. Cannot be <code>null</code>.
   * @see #setResponsive(boolean)
   */
  public void setMinimumHeightCategory( DialogMinimumHeightCategory minimumHeightCategory ) {
    if ( minimumHeightCategory == null ) {
      throw new IllegalArgumentException( "Argument 'minimumHeightCategory' cannot be null." );
    }

    if ( !this.minimumHeightCategory.equals( minimumHeightCategory ) ) {
      updateMinimumHeightCategory( minimumHeightCategory, this.minimumHeightCategory );
      this.minimumHeightCategory = minimumHeightCategory;
    }
  }

  private void updateMinimumHeightCategory( DialogMinimumHeightCategory newMinimumHeightCategory,
                                            DialogMinimumHeightCategory oldMinimumHeightCategory ) {
    replaceDomStyleName(
      newMinimumHeightCategory != null ? newMinimumHeightCategory.getStyleName() : null,
      oldMinimumHeightCategory != null ? oldMinimumHeightCategory.getStyleName() : null );
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
    if ( pageBackground == null ) {
      pageBackground = new FocusPanel();
      pageBackground.setStyleName( "glasspane" );
      pageBackground.addClickListener( sender -> {
        clickCount++;
        if ( clickCount > 2 ) {
          clickCount = 0;
          pageBackground.setVisible( false );
        }
      } );
      RootPanel.get().add( pageBackground, 0, 0 );
    }
  }

  protected void block() {
    initializePageBackground();
    blockPageBackground();
  }

  void blockPageBackground() {
    pageBackground.setSize( "100%", "100%" );
    pageBackground.setVisible( true );
    pageBackground.getElement().getStyle().setDisplay( Display.BLOCK );
  }

  public void center() {
    super.center();

    // Needs to be repeated here due to dialog being not isVisible() when
    // `center()` calls `show()`.
    doAutoFocus();
  }

  @Override
  public void setPopupPositionAndShow( PositionCallback callback ) {
    super.setPopupPositionAndShow( callback );

    // Needs to be repeated here due to dialog being not isVisible() when
    // `setPopupPositionAndShow()` calls `show()`.
    doAutoFocus();
  }

  public Focusable getAutoFocusWidget() {
    if ( focusWidget != null ) {
      return focusWidget;
    }

    return getDefaultFocusWidget();
  }

  protected void doAutoFocus() {
    if ( isShowing() && isVisible() ) {
      Focusable autoFocusWidget = getAutoFocusWidget();
      if ( autoFocusWidget != null ) {
        autoFocusWidget.setFocus( true );
      }
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
    if ( isShowing() ) {
      return;
    }

    initializeResizeHandler();

    super.show();

    if ( isModal() ) {
      block();
      dialogDepthCount++;
    }

    doAutoFocus();

    toggleEmbedVisibility( false );

    // Notify listeners that we're showing a dialog (hide PDFs, flash).
    getGlassPane().show();
  }

  GlassPane getGlassPane() {
    return GlassPane.getInstance();
  }

  void toggleEmbedVisibility( boolean visible ) {
    // show oe hide <embeds>
    // TODO: migrate to GlassPane Listener
    FrameUtils.toggleEmbedVisibility( false );
  }

  void initializeResizeHandler() {
    if ( resizeRegistration == null ) {
      resizeRegistration = Window.addResizeHandler( DialogBox.this::onResize );
    }
  }

  void unregisterResizeHandler() {
    if ( resizeRegistration != null ) {
      resizeRegistration.removeHandler();
      resizeRegistration = null;
    }
  }

  protected void onResize( ResizeEvent event ) {
    center();
  }

  public void setFocusWidget( FocusWidget widget ) {
    focusWidget = widget;
    doAutoFocus();
  }

  @Override
  public void hide( boolean autoClosed ) {
    unregisterResizeHandler();

    super.hide( autoClosed );
  }

  public void onPopupClosed( PopupPanel sender, boolean autoClosed ) {
    if ( isModal() ) {
      dialogDepthCount--;
      if ( dialogDepthCount <= 0 ) {
        pageBackground.setVisible( false );

        // reshow <embeds>
        if ( this.isVisible() ) {
          toggleEmbedVisibility( true );
        }

        // just make sure it is zero
        dialogDepthCount = 0;
      }
    }

    // Notify listeners that we're hiding a dialog (re-show PDFs, flash).
    getGlassPane().hide();
  }

  protected static FocusPanel getPageBackground() {
    return pageBackground;
  }

  private void replaceDomStyleName( String newStyleName, String oldStyleName ) {
    if ( oldStyleName != null ) {
      removeStyleName( oldStyleName );
    }

    if ( newStyleName != null ) {
      addStyleName( newStyleName );
    }
  }
}
