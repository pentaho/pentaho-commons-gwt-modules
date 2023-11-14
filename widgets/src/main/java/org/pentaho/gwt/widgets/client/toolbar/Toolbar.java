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

package org.pentaho.gwt.widgets.client.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.pentaho.gwt.widgets.client.panel.HorizontalFlexPanel;
import org.pentaho.gwt.widgets.client.utils.ElementUtils;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a collection of buttons in a standard toolbar view. Also supports ToolbarGroup objects that manage related
 * buttons.
 *
 * @author nbaker
 */
public class Toolbar extends HorizontalFlexPanel implements ToolbarPopupListener, ToolbarPopupSource {

  public static final int SEPARATOR = 1;

  public static final int GLUE = 2;

  // table holding the buttons
  protected HorizontalPanel bar = new HorizontalFlexPanel();

  // Collection of buttons
  protected List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();

  // collection of groups
  protected List<ToolbarGroup> groups = new ArrayList<ToolbarGroup>();

  protected List<ToolbarPopupListener> popupListeners = new ArrayList<ToolbarPopupListener>();

  private JavaScriptObject jsToolbarController;

  public Toolbar() {
    // Clear base style first. Otherwise, calling setStylePrimaryName would only partially clear the base style,
    // because it is composed of multiple classes... Base style is added back right after.
    this.setStyleName( "" );
    this.setStylePrimaryName( "toolbar" ); //$NON-NLS-1$
    this.addStyleName( HorizontalFlexPanel.STYLE_NAME );

    this.setVerticalAlignment( ALIGN_MIDDLE );

    bar.setVerticalAlignment( ALIGN_MIDDLE );
    bar.setSpacing( 1 );
    bar.setWidth( "100%" );
    super.add( bar );
    super.setCellWidth( bar, "100%" );
    setWidth( "100%" ); //$NON-NLS-1$
  }

  @Override
  protected void onAttach() {
    super.onAttach();

    jsToolbarController = createJsToolbarController( this, getElement() );
  }

  @Override
  protected void onDetach() {
    if ( jsToolbarController != null ) {
      destroyJsToolbarController( jsToolbarController );
      jsToolbarController = null;
    }

    super.onDetach();
  }

  private static native JavaScriptObject createJsToolbarController( Toolbar toolbar, Element container )/*-{
    return $wnd.pho.util._a11y.makeAccessibleToolbar( container, {
      itemSelector: ".toolbar-button-focus-panel",
      itemFilter: function(itemElem) {
        return toolbar.@org.pentaho.gwt.widgets.client.toolbar.Toolbar::itemFilter(Lcom/google/gwt/dom/client/Element;)( itemElem );
      }
    });
  }-*/;


  private boolean itemFilter( Element element ) {
    Widget button = ElementUtils.getWidgetOfRootElement( element );
    if ( button != null ) {
      for ( ToolbarButton toolbarButton : buttons ) {
        if ( toolbarButton.getPushButton().equals( button ) ) {
          return toolbarButton.isEnabled() && toolbarButton.isVisible();
        }
      }
    }

    return false;
  }

  private static native void destroyJsToolbarController( JavaScriptObject jsToolbarController )/*-{
    jsToolbarController.destroy();
  }-*/;

  /**
   * Add in a collection of buttons assembled as a ToolbarGroup
   *
   * @param group
   *          ToolbarGroup to add.
   */
  public void add( ToolbarGroup group ) {

    // check to see if there's already a separator added before this group
    if ( bar.getWidgetCount() > 0 ) {
      if ( !( bar.getWidget( bar.getWidgetCount() - 1 ) instanceof Image ) ) {
        bar.add( group.getLeadingSeparator() );
        bar.setCellVerticalAlignment( group.getLeadingSeparator(), ALIGN_MIDDLE );
      }
    }

    // if the group has a label tag, add it before the buttons
    if ( group.getLabel() != null ) {
      bar.add( group.getGroupLabel() );
      bar.setCellVerticalAlignment( group.getGroupLabel(), ALIGN_MIDDLE );
    }

    // add the buttons to the bar and buttons collection
    for ( ToolbarButton btn : group.getButtons() ) {
      bar.add( btn.getPushButton() );
    }
    bar.add( group.getTrailingSeparator() );
    bar.setCellVerticalAlignment( group.getTrailingSeparator(), ALIGN_MIDDLE );
    groups.add( group );
  }

