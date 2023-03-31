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
 * Copyright (c) 2002-2023 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.gwt.widgets.client.wizards;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.pentaho.gwt.widgets.client.dialogs.DialogBox;
import org.pentaho.gwt.widgets.client.messages.Messages;
import org.pentaho.gwt.widgets.client.panel.HorizontalFlexPanel;

/**
 * @author wseyler
 * 
 *         Framework for creating Wizards
 */
@SuppressWarnings( "deprecation" )
public abstract class AbstractWizardDialog extends DialogBox implements IWizardPanelListener {

  private static final int STEPS_COUNT = 15; // Defines the height of the steps ListBox

  private static final String WIZARD_DIALOG = "pentaho-wizard-dialog";

  private static final String WIZARD_DECK_PANEL = "pentaho-wizard-deck-panel"; //$NON-NLS-1$

  private static final String WIZARD_BUTTON_PANEL = "pentaho-wizard-button-panel"; //$NON-NLS-1$

  private static final String BUTTON_PANEL = "button-panel";

  private static final String INNER_BUTTON_WRAPPER = "inner-button-wrapper";

  public static final String PENTAHO_BUTTON = "pentaho-button";

  private static final String DIALOG_CONTENT = "dialog-content";

  private static final String WIZARD_STEPS_LIST = "pentaho-wizard-steps-list";

  public enum ScheduleDialogType {
    SCHEDULER, BLOCKOUT
  }

  protected ScheduleDialogType dialogType;

  // gui elements
  protected Button backButton = new Button( Messages.getString( "dialog.button.back" ) );
  protected Button nextButton = new Button( Messages.getString( "dialog.button.next" ) );
  protected Button cancelButton = new Button( Messages.getString( "dialog.button.cancel" ) );
  protected Button finishButton = new Button( Messages.getString( "dialog.button.finish" ) );

  ListBox steps = new ListBox();
  protected DeckPanel wizardDeckPanel = new DeckPanel();
  VerticalPanel stepsList = new VerticalPanel();

  private IWizardPanel[] wizardPanels;

  private boolean canceled = false;

  public AbstractWizardDialog( ScheduleDialogType type, String title, IWizardPanel[] panels, boolean autohide,
      boolean modal ) {
    super( autohide, modal );

    dialogType = type;
    setText( title );

    init();
    layout();
    setWizardPanels( panels );
    show();
  }

  public ScheduleDialogType getDialogType() {
    return dialogType;
  }

  /**
   * Init()
   * 
   * Initialize the GUI Elements by setting up the required listeners and state NOTE: This method can be overridden to
   * provided new/additional functionality but should NEVER be called more than once during the lifecycle of the object
   */
  protected void init() {

    addStyleName( WIZARD_DIALOG );

    backButton.getElement().setId( "wizard-back-button" );
    nextButton.getElement().setId( "wizard-next-button" );
    cancelButton.getElement().setId( "wizard-cancel-button" );
    finishButton.getElement().setId( "wizard-finish-button" );

    backButton.setStyleName( PENTAHO_BUTTON );
    nextButton.setStyleName( PENTAHO_BUTTON );
    cancelButton.setStyleName( PENTAHO_BUTTON );
    finishButton.setStyleName( PENTAHO_BUTTON );

    nextButton.addClickListener( new ClickListener() {
      @Override
      public void onClick( Widget sender ) {
        nextClicked();
      }
    } );

    backButton.addClickListener( new ClickListener() {
      @Override
      public void onClick( Widget sender ) {
        backClicked();
      }
    } );

    cancelButton.addClickListener( new ClickListener() {
      @Override
      public void onClick( Widget sender ) {
        cancelClicked();
      }
    } );

    finishButton.addClickListener( new ClickListener() {
      @Override
      public void onClick( Widget sender ) {
        finishClicked();
      }
    } );

    steps.setEnabled( false );

  }

  protected boolean enableNext( int index ) {
    return ( (IWizardPanel) wizardDeckPanel.getWidget( index ) ).canContinue()
        && index < wizardDeckPanel.getWidgetCount() - 1;
  }

  protected boolean enableFinish( int index ) {
    return ( (IWizardPanel) wizardDeckPanel.getWidget( index ) ).canFinish();
  }

  protected boolean enableBack( int index ) {
    return index > 0;
  }

  protected boolean showNext( int index ) {
    return wizardDeckPanel.getWidgetCount() > 1;
  }

  protected boolean showBack( int index ) {
    return wizardDeckPanel.getWidgetCount() > 1;
  }

