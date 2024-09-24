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

import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Frame;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains methods to show/hide iframes if they contain PDFs (<embeds>). This is primarily of interest to Firefox
 * browsers who's Acrobat Plug-in does not work well with overlapping HTML elements.
 */
public class FrameUtils {

  private static Map<Frame, FrameTimer> timers = new HashMap<Frame, FrameTimer>();

  /**
   * Private method that does the actual hiding or showing of frames if an embed is in.
   * 
   * @param frame
   * @param visible
   */
  private static native void toggleEmbedVisibility( Element frame, boolean visible )/*-{
    if(! window.globalStorage){
      return;
    }
    try{
      var doc = (frame.contentWindow.document || frame.contentDocument);

      if(doc == null)
      {
        return;
      }

      var embeds = doc.getElementsByTagName("embed");
      if(embeds.length > 0){
        var containsPdf = false;
        for(var i=0; i< embeds.length; i++){
          if(embeds[i].type && embeds[i].type == "application/pdf"){
            containsPdf = true;
            break;
          }
        }
        if(containsPdf){
          if(visible){
            if(frame.style.display == "none"){   //don't do anything unless you need to
              frame.style.display = "" ;         //Show frame
              if (frame.style.height == "100%") {
                // force the frame to reload
                frame.style.height = "99%";
                frame.style.height = "100%";
              }

            }
          } else {
            frame.style.display = "none" ;  //hide frame
          }
        }
      }

      var iframes = doc.getElementsByTagName("iframe");
      if(iframes.length > 0){ // iframe has it's own iframes
        //recurse with child iframe
        for(var i=0; i<iframes.length; i++){
          @org.pentaho.gwt.widgets.client.utils.FrameUtils::toggleEmbedVisibility(Lcom/google/gwt/dom/client/Element;Z)(
              iframes[i], visible);
        }
      }

    } catch(e) {
      // Cross-site scripting error in all likelihood
    }
  }-*/;

  /**
   * If the frame contains an <embed> schedule it for showing/hiding
   * 
   * @param frame
   * @param visible
   */
  public static void setEmbedVisibility( Frame frame, boolean visible ) {
    if ( timers.containsKey( frame ) ) { // Already Scheduled
      FrameTimer t = timers.get( frame );
      if ( t.visible == visible ) { // dupe call, ignore
        return;
      } else { // timer exists but the visibility call has changed
        t.cancel();
        t.visible = visible;
        t.schedule( 200 );
      }
    } else { // Not currently in the operations queue
      FrameTimer t = new FrameTimer( frame, visible );
      timers.put( frame, t );
      t.schedule( 200 );
    }
  }

  /**
   * Loops through all iframe object on the document. Used when reference to Frame is not available.
   * 
   * @param visible
   */
  public static void toggleEmbedVisibility( boolean visible ) {
    Element[] frames = new Element[0];
    try {
      frames = ElementUtils.getElementsByTagName( "iframe" ); //$NON-NLS-1$
    } catch ( ClassCastException cce ) {
      // ignore class cast exceptions in here, they are happening in hosted mode for Elements
      //ignore
    }

    for ( Element ele : frames ) {
      Frame f = null;

      // Attempt to find a previously GWT-wrapped frame instance in our timer collection
      Object[] tmap = timers.entrySet().toArray();
      for ( int i = 0; i < tmap.length; i++ ) {
        @SuppressWarnings( "unchecked" )
        Map.Entry<Frame, FrameTimer> t = (Map.Entry<Frame, FrameTimer>) tmap[i];
        if ( t.getKey().getElement() == ele ) {
          // found an already wrapped instance
          f = t.getKey();
        }
      }


      if ( f == null ) {
        /* When in Super Dev Mode, `Frame.wrap( . )`, below, constantly fails due to an assertion error,
           causing this frame, and all following it, to not be handled.

           Additionally, this causes a mandatory stop in the debugger,
           given assert statements are transpiled into a JS `debugger;` statement.

           And, assertions cannot be disabled in Super Dev Mode...

           When in production mode, assertions are disabled and the `Frame.wrap( . )` operation (apparently) succeeds.
         */
        if ( GWTUtils.isSuperDevMode() && isElementChildOfWidget( ele ) ) {
          continue;
        }

        f = Frame.wrap( ele );
      }

      setEmbedVisibility( f, visible );
    }
  }

  // Copied from com.google.gwt.user.client.ui.RootPanel.isElementChildOfWidget( . )
  private static boolean isElementChildOfWidget( Element element ) {
    // Walk up the DOM hierarchy, looking for any widget with an event listener
    // set. Though it is not dependable in the general case that a widget will
    // have set its element's event listener at all times, it *is* dependable
    // if the widget is attached. Which it will be in this case.
    element = element.getParentElement();
    BodyElement body = Document.get().getBody();

    while ( element != null && body != element ) {
      if ( Event.getEventListener( element ) != null ) {
        return true;
      }

      element = element.getParentElement().cast();
    }

    return false;
  }

  /**
   * This Timer adds a buffer to show/hide operations so other code has a chance of "canceling" it.
   */
  private static class FrameTimer extends Timer {
    Frame frame;
    boolean visible;

    public FrameTimer( Frame frame, boolean visible ) {
      this.frame = frame;
      this.visible = visible;
    }

    public void run() {
      toggleEmbedVisibility( frame.getElement(), visible );
      timers.remove( frame );
    }
  }
}
