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


package org.pentaho.gwt.widgets.client.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

public interface PentahoTabImages extends ImageBundle {

  public static final PentahoTabImages images = (PentahoTabImages) GWT.create( PentahoTabImages.class );

  AbstractImagePrototype tab_close_off();

  AbstractImagePrototype tab_close_on();

}