  protected boolean showFinish( int index ) {
    return true;
  }

  protected int getIndex() {
    return wizardDeckPanel.getVisibleWidget();
  }

  protected void nextClicked() {
    int oldIndex = steps.getSelectedIndex(); // The panel currently being displayed
    int newIndex = oldIndex + 1; // The panel that is going to be displayed
    // Get the actors (next and previous panels)
    IWizardPanel nextPanel = (IWizardPanel) wizardDeckPanel.getWidget( newIndex );
    IWizardPanel previousPanel = (IWizardPanel) wizardDeckPanel.getWidget( oldIndex );
    if ( !onNext( nextPanel, previousPanel ) ) {
      return;
    }
    // Update the Listeners
    previousPanel.removeWizardPanelListener( AbstractWizardDialog.this );
    nextPanel.addWizardPanelListener( AbstractWizardDialog.this );
    // Update the GUI with the current widget index;
    updateGUI( newIndex );
  }

  protected void backClicked() {
    int oldIndex = getIndex();
    int newIndex = oldIndex - 1; // The panel that is going to be displayed
    // Get the actors (next and previous panels)
    IWizardPanel previousPanel = (IWizardPanel) wizardDeckPanel.getWidget( newIndex );
    IWizardPanel currentPanel = (IWizardPanel) wizardDeckPanel.getWidget( oldIndex );
    if ( !onPrevious( previousPanel, currentPanel ) ) {
      return;
    }
    // Update the Listeners
    currentPanel.removeWizardPanelListener( AbstractWizardDialog.this );
    previousPanel.addWizardPanelListener( AbstractWizardDialog.this );
    // Update the GUI with the current widget index;
    updateGUI( newIndex );
  }

  protected void finishClicked() {
    if ( onFinish() ) {
      AbstractWizardDialog.this.hide();
    }
  }

  protected void cancelClicked() {
    canceled = true;
    AbstractWizardDialog.this.hide();
  }

  /**
   * @param index
   *          of the widget that will be shown.
   * 
   *          updateGUI(int index) sets up the panels and buttons based on the state of the widget (IWizardPanel) that
   *          will be shown (index).
   */
  protected void updateGUI( int index ) {
    stepsList.setVisible( wizardDeckPanel.getWidgetCount() > 1 );
    finishButton.setVisible( showFinish( index ) );
    backButton.setVisible( showBack( index ) );
    nextButton.setVisible( showNext( index ) );
    // Updates the selected step
    steps.setSelectedIndex( index );
    // Shows the current IWizardPanel
    wizardDeckPanel.showWidget( index );
    // Enables the next button if the current IWizardPanel can continue and we're not at the last IWizardPanel
    nextButton.setEnabled( enableNext( index ) );
    // Back button always enabled unless we're on the first IWizardPanel
    backButton.setEnabled( enableBack( index ) );
    // Current IWizardPanel can finish at any step.
    finishButton.setEnabled( enableFinish( index ) );

  }

  /**
   * layout()
   * 
   * Lays out the GUI elements. Should only be called ONCE during the objects lifecycle
   */
  protected void layout() {
    // Create the overall container to be displayed in the dialog

    SimplePanel deckWrapper = new SimplePanel();
    deckWrapper.setHeight( "100%" );
    deckWrapper.setWidth( "100%" );
    deckWrapper.setStyleName( DIALOG_CONTENT );

    DockPanel content = new DockPanel();

    // Create the Steps and add it to the content
    stepsList = new VerticalPanel();
    stepsList.addStyleName( WIZARD_STEPS_LIST );
    stepsList.add( new Label( Messages.getString( "dialog.steps" ) ) );
    steps.setVisibleItemCount( STEPS_COUNT );
    stepsList.add( steps );
    // steps.setSize("30%", "100%");
    content.add( stepsList, DockPanel.WEST );

    // Add the wizardPanels to the Deck and add the deck to the content
    // wizardDeckPanel.setSize("70%", "100%");
    deckWrapper.setWidget( wizardDeckPanel );
    content.add( deckWrapper, DockPanel.CENTER );
    wizardDeckPanel.addStyleName( WIZARD_DECK_PANEL );

    // Add the control buttons
    HorizontalPanel wizardButtonPanel = new HorizontalFlexPanel();
    wizardButtonPanel.setSpacing( 2 );
    // If we have only one button then we don't need to show the back and next button.
    wizardButtonPanel.add( backButton );
    wizardButtonPanel.add( nextButton );
    wizardButtonPanel.add( finishButton );
    wizardButtonPanel.add( cancelButton );
    wizardButtonPanel.addStyleName( INNER_BUTTON_WRAPPER );
    wizardButtonPanel.addStyleName( WIZARD_BUTTON_PANEL );

    HorizontalPanel wizardButtonPanelWrapper = new HorizontalFlexPanel();
    wizardButtonPanelWrapper.addStyleName( BUTTON_PANEL );

    wizardButtonPanelWrapper.setWidth( "100%" ); //$NON-NLS-1$
    wizardButtonPanelWrapper.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_RIGHT );
    wizardButtonPanelWrapper.setVerticalAlignment( HasVerticalAlignment.ALIGN_BOTTOM );
    wizardButtonPanelWrapper.add( wizardButtonPanel );

