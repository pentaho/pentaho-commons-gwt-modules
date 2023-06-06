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

package org.pentaho.gwt.widgets.client.listbox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.pentaho.gwt.widgets.client.panel.HorizontalFlexPanel;
import org.pentaho.gwt.widgets.client.utils.ElementUtils;

/**
 * 
 * User: Nick Baker Date: Mar 9, 2009 Time: 11:28:45 AM
 */
public class DefaultListItem extends HorizontalFlexPanel implements ListItem<Object> {

  private static final String DROP_INVALID_PNG = "drop_invalid.png"; //$NON-NLS-1$
  private static final String DROP_VALID_PNG = "drop_valid.png"; //$NON-NLS-1$
  public static final String HOVER = "hover"; //$NON-NLS-1$
  public static final String SELECTED = "selected";
  public static final String PROXY_VALID = "proxy-valid";
  private String text = ""; //$NON-NLS-1$
  protected Widget widget;
  protected Widget dropWidget;
  private Image img;
  private Widget extraWidget;
  private String baseStyleName = "custom-list"; //$NON-NLS-1$
  private Object value;
  protected ListItemListener listItemListener;
  private String styleName = "custom-list"; //$NON-NLS-1$
  private Object backingObject;
  protected Image dragIndicator;

  public DefaultListItem() {
    init();
  }

  public DefaultListItem( String str ) {
    this.text = str;
    this.value = this.text;
    init();
  }

  private void init() {
    createWidgets();
    this.sinkEvents( Event.MOUSEEVENTS );

    // Clear base style first. Otherwise, calling setStylePrimaryName would only partially clear the base style,
    // because it is composed of multiple classes... Base style is added back from within setStylePrimaryName.
    this.setStyleName( "" );
    this.setStylePrimaryName( styleName );
  }

  /**
   * Convenience constructor for creating a listItem with an Image followed by a string..
   * <p>
   * NOTE: The Image needs to have been constructed with a specified size (ie new Image("src.png",0,0,100,100);)
   * 
   * @param str
   * @param img
   */
  public DefaultListItem( String str, Image img ) {
    this.text = str;
    this.value = this.text;
    this.img = img;
    createWidgets();
    this.setStyleName( "" );
    this.setStylePrimaryName( styleName );
  }

  public DefaultListItem( String str, Widget widget ) {
    this.text = str;
    this.extraWidget = widget;
    createWidgets();
    this.setStyleName( "" );
    this.setStylePrimaryName( styleName );
  }

  public void setStylePrimaryName( String style ) {
    baseStyleName = style;
    dropWidget.setStylePrimaryName( style + "-item" );
    dropWidget.addStyleName( HorizontalFlexPanel.STYLE_NAME );//$NON-NLS-1$
    super.setStylePrimaryName( style + "-item" ); //$NON-NLS-1$
    super.addStyleName( HorizontalFlexPanel.STYLE_NAME );
  }

  /**
   * There are two widgets that need to be maintaned. One that shows in the drop-down when not opened, and another that
   * shows in the drop-down popup itself.
   */
  private void createWidgets() {

    formatWidget( this );
    widget = this;

    HorizontalPanel hbox = new HorizontalPanel();
    hbox.setStylePrimaryName( baseStyleName + "-item" ); //$NON-NLS-1$
    formatWidget( hbox );
    dropWidget = hbox;

  }

  private void formatWidget( HorizontalPanel panel ) {
    panel.sinkEvents( Event.MOUSEEVENTS );

    if ( img != null ) {
      Image i = new Image( img.getUrl(), img.getOriginLeft(), img.getOriginTop(), img.getWidth(), img.getHeight() );
      panel.add( i );
      panel.setCellVerticalAlignment( i, HasVerticalAlignment.ALIGN_MIDDLE );
      i.getElement().getStyle().setProperty( "marginRight", "5px" ); //$NON-NLS-1$ //$NON-NLS-2$
    } else if ( extraWidget != null ) {
      Element ele = DOM.clone( extraWidget.getElement(), true );
      Widget w = new WrapperWidget( ele );
      panel.add( w );
      panel.setCellVerticalAlignment( w, HasVerticalAlignment.ALIGN_MIDDLE );
      w.getElement().getStyle().setProperty( "marginRight", "5px" ); //$NON-NLS-1$ //$NON-NLS-2$
    }

    Label label = new Label( text );
    label.getElement().getStyle().setProperty( "cursor", "pointer" ); //$NON-NLS-1$ //$NON-NLS-2$
    label.setWidth( "100%" ); //$NON-NLS-1$
    label.setTitle( text );
    SimplePanel sp = new SimplePanel();
    sp.getElement().getStyle().setProperty( "overflowX", "auto" ); //$NON-NLS-1$ //$NON-NLS-2$
    sp.addStyleName( "flex-row" );
    sp.add( label );

    panel.add( sp );
    panel.setCellWidth( sp, "100%" ); //$NON-NLS-1$
    panel.setCellVerticalAlignment( label, HasVerticalAlignment.ALIGN_MIDDLE );

    ElementUtils.preventTextSelection( panel.getElement() );

    //    label.setStylePrimaryName("custom-list-item"); //$NON-NLS-1$
    panel.setWidth( "100%" ); //$NON-NLS-1$
  }

