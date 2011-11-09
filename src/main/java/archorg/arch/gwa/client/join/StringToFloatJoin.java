package archorg.arch.gwa.client.join;

import it.celi.research.balrog.beacon.Beacon;

import com.google.gwt.user.client.ui.HasValue;

public class StringToFloatJoin extends Join<String, Float>
{
  public static void join(HasValue<String> widget,
      Beacon<Float> beacon)
  {
    new StringToFloatJoin(widget, beacon);
  }

  protected StringToFloatJoin(HasValue<String> widget,
      Beacon<Float> beacon)
  {
    super(new StringToFloatConverter(), widget, beacon);
  }
}
