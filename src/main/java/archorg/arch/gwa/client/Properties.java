package archorg.arch.gwa.client;

public class Properties
{
  public static native String get(String s)
  /*-{
    return $wnd.client_properties[s];
  }-*/;
}
