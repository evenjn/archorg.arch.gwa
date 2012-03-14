package archorg.arch.gwa.client.join;

import it.celi.research.balrog.beacon.SimpleBeacon;

import com.google.gwt.user.client.ui.HasValue;

public class StringToIntegerJoin
  extends
  Join<String, Integer>
{
  public static void join(
    HasValue<String> widget,
    SimpleBeacon<Integer> beacon)
  {
    new StringToIntegerJoin(widget, beacon);
  }

  protected StringToIntegerJoin(
    HasValue<String> widget,
    SimpleBeacon<Integer> beacon)
  {
    super(new UglyStringToIntegerConverter(), widget, beacon);
  }
}