    content.add( wizardButtonPanelWrapper, DockPanel.SOUTH );
    content.setCellVerticalAlignment( wizardButtonPanelWrapper, HasVerticalAlignment.ALIGN_BOTTOM );

    // Add the content to the dialog
    add( content );
    content.setWidth( "100%" ); //$NON-NLS-1$
    content.setHeight( "100%" ); //$NON-NLS-1$
    content.setCellHeight( deckWrapper, "98%" );
  }

  /**
   * @param wizardPanels
   *          - IWizardPanel[]
   * 
   *          Creates a wizardDeckPanel with the contents of wizardPanels respecting the order. Creates a step panel
   *          populated with the step names from the wizardPanels and then sets the current wizard panel to the first
   *          panel in the list and updates the GUI.
   */
  public void setWizardPanels( IWizardPanel[] wizardPanels ) {
    this.wizardPanels = wizardPanels;

    steps.clear();
    wizardDeckPanel.clear();
    if ( wizardPanels != null && wizardPanels.length > 0 ) { // Add new wizardPanels
      for ( IWizardPanel panel : wizardPanels ) {
        steps.addItem( panel.getName() );
        wizardDeckPanel.add( (Widget) panel );
      }

      ( (IWizardPanel) wizardDeckPanel.getWidget( 0 ) ).addWizardPanelListener( this );
      if ( wizardPanels.length == 1 ) { // We only have one item so change the Finish button to ok.
        finishButton.setText( Messages.getString( "dialog.button.finish" ) );
      }

      updateGUI( 0 );
    }
  }

  /**
   * @return the current list if IWizardPanel in an array.
   */
  public IWizardPanel[] getWizardPanels() {
    return wizardPanels;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.pentaho.gwt.widgets.client.wizards.IWizardPanelListener#panelChanged(org.pentaho.gwt.widgets.client.wizards
   * .IWizardPanel)
   */
  @Override
  public void panelUpdated( IWizardPanel wizardPanel ) {
    int index = getIndex();
    nextButton.setEnabled( enableNext( index ) );
    finishButton.setEnabled( enableFinish( index ) );
  }

  @Override
  protected void onResize( ResizeEvent event ) {
    if ( wizardPanels != null ) {
      for ( IWizardPanel panel : wizardPanels ) {
        if( panel instanceof RequiresResize ) {
          ( (RequiresResize) panel).onResize();
        }
      }
    }

    super.onResize( event );
  }

  public boolean wasCancelled() {
    return canceled;
  }

  /**
   * abstract onFinish()
   * 
   * Override for action to take when user presses the finish button. Return true if the wizard dialog should close
   * after the finish() method completes.
   */
  protected abstract boolean onFinish();

  /**
   * @param nextPanel
   * @param previousPanel
   * @return boolean if the "Next" operation should complete.
   * 
   *         Users should return true if the Wizard should proceed to the next panel. This would be a good spot to
   *         retrieve/update state information between the two panels. This method is call before the next method
   *         executes (ie. next panel is displayed). If nothing needs to be done simply return true
   */
  protected abstract boolean onNext( IWizardPanel nextPanel, IWizardPanel previousPanel );

  /**
   * @param previousPanel
   * @param currentPanel
   * @return boolean if the "Back" operation should complete
   * 
   *         Users should return true if the Wizard should proceed to the next panel. This would be a good spot to
   *         retrieve/update state information between the two panels. This method is call before the back method
   *         executes (ie. previous panel is displayed). If nothing needs to be done simply return true;
   */
  protected abstract boolean onPrevious( IWizardPanel previousPanel, IWizardPanel currentPanel );
}
