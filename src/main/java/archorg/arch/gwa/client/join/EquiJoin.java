package archorg.arch.gwa.client.join;

import it.celi.research.balrog.beacon.SimpleBeacon;

import com.google.gwt.user.client.ui.HasValue;

public class EquiJoin
{
  public static <X> void join(
    final HasValue<X> widget,
    final SimpleBeacon<X> beacon)
  {
    new Join<X, X>(new Converter<X, X>()
    {
      @Override
      protected X in(
        X value)
      {
        return value;
      }

      @Override
      protected X out(
        X value)
      {
        return value;
      }
    }, widget, beacon);
  }
}
