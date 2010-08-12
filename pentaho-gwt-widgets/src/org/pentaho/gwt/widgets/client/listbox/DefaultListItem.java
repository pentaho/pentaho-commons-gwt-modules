package org.pentaho.gwt.widgets.client.listbox;

import com.gargoylesoftware.htmlunit.javascript.regexp.HtmlUnitRegExpProxy;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import org.pentaho.gwt.widgets.client.utils.ElementUtils;

/**
 * 
 * User: Nick Baker
 * Date: Mar 9, 2009
 * Time: 11:28:45 AM
 */
public class DefaultListItem extends HorizontalPanel implements ListItem<Object> {

  private String text = ""; //$NON-NLS-1$
  private Widget widget;
  private Widget dropWidget;
  private Image img;
  private Widget extraWidget;
  private String baseStyleName = "custom-list"; //$NON-NLS-1$
  private Object value;
  private ListItemListener listItemListener;
  private String styleName = "custom-list";
  private Object backingObject;
  private Image dragIndicator;

  public DefaultListItem(){
    init();
  }

  public DefaultListItem(String str){
    this.text = str;
    this.value = this.text;
    init();
  }

  private void init(){
    createWidgets();
    this.sinkEvents(Event.MOUSEEVENTS);
    this.setStylePrimaryName(styleName);
  }


  /**
   * Convenience constructor for creating a listItem with an Image followed by a string..
   * <p>
   * NOTE: The Image needs to have been constructed with a specified size (ie new Image("src.png",0,0,100,100);)
   *
   * @param str
   * @param img
   */
  public DefaultListItem(String str, Image img){
    this();
    this.text = str;
    this.value = this.text;
    this.img = img;
    createWidgets();
  }

  public DefaultListItem(String str, Widget widget){
    this();
    this.text = str;
    this.extraWidget = widget;
    createWidgets();
  }

  public void setStylePrimaryName(String style){
    baseStyleName = style;
    dropWidget.setStylePrimaryName(style+"-item"); //$NON-NLS-1$
    super.setStylePrimaryName(style+"-item"); //$NON-NLS-1$

  }

  /**
   * There are two widgets that need to be maintaned. One that shows in the drop-down when not opened, and
   * another that shows in the drop-down popup itself.
   */
  private void createWidgets(){

    formatWidget(this);
    widget = this;

    HorizontalPanel hbox = new HorizontalPanel();
    hbox.setStylePrimaryName(baseStyleName+"-item"); //$NON-NLS-1$
    formatWidget(hbox);
    dropWidget = hbox;

  }

