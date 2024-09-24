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

import com.google.gwt.core.client.JavaScriptObject;

public class GlassPaneNativeListener implements GlassPaneListener {
  private JavaScriptObject callback;

  public GlassPaneNativeListener( JavaScriptObject callback ) {
    this.callback = callback;
  }

  public void glassPaneHidden() throws Exception {
    sendHide( callback );
  }

  public void glassPaneShown() throws Exception {
    sendShown( callback );
  }

  private native void sendHide( JavaScriptObject obj ) throws Exception/*-{
                                                                       try{
                                                                       obj.glassPaneHidden();
                                                                       } catch(e){
                                                                       throw e;
                                                                       }
                                                                       
                                                                       }-*/;

  private native void sendShown( JavaScriptObject obj ) throws Exception/*-{
                                                                        try{
                                                                        obj.glassPaneShown();
                                                                        } catch (e){
                                                                        throw e;
                                                                        }
                                                                        }-*/;
}