  public void onBrowserEvent( Event event ) {
    int code = event.getTypeInt();
    switch ( code ) {
      case Event.ONMOUSEOVER:
        this.addStyleDependentName( HOVER );
        break;
      case Event.ONMOUSEOUT:
        this.removeStyleDependentName( HOVER );
        break;
      case Event.ONMOUSEUP:
        listItemListener.itemSelected( DefaultListItem.this, event );
        this.removeStyleDependentName( HOVER );
        break;
      case Event.ONDBLCLICK:
        listItemListener.doAction( DefaultListItem.this );
      default:
        break;
    }
    super.onBrowserEvent( event );
  }

  public Widget getWidgetForDropdown() {
    return dropWidget;
  }

  public Widget getWidget() {
    return widget;
  }

  public Object getValue() {
    return this.value;
  }

  public void setValue( Object o ) {
    this.value = o;
  }

  public void onHoverEnter() {
  }

  public void onHoverExit() {
  }

  public void onSelect() {
    try {
      widget.addStyleDependentName( SELECTED ); //$NON-NLS-1$
    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }

  public void onDeselect() {
    try {
      widget.removeStyleDependentName( SELECTED ); //$NON-NLS-1$
    } catch ( Exception e ) {
      e.printStackTrace();

    }
  }

  public String getText() {
    return text;
  }

  public void setText( String text ) {
    this.text = text;
  }

  private static class WrapperWidget extends Widget {
    public WrapperWidget( Element ele ) {
      this.setElement( ele );
    }
  }

  public void setListItemListener( ListItemListener listener ) {
    this.listItemListener = listener;
  }

  /**
   * DND required methods below
   */
  public HandlerRegistration addMouseUpHandler( MouseUpHandler handler ) {
    return addDomHandler( handler, MouseUpEvent.getType() );
  }

  public HandlerRegistration addMouseOutHandler( MouseOutHandler handler ) {
    return addDomHandler( handler, MouseOutEvent.getType() );
  }

  public HandlerRegistration addMouseMoveHandler( MouseMoveHandler handler ) {
    return addDomHandler( handler, MouseMoveEvent.getType() );
  }

  public HandlerRegistration addMouseWheelHandler( MouseWheelHandler handler ) {
    return addDomHandler( handler, MouseWheelEvent.getType() );
  }

  public HandlerRegistration addMouseOverHandler( MouseOverHandler handler ) {
    return addDomHandler( handler, MouseOverEvent.getType() );
  }

  public HandlerRegistration addMouseDownHandler( MouseDownHandler handler ) {
    return addDomHandler( handler, MouseDownEvent.getType() );
  }

  private void makeDraggable() {
    clear();
    dragIndicator = new Image( GWT.getModuleBaseURL() + DROP_INVALID_PNG );
    add( dragIndicator );
    Label label = new Label( text );
    add( label );
    this.setCellWidth( dragIndicator, "16px" );
    this.setCellVerticalAlignment( dragIndicator, HasVerticalAlignment.ALIGN_MIDDLE );
    addStyleDependentName( "proxy" );
  }

  public void setDropValid( boolean valid ) {
    if ( valid ) {
      addStyleDependentName( PROXY_VALID );
      dragIndicator.setUrl( GWT.getModuleBaseURL() + DROP_VALID_PNG );
    } else {
      removeStyleDependentName( PROXY_VALID );
      dragIndicator.setUrl( GWT.getModuleBaseURL() + DROP_INVALID_PNG );
    }
  }

  public Widget makeProxy( Widget ele ) {
    DefaultListItem item = new DefaultListItem( this.getText() );
    item.setWidth( "20px" );
    item.makeDraggable();
    removeStyleDependentName( HOVER ); //$NON-NLS-1$
    return item;
  }

  public void setBackingObject( Object backingObject ) {
    this.backingObject = backingObject;
  }

  public Object getDragObject() {
    return backingObject;
  }

  public void notifyDragFinished() {
    // TODO: implement callbacks to support "move" operations
    // listItemListener.notifyDragFinished();
  }
}
