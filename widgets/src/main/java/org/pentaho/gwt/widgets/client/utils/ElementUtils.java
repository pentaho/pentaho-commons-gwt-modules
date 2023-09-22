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

package org.pentaho.gwt.widgets.client.utils;

import com.google.gwt.aria.client.Id;
import com.google.gwt.aria.client.Property;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalSplitPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.FocusImpl;

import static org.pentaho.gwt.widgets.client.utils.string.StringUtils.isEmpty;

public class ElementUtils {

  public static final int LEFT = 0;
  public static final int RIGHT = 1;
  public static final int TOP = 0;
  public static final int BOTTOM = 1;
  private static AbsolutePanel sandbox = new AbsolutePanel(); // Used to find the size of elements

  static {
    // RootPanel.get().add( . ) fails when running Java unit tests.
    // GWT.isClient() is false when GWT is running in Java.
    if ( GWT.isClient() ) {
      sandbox.getElement().getStyle().setProperty( "position", "absolute" ); //$NON-NLS-1$ //$NON-NLS-2$
      sandbox.getElement().getStyle().setProperty( "overflow", "hidden" ); //$NON-NLS-1$ //$NON-NLS-2$
      sandbox.getElement().getStyle().setProperty( "width", "0px" ); //$NON-NLS-1$ //$NON-NLS-2$
      sandbox.getElement().getStyle().setProperty( "height", "0px" ); //$NON-NLS-1$ //$NON-NLS-2$

      RootPanel.get().add( sandbox );
    }
  }

  public static native void blur( Element e )/*-{
    if(e.blur){
      e.blur();
    }
  }-*/;

  /**
   * This method handles the click event using element
   * @param e
   */
  public static native void click( Element e )/*-{
    if ( e.click ) {
      e.click();
    }
  }-*/;

  public static void removeScrollingFromSplitPane( Widget panel ) {
    if ( !panel.isAttached() ) {
      // throw new IllegalStateException("Operation not allowed while element not on DOM");
    }

    if ( ( panel instanceof HorizontalSplitPanel || panel instanceof VerticalSplitPanel ) == false ) {
      throw new IllegalArgumentException( "Widget not expected SplitPane type" ); //$NON-NLS-1$
    }

    if ( panel instanceof HorizontalSplitPanel ) {
      HorizontalSplitPanel hp = (HorizontalSplitPanel) panel;
      removeScrollingFromUpTo( hp.getLeftWidget().getElement(), hp.getElement() );
      removeScrollingFromUpTo( hp.getRightWidget().getElement(), hp.getElement() );
    } else {
      VerticalSplitPanel vp = (VerticalSplitPanel) panel;
      removeScrollingFromUpTo( vp.getTopWidget().getElement(), vp.getElement() );
      removeScrollingFromUpTo( vp.getBottomWidget().getElement(), vp.getElement() );
    }

  }

  public static void removeScrollingFromUpTo( Element bottom, Element top ) {

    Element ele = bottom;
    while ( ele != top && ele.getParentElement() != null ) {
      ele.getStyle().setProperty( "overflow", "visible" ); //$NON-NLS-1$ //$NON-NLS-2$
      ele.getStyle().setProperty( "overflowX", "visible" ); //$NON-NLS-1$ //$NON-NLS-2$
      ele.getStyle().setProperty( "overflowY", "visible" ); //$NON-NLS-1$ //$NON-NLS-2$
      ele = ele.getParentElement();
    }
  }

  /**
   * Gets the widget which is listening to events on the given root element.
   * <p>
   *   Widgets are associated with their root element on attachment (see <code>Widget#onAttach()</code>).
   * </p>
   * <p>
   *   Based on https://stackoverflow.com/a/17863305/178749.
   * </p>
   * @param element The widget's root element.
   * @return The widget, if one is associated with the <code>element</code>;
   *         <code>null</code>, otherwise.
   */
  public static Widget getWidgetOfRootElement( Element element ) {
    EventListener listener = DOM.getEventListener( element );
    return ( listener instanceof Widget ) ?  (Widget) listener : null;
  }

  public static void killAutoScrolling( Element ele ) {
    ele.getStyle().setProperty( "overflow", "visible" ); //$NON-NLS-1$ //$NON-NLS-2$
    if ( ele.hasChildNodes() ) {

      NodeList<Node> nodes = ele.getChildNodes();
      for ( int i = 0; i < nodes.getLength(); i++ ) {
        Node n = nodes.getItem( i );
        if ( n instanceof Element ) {
          killAutoScrolling( (Element) n );
        }
      }
    }
  }

  public static void killAllTextSelection( com.google.gwt.dom.client.Element item ) {
    ElementUtils.preventTextSelection( item );
    com.google.gwt.dom.client.NodeList<Node> children = item.getChildNodes();
    for ( int i = 0; i < children.getLength(); i++ ) {
      killAllTextSelection( (com.google.gwt.dom.client.Element) children.getItem( i ) );
    }
  }