  /**
   * Add in a Label to a toolbar
   * 
   * @param group
   *          ToolbarGroup to add.
   */
  public void add( Label lbl ) {
    bar.add( lbl );
  }

  /**
   * Add a panel (spacer most likely) to the toolbar
   * 
   * @param panel
   *          ToolbarSpacer to add.
   */
  public void add( Panel p ) {
    bar.add( p );
    // spacer now passed in as panel
    if ( p instanceof SimplePanel ) {
      String flex = p.getElement().getAttribute( "flex" );
      if ( StringUtils.isEmpty( flex ) == false && Integer.parseInt( flex ) > 0 ) {
        bar.setCellWidth( p, "100%" );
      }
    }
  }

  /**
   * Add a Button to the Toolbar
   */
  public void add( ToolbarButton button ) {
    bar.add( button.getPushButton() );
    buttons.add( button );

    // register interest in popupPanel of the comboButtons
    if ( button instanceof ToolbarComboButton ) {
      ( (ToolbarComboButton) button ).addPopupPanelListener( this );
    }
  }

  /**
   * Add a special element to the toolbar. Support for separator and glue.
   * 
   * @param key
   *          id of element to add
   */
  public void add( int key ) {
    switch ( key ) {
      case Toolbar.SEPARATOR:
        Image img = new Image( GWT.getModuleBaseURL() + "images/toolbarDivider.png" );
        bar.add( img );
        bar.setCellVerticalAlignment( img, ALIGN_MIDDLE );
        break;
      case Toolbar.GLUE:
        SimplePanel panel = new SimplePanel();
        panel.setStyleName( "flex-auto" );

        bar.add( panel );
        bar.setCellWidth( panel, "100%" ); //$NON-NLS-1$
        break;
      default:
        // add error logging message
    }
  }

  /**
   * Add a special spacer element to the toolbar.
   * 
   * @param the
   *          amount of space to add
   */
  public void addSpacer( int spacerAmount ) {
    SimplePanel panel = new SimplePanel();
    panel.setWidth( spacerAmount + "px" ); //$NON-NLS-1$
    panel.addStyleName( "gwt-spacer" );
    bar.add( panel );
    bar.setCellWidth( panel, spacerAmount + "px" ); //$NON-NLS-1$
  }

  /**
   * Enable or Disable the toolbar. If passed in false it will disable all buttons, if true it will restore the buttons
   * to their previous state.
   * 
   * @param enabled
   *          boolean flag
   */
  public void setEnabled( boolean enabled ) {
    try {
      for ( ToolbarButton button : this.buttons ) {
        button.setEnabled( enabled );
        button.setTempDisabled( !enabled );
      }

      for ( ToolbarGroup gp : groups ) {
        gp.setEnabled( enabled );
        gp.setTempDisabled( !enabled );
      }

    } catch ( Exception e ) {
      System.out.println( "Error with Disable: " + e ); //$NON-NLS-1$
      e.printStackTrace( System.out );
    }
  }

  public void addPopupPanelListener( ToolbarPopupListener listener ) {
    if ( popupListeners.contains( listener ) == false ) {
      popupListeners.add( listener );
    }
  }

  public void removePopupPanelListener( ToolbarPopupListener listener ) {
    if ( popupListeners.contains( listener ) ) {
      popupListeners.remove( listener );
    }
  }

  public void popupClosed( PopupPanel panel ) {
    for ( ToolbarPopupListener listener : popupListeners ) {
      listener.popupClosed( panel );
    }
  }

  public void popupOpened( PopupPanel panel ) {
    for ( ToolbarPopupListener listener : popupListeners ) {
      listener.popupOpened( panel );
    }
  }

  public void removeAll() {
    bar.clear();
    buttons.clear();
  }

}
