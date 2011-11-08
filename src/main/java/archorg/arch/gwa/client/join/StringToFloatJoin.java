package archorg.arch.gwa.client.join;

import it.celi.research.balrog.beacon.Beacon;
import it.celi.research.balrog.claudenda.Claudenda;

import com.google.gwt.user.client.ui.HasValue;

public class StringToFloatJoin extends Join<String, Float>
{
  public static void join(Claudenda clau,
      HasValue<String> widget,
      Beacon<Float> beacon)
  {
    new StringToFloatJoin(clau, widget, beacon);
  }

  protected StringToFloatJoin(Claudenda clau,
      HasValue<String> widget,
      Beacon<Float> beacon)
  {
    super(clau, new StringToFloatConverter(), widget, beacon);
  }
}
