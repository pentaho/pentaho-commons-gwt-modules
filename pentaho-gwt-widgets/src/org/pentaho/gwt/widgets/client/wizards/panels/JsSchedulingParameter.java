package org.pentaho.gwt.widgets.client.wizards.panels;

import com.google.gwt.core.client.JavaScriptObject;

public class JsSchedulingParameter extends JavaScriptObject implements ISchedulingParameter {

  public final native String getName() /*-{
    return this.name;
  }-*/;

  public final native String getStringValue() /*-{
    return this.stringValue;
  }-*/;

  public final native void setName(String name) /*-{
    this.name = name;
  }-*/;

  public final native void setStringValue(String value) /*-{
    return this.stringValue = value;
  }-*/;

  public final native String getType() /*-{
    return this.type;
  }-*/;

  public final native void setType(String type) /*-{
    this.type = type;
  }-*/;
}