  public static native void preventTextSelection( Element ele ) /*-{
                                                                // Handle all 3 browser types
                                                                if(ele.getAttribute('style') != null){
                                                                var isWebkit = 'webkitRequestAnimationFrame' in $wnd;
                                                                // IE (note: IE10 already supports msUserSelect style)
                                                                if(document.all){
                                                                ele.onselectstart=function() {return false};
                                                                }
                                                                //Webkit
                                                                else if(isWebkit){
                                                                ele.style.webkitUserSelect='none';
                                                                }
                                                                //Firefox
                                                                else {
                                                                ele.style.MozUserSelect='none';
                                                                }
                                                                }
                                                                }-*/;

  public static native Element[] getElementsByTagName( String name )/*-{
                                                                    return $doc.getElementsByTagName(name);
                                                                    }-*/;

  public static native void convertPNGs()
  /*-{
    try {
      if ($wnd.fixPNGs) {
        $wnd.fixPNGs();
      }
    } catch(e){alert("pngfix died: "+e);}
  }-*/;

  public static native int[] calculateScrollOffsets( Element e )/*-{
                                                                var x=0;
                                                                var y=0;
                                                                while(e.offsetParent != null){
                                                                x += e.scrollLeft;
                                                                y += e.scrollTop;
                                                                e = e.offsetParent;
                                                                }
                                                                return [x,y];
                                                                }-*/;

  public static native int[] calculateOffsets( Element e )/*-{
                                                          var x=0;
                                                          var y=0;
                                                          while(e.offsetParent != null){
                                                          x += e.offsetLeft;
                                                          y += e.offsetTop;
                                                          e = e.offsetParent;
                                                          }
                                                          return [x,y];
                                                          }-*/;

  public static Element findElementAboveByTagName( Element base, String targetName ) {

    Element curEle = base;
    Element parent;
    while ( ( parent = curEle.getParentElement() ) != null ) {
      if ( parent.getTagName().equalsIgnoreCase( targetName ) ) {
        return parent;
      }
      curEle = parent;
    }
    return null;
  }

  public static boolean elementsOverlap( Element ele1, Element ele2 ) {

    Rectangle r1 =
        new Rectangle( ele1.getAbsoluteLeft(), ele1.getAbsoluteTop(), ele1.getOffsetWidth(), ele1.getOffsetHeight() );

    Rectangle r2 =
        new Rectangle( ele2.getAbsoluteLeft(), ele2.getAbsoluteTop(), ele2.getOffsetWidth(), ele2.getOffsetHeight() );

    return r1.intersects( r2 );

  }

  public static Rectangle getSize( com.google.gwt.user.client.Element ele ) {
    Rectangle r = new Rectangle();
    r.width = ele.getOffsetWidth();
    r.height = ele.getOffsetHeight();

    // If the element is not on the DOM, or not visible, browsers may not be able to calculate the size
    // Clone the element and put it in the "sandbox" to grab the size.
    if ( r.width == 0 && r.height == 0 ) {
      com.google.gwt.user.client.Element e = DOM.clone( ele, true );
      sandbox.getElement().appendChild( e );
      r.width = e.getOffsetWidth();
      r.height = e.getOffsetHeight();

      sandbox.getElement().removeChild( e );
    }
    return r;
  }

  public static boolean isVisible(Element ele) {
    if ( ele.getStyle().getProperty( "display" ).equals( "none" ) ) { //$NON-NLS-1$ //$NON-NLS-2$
      return false;
    }
    if ( ele.getStyle().getProperty( "visibility" ).equals( "hidden" ) ) { //$NON-NLS-1$ //$NON-NLS-2$
      return false;
    }
    Element parentElement = ele.getParentElement();
    if( parentElement != null
            && !parentElement.equals(parentElement.getOwnerDocument().getDocumentElement())) {
      return isVisible( parentElement );
    }
    // TODO: add scrollpanel checking here
    return true;

  }

  /**
   * Gets the first descendant element of the given element which can currently receive keyboard focus.
   * @param root The root element.
   * @return The first keyboard-focusable descendant, if any; <code>null</code>, otherwise.
   */
  public static native Element findFirstKeyboardFocusableDescendant( Element root )/*-{
    return $wnd.pho.util._focus.firstTabbable(root);
  }-*/;

  /**
   * Gets the element after the given one which can currently receive keyboard focus.
   * @param elem The initial element.
   * @return The next keyboard-focusable descendant, if any; <code>null</code>, otherwise.
   */
  public static native Element findNextKeyboardFocusableElement( Element elem )/*-{
    return $wnd.pho.util._focus.nextTabbable(elem);
  }-*/;

  /**
   * Gets the element before the given one which can currently receive keyboard focus.
   * @param elem The initial element.
   * @return The previous keyboard-focusable descendant, if any; <code>null</code>, otherwise.
   */
  public static native Element findPreviousKeyboardFocusableElement( Element elem )/*-{
    return $wnd.pho.util._focus.previousTabbable(elem);
  }-*/;

  /**
   * Gets a value that indicates if the element can currently receive focus, via keyboard or code.
   * @param elem The element.
   * @return <code>true</code> if it can receive focus; <code>false</code>, otherwise.
   */
  public static native boolean isFocusable( Element elem )/*-{
    return $wnd.pho.util._focus.isTabbable(elem, {focusable: true});
  }-*/;

