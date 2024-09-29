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


package org.pentaho.gwt.widgets.client.filechooser.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.TreeImages;

public interface FileChooserImages extends ImageBundle, TreeImages {
  public static final FileChooserImages images = (FileChooserImages) GWT.create( FileChooserImages.class );

  AbstractImagePrototype file();

  AbstractImagePrototype file_waqr_report();

  AbstractImagePrototype file_pir_report();

  AbstractImagePrototype file_prpt_report();

  AbstractImagePrototype file_dashboard();

  AbstractImagePrototype file_analysis();

  AbstractImagePrototype file_analyzer();

  AbstractImagePrototype file_url();

  AbstractImagePrototype file_action();

  AbstractImagePrototype folder();

  AbstractImagePrototype up();

  AbstractImagePrototype search();
}
