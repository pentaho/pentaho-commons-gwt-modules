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


package org.pentaho.gwt.widgets.client.text;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.pentaho.gwt.widgets.client.buttons.ThemeableImageButton;
import org.pentaho.gwt.widgets.client.panel.HorizontalFlexPanel;
import org.pentaho.gwt.widgets.client.panel.ScrollFlexPanel;
import org.pentaho.gwt.widgets.client.panel.VerticalFlexPanel;
import org.pentaho.mantle.client.messages.Messages;


public class TextCopyToClipboard extends HorizontalFlexPanel {
  public static final String CLASSNAME = "gwt-text-copy-to-clipboard";

  private final String value;

  private final ThemeableImageButton copyButton = new ThemeableImageButton();
  private final Label copyLabel = new Label();
  private final VerticalPanel valueContainerPanel = new VerticalFlexPanel();
  private final ScrollPanel valueScrollPanel = new ScrollFlexPanel();

  public TextCopyToClipboard( String value ) {
    super();

    this.value = value;

    createUI();

    addStyleName( CLASSNAME );
    setWidth( "100%" );
  }

  /* Visible for testing */
  ThemeableImageButton getCopyButton() {
    return this.copyButton;
  }

  /* Visible for testing */
  Label getCopyLabel() {
    return this.copyLabel;
  }

  /* Visible for testing */
  VerticalPanel getValueContainerPanel() {
    return this.valueContainerPanel;
  }

  /* Visible for testing */
  ScrollPanel getValueScrollPanel() {
    return this.valueScrollPanel;
  }

  /* Visible for testing */
  void createUI() {
    ThemeableImageButton button = getCopyButton();
    button.addEnabledStyle( true, "pentaho-copy-text-button", "icon-zoomable" );
    button.setTitle( Messages.getString( "copy" ) );
    button.addClickHandler( event -> legacyCopyToClipboard( value ) );
    add( button );

    VerticalPanel valueContainer = getValueContainerPanel();
    valueContainer.addStyleName( "value-container" );

    Label label = getCopyLabel();
    label.setText( value );
    label.setStyleName( "typography typography-body" );
    valueContainer.add( label );

    ScrollPanel valueScroll = getValueScrollPanel();
    valueScroll.add( valueContainer );

    add( valueScroll );
  }

  private native void legacyCopyToClipboard( String text ) /*-{
    var textArea = document.createElement("textarea");

    textArea.style.position = "absolute";
    textArea.style.left = "-9000px";
    textArea.value = text;

    document.body.appendChild(textArea);
    textArea.select();

    try {
      document.execCommand("copy");
    } catch (err) {
      console.log("Unable to copy");
    }

    document.body.removeChild(textArea);
  }-*/;
}
