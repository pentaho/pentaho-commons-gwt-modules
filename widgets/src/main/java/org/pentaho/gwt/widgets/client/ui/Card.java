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

package org.pentaho.gwt.widgets.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.pentaho.gwt.widgets.client.panel.HorizontalFlexPanel;
import org.pentaho.gwt.widgets.client.panel.VerticalFlexPanel;

import static org.pentaho.gwt.widgets.client.utils.ImageUtil.getThemeableImage;

public class Card extends VerticalFlexPanel {
  public static final String CLASSNAME = "gwt-card";
  public static final String EMPTY_DESCRIPTION = "&nbsp;";

  private final String status;
  private final String title;
  private final String subtitle;
  private final Widget content;

  private final HorizontalPanel headerPanel = new HorizontalFlexPanel();
  private final VerticalPanel headerTitlePanel = new VerticalFlexPanel();
  private final Label headerTitle = new Label();
  private final HTML headerSubtitle = new HTML();
  private final Image headerIcon = getThemeableImage( "icon-zoomable" );
  private final HorizontalPanel contentPanel = new HorizontalFlexPanel();

  public Card( String title, String description, Widget content ) {
    this( null, title, description, content );
  }

  public Card( String status, String title, String subtitle, Widget content ) {
    super();

    this.status = status;
    this.title = title;
    this.subtitle = subtitle;
    this.content = content;

    createUI();

    addStyleName( CLASSNAME );
    if ( status != null ) {
      addStyleName( "status-" + status );
    }
  }

  /* Visible for testing */
  HorizontalPanel getHeaderPanel() {
    return headerPanel;
  }

  /* Visible for testing */
  VerticalPanel getHeaderTitlePanel() {
    return headerTitlePanel;
  }

  /* Visible for testing */
  Label getHeaderTitle() {
    return headerTitle;
  }

  /* Visible for testing */
  HTML getHeaderSubtitle() {
    return headerSubtitle;
  }

  /* Visible for testing */
  Image getHeaderIcon() {
    return headerIcon;
  }

  /* Visible for testing */
  HorizontalPanel getContentPanel() {
    return contentPanel;
  }

  /* Visible for testing */
  void createUI() {
    add( createHeaderUI() );
    add( createContentUI() );
  }

  private HorizontalPanel createHeaderUI() {
    HorizontalPanel cardHeader = getHeaderPanel();
    cardHeader.addStyleName( "gwt-card-header" );

    VerticalPanel titlePanel = getHeaderTitlePanel();

    Label titleLabel = getHeaderTitle();
    titleLabel.setText( title );
    titleLabel.setStyleName( "typography typography-label" );
    titlePanel.add( titleLabel );

    HTML subtitleLabel = getHeaderSubtitle();
    if ( subtitle != null ) {
      subtitleLabel.setText( subtitle );
    } else {
      subtitleLabel.setHTML( EMPTY_DESCRIPTION );
    }
    subtitleLabel.setStyleName( "typography typography-caption-1" );
    titlePanel.add( subtitleLabel );
    cardHeader.add( titlePanel );

    if ( status != null ) {
      Image icon = getHeaderIcon();
      icon.addStyleName( "pentaho-status-" + status + "-button" );

      cardHeader.add( icon );
    }

    return cardHeader;
  }

  private HorizontalPanel createContentUI() {
    HorizontalPanel cardContent = getContentPanel();
    cardContent.addStyleName( "gwt-card-content" );
    cardContent.add( content );

    return cardContent;
  }
}
