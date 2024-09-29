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


package org.pentaho.gwt.widgets.client.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Represents a collection of related buttons in a {@linkplain Toolbar}. Buttons in the group can be disabled/enabled
 * and have their visibility changed together.
 * 
 * @author nbaker
 */
public class ToolbarGroup {

  protected List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
  protected boolean enabled = true;
  protected boolean visible = true;
  private String label = null;
  protected Image trailingSeparator = null;
  protected Image leadingSeparator = null;
  protected Label groupLabel = new Label();

  public static final String CSS_ENABLED = "toolbar-group-label"; //$NON-NLS-1$ 
  public static final String CSS_DISABLED = "toolbar-group-label-disabled"; //$NON-NLS-1$ 

  public ToolbarGroup() {
    groupLabel.setStyleName( CSS_ENABLED );
    String url = "mantle/style/images/toolbarDivider.png"; //$NON-NLS-1$
    if ( GWT.isScript() ) {
      String mypath = Window.Location.getPath();
      if ( !mypath.endsWith( "/" ) ) { //$NON-NLS-1$
        mypath = mypath.substring( 0, mypath.lastIndexOf( "/" ) + 1 ); //$NON-NLS-1$
      }
      mypath = mypath.replaceAll( "/mantle/", "/" ); //$NON-NLS-1$ //$NON-NLS-2$
      if ( !mypath.endsWith( "/" ) ) { //$NON-NLS-1$
        mypath = "/" + mypath; //$NON-NLS-1$
      }
      url = mypath + url;
    }
    trailingSeparator = new Image( url, 0, 0, 2, 16 );
    leadingSeparator = new Image( url, 0, 0, 2, 16 );
  }

  /**
   * Initialized the ToolbarGroup with a label description
   * 
   * @param groupName
   */
  public ToolbarGroup( String groupName ) {
    this();
    setLabel( groupName );
  }

  /**
   * Adds a {@link ToolbarButton} to this group.
   * 
   * @param btn
   *          ToolbarButton
   */
  public void add( ToolbarButton btn ) {
    if ( !buttons.contains( btn ) ) {
      buttons.add( btn );
    } else {
      // log error
      //ignore
    }
  }

  /**
   * Changes the enabled status of the group. If enabled is false, the buttons will be disabled. If enabled is true, it
   * will consult the buttons for their current enabled state.
   * 
   * @param enabled
   *          boolena flag
   */
  public void setEnabled( boolean enabled ) {
    if ( enabled == this.enabled ) { // no change
      return;
    }
    this.enabled = enabled;
    for ( ToolbarButton btn : buttons ) {
      btn.setEnabled( this.enabled );
    }
    this.groupLabel.setStyleName( ( this.enabled ) ? CSS_ENABLED : CSS_DISABLED );
  }

  public void setTempDisabled( boolean disable ) {

    for ( ToolbarButton btn : buttons ) {
      btn.setTempDisabled( disable );
    }
    this.groupLabel.setStyleName( ( disable ) ? CSS_DISABLED : CSS_ENABLED );

  }

  /**
   * Returns the enabled status of this group
   * 
   * @return boolean flag
   */
  public boolean isEnabled() {
    return this.enabled;
  }

  /**
   * Returns the collection of buttons managed by this ToolbarGroup.
   * 
   * @return List of ToolbarButtons
   */
  public List<ToolbarButton> getButtons() {
    return buttons;
  }

  /**
   * Returns the visibility of the group.
   * 
   * @return boolean flag
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Sets the visibility of the group. If visible is false the group will be hidden. If visible is true, the buttons
   * will be returned to their previous visible state.
   * 
   * @param visible
   *          boolean flag
   */
  public void setVisible( boolean visible ) {
    if ( visible == this.visible ) { // no change
      return;
    }

    this.visible = visible;
    for ( ToolbarButton btn : buttons ) {
      btn.setVisible( this.visible );
    }
    groupLabel.setVisible( this.visible );
    trailingSeparator.setVisible( this.visible );
    leadingSeparator.setVisible( this.visible );
  }

  /**
   * Returns the optional label to be displayed before the group buttons in the Toolbar.
   * 
   * @return String
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the optional label to be displayed before the group button in the Toolbar
   * 
   * @param label
   *          String to be used as a label
   */
  public void setLabel( String label ) {
    this.label = label;
    this.groupLabel.setText( this.label );
  }

  /**
   * Returns the image separator to be shown before the group. The ToolbarGroup manages this object so it can toggle
   * it's visibility.
   * 
   * @return Image
   */
  public Image getTrailingSeparator() {
    return trailingSeparator;
  }

  /**
   * Returns the image separator to be shown after the group. The ToolbarGroup manages this object so it can toggle it's
   * visibility.
   * 
   * @return Image
   */
  public Image getLeadingSeparator() {
    return leadingSeparator;
  }

  /**
   * Returns the Label object to be optionally displayed by the Toolbar. The ToolbarGroup manages this object so it can
   * toggle it's disabled/visible state.
   * 
   * @return Label
   */
  public Label getGroupLabel() {
    return groupLabel;
  }

}