  private void formatWidget(HorizontalPanel panel){
    panel.sinkEvents(Event.MOUSEEVENTS);

    if(img != null){
      Image i = new Image(img.getUrl(), img.getOriginLeft(), img.getOriginTop(), img.getWidth(), img.getHeight());
      panel.add(i);
      panel.setCellVerticalAlignment(i, HasVerticalAlignment.ALIGN_MIDDLE);
      i.getElement().getStyle().setProperty("marginRight","5px"); //$NON-NLS-1$ //$NON-NLS-2$
    } else if(extraWidget != null){
      Element ele = DOM.clone(extraWidget.getElement(), true);
      Widget w = new WrapperWidget(ele);
      panel.add(w);
      panel.setCellVerticalAlignment(w, HasVerticalAlignment.ALIGN_MIDDLE);
      w.getElement().getStyle().setProperty("marginRight","5px"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    Label label = new Label(text);
    label.getElement().getStyle().setProperty("cursor","pointer"); //$NON-NLS-1$ //$NON-NLS-2$
    label.setWidth("100%"); //$NON-NLS-1$
    SimplePanel sp = new SimplePanel();
    sp.getElement().getStyle().setProperty("overflowX","auto"); //$NON-NLS-1$ //$NON-NLS-2$
    sp.add(label);


    panel.add(sp);
    panel.setCellWidth(sp, "100%"); //$NON-NLS-1$
    panel.setCellVerticalAlignment(label, HasVerticalAlignment.ALIGN_MIDDLE);

    ElementUtils.preventTextSelection(panel.getElement());

//    label.setStylePrimaryName("custom-list-item"); //$NON-NLS-1$
    panel.setWidth("100%"); //$NON-NLS-1$
  }

  public void onBrowserEvent(Event event) {
    int code = event.getTypeInt();
    switch(code){
      case Event.ONMOUSEOVER:
        this.addStyleDependentName("hover"); //$NON-NLS-1$
        break;
      case Event.ONMOUSEOUT:
        this.removeStyleDependentName("hover"); //$NON-NLS-1$
        break;
      case Event.ONMOUSEUP:
        listItemListener.itemSelected(DefaultListItem.this);
        this.removeStyleDependentName("hover"); //$NON-NLS-1$
      case Event.ONDBLCLICK:
        listItemListener.doAction(DefaultListItem.this);
      default:
        break;
    }
    super.onBrowserEvent(event);
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

  public void setValue(Object o) {
    this.value = o;
  }

  public void onHoverEnter() {
  }

  public void onHoverExit() {
  }

  public void onSelect() {
    try{
    widget.addStyleDependentName("selected"); //$NON-NLS-1$
    } catch(Exception e){
      e.printStackTrace();
    }
  }

  public void onDeselect() {
    try{
      widget.removeStyleDependentName("selected"); //$NON-NLS-1$
    } catch(Exception e){
      e.printStackTrace();
      
    }
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  private static class WrapperWidget extends Widget{
    public WrapperWidget(Element ele){
      this.setElement(ele);
    }
  }

  public void setListItemListener(ListItemListener listener) {
    this.listItemListener = listener;
  }


  /**
   * DND required methods below
   */
  public HandlerRegistration addMouseUpHandler( MouseUpHandler handler ) {
    return addDomHandler(handler, MouseUpEvent.getType());
  }

  public HandlerRegistration addMouseOutHandler( MouseOutHandler handler ) {
    return addDomHandler(handler, MouseOutEvent.getType());
  }

  public HandlerRegistration addMouseMoveHandler( MouseMoveHandler handler ) {
    return addDomHandler(handler, MouseMoveEvent.getType());
  }

  public HandlerRegistration addMouseWheelHandler( MouseWheelHandler handler ) {
    return addDomHandler(handler, MouseWheelEvent.getType());
  }

  public HandlerRegistration addMouseOverHandler( MouseOverHandler handler ) {
    return addDomHandler(handler, MouseOverEvent.getType());
  }

  public HandlerRegistration addMouseDownHandler( MouseDownHandler handler ) {
    return addDomHandler(handler, MouseDownEvent.getType());
  }

  private void makeDraggable(){
    clear();
    dragIndicator = new Image("drop_invalid.png");
    add(dragIndicator);
    Label label = new Label(text);
    add(label);
    this.setCellWidth(dragIndicator,"16px");
    this.setCellVerticalAlignment(dragIndicator, HasVerticalAlignment.ALIGN_MIDDLE);
    addStyleDependentName("proxy");
  }

  public void setDropValid(boolean valid){
    if(valid){
      addStyleDependentName("proxy-valid");
      dragIndicator.setUrl("drop_valid.png");
    } else {
      removeStyleDependentName("proxy-valid");
      dragIndicator.setUrl("drop_invalid.png");
    }
  }

  public Widget makeProxy(Widget ele) {
    DefaultListItem item = new DefaultListItem(this.getText());
    item.setWidth(this.getElement().getOffsetWidth()+"px");
    item.makeDraggable();
    removeStyleDependentName("hover"); //$NON-NLS-1$
    return item;
  }

  public void setBackingObject(Object backingObject) {
    this.backingObject = backingObject;
  }

  public Object getDragObject() {
    return backingObject;
  }

  public void notifyDragFinished() {
    // TODO: implement callbacks to support "move" operations
    //listItemListener.notifyDragFinished();
  }
}