  /**
   * Sets focus on a given element.
   * <p>
   *   On some user agents, e.g. <code>safari</code>,
   *   focus is set asynchronously (see <code>FocusImplSafari</code>).
   * </p>
   * <p>
   *   The focus implementation obtained by {@link FocusImpl#getFocusImplForPanel()} is used.
   *   This is the focus implementation used, for example, by MenuBar.
   *   While other GWT classes use {@link FocusImpl#getFocusImplForWidget()}, instead,
   *   by using the former, there's no danger of being overtaken by other GWT code executed before this call.
   * </p>
   * @param elem The element to focus.
   */
  public static void focus( Element elem ) {
    FocusImpl.getFocusImplForPanel().focus( elem );
  }

  public static void tabNext( Element rootElem ) {
    Element elem = ElementUtils.findNextKeyboardFocusableElement( rootElem );
    if ( elem != null ) {
      focus( elem );
    }
  }

  public static void tabPrevious( Element rootElem ) {
    Element elem = ElementUtils.findPreviousKeyboardFocusableElement( rootElem );
    if ( elem != null ) {
      focus( elem );
    }
  }

  public static void setupButtonHoverEffect() {
    setupHoverEffectJS();
  }

  private static native void setupHoverEffectJS()/*-{
    if(!$wnd.jQuery || !$wnd.setupJsButtonHover){
      return;
    }
    try{
      $wnd.setupJsButtonHover();
    } catch(e){
      alert(e);
    }
  }-*/;

  public static native String getComputedStyle( Element ele, String styleName )/*-{
    if(window.getComputedStyle) {
      var style = window.getComputedStyle(ele, null);
      return style.getPropertyValue(styleName);
    } else {
      if (ele.currentStyle && ele.currentStyle[styleName]) {
        return ele.currentStyle[styleName];
      }
    }
    return "";
  }-*/;

  /**
   * Sets a style property using the CSS style property name.
   * <p>
   *   GWT's {@link com.google.gwt.dom.client.Style#setProperty(String, String)} only
   *   allows setting properties with its camel-case name.
   * </p>
   * @param elem The element.
   * @param name The name of the CSS style property.
   * @param value The value of the property.
   */
  public static native void setStyleProperty( Element elem, String name, String value ) /*-{
    elem.style.setProperty(name, value);
  }-*/;

  /**
   * Set's a widget's ARIA label.
   * <p>If the provided label widget does not have an identifier, a unique one is assigned to it.</p>
   * @param widget The widget.
   * @param label The label.
   */
  public static void setAriaLabelledBy( Widget widget, Label label ) {
    Property.LABELLEDBY.set( widget.getElement(), Id.of( ensureId( label ) ) );
  }

  /**
   * Ensures that a given widget has an identifier, assigning it a unique one, if necessary.
   * @param widget The widget.
   * @return The widget's identifier.
   */
  public static String ensureId( Widget widget ) {
    com.google.gwt.user.client.Element element = widget.getElement();
    String id = element.getId();
    if ( isEmpty( id ) ) {
      id = DOM.createUniqueId();
      element.setId( id );
    }

    return id;
  }

  /**
   * This method validates whether provided element is active element in the browser
   * @param e
   * @return boolean
   */
  public static native boolean isActiveElement( Element e )/*-{
    return $doc.activeElement === e;
  }-*/;

  /**
   * This method sets the focus on the provided element
   * @param elem
   */
  public static native void focusSync( Element elem )/*-{
    elem.focus();
  }-*/;

  /**
   * Scrolls an element's scroll container so that it is in view.
   * <p>
   *   The element's scroll container is scrolled down,
   *   if the element is off view, below the container's bottom position.
   *   Conversely, the scroll container is scrolled up,
   *   if the element is off view, above the container's top position.
   * </p>
   * @param elem The element to scroll into view.
   */
  public static native void scrollVerticallyIntoView( Element elem ) /*-{
    var item = elem;
    var container = elem.offsetParent;

    // In the coordinates of `container`.
    var offsetTop = 0;
    if (item !== container) {
      offsetTop = item.offsetTop;
    }

    var elemRect = elem.getBoundingClientRect();
    if (offsetTop < container.scrollTop) {
      container.scrollTop = offsetTop;
    } else if ( offsetTop + elemRect.height > container.scrollTop + container.clientHeight ) {
      container.scrollTop = offsetTop +  elemRect.height - container.clientHeight;
    }
  }-*/;

  /**
   * Indicates whether the call to <code>Event.preventDefault()</code> canceled the event.
   * @param evt The NativeEvent.
   * @return <code>true</code> if the default event was prevented; <code>false</code>, otherwise.
   */
  public static native boolean isEventDefaultPrevented( NativeEvent evt ) /*-{
    return evt.defaultPrevented;
  }-*/;

  /**
   * This method is used for providing a log text on the browser console
   * @param text The Text to be logged on console.
   */
  public static native void log( String text ) /*-{
    $wnd.console.log( text );
  }-*/;
}
