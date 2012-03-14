package archorg.arch.gwa.client.join;

import it.celi.research.balrog.beacon.SimpleBeacon;

import com.google.gwt.user.client.ui.HasValue;

public class StringToFloatJoin
  extends
  Join<String, Float>
{
  public static void join(
    HasValue<String> widget,
    SimpleBeacon<Float> beacon)
  {
    new StringToFloatJoin(widget, beacon);
  }

  protected StringToFloatJoin(
    HasValue<String> widget,
    SimpleBeacon<Float> beacon)
  {
    super(new StringToFloatConverter(), widget, beacon);
  }
}
