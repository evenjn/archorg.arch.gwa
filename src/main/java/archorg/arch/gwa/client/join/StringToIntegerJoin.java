package archorg.arch.gwa.client.join;

import it.celi.research.balrog.beacon.Beacon;
import it.celi.research.balrog.claudenda.Claudenda;

import com.google.gwt.user.client.ui.HasValue;

public class StringToIntegerJoin extends Join<String, Integer>
{
  public static void join(Claudenda clau,
      HasValue<String> widget,
      Beacon<Integer> beacon)
  {
    new StringToIntegerJoin(clau, widget, beacon);
  }

  protected StringToIntegerJoin(Claudenda clau,
      HasValue<String> widget,
      Beacon<Integer> beacon)
  {
    super(clau, new UglyStringToIntegerConverter(), widget, beacon);
  }
}
