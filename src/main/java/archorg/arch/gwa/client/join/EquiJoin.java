package archorg.arch.gwa.client.join;

import it.celi.research.balrog.beacon.Beacon;
import it.celi.research.balrog.claudenda.Claudenda;

import com.google.gwt.user.client.ui.HasValue;

public class EquiJoin<T> extends Join<T, T>
{
  public static <T> void join(Claudenda clau,
      final HasValue<T> widget,
      final Beacon<T> beacon)
  {
    new EquiJoin<T>(clau, widget, beacon);
  }

  protected EquiJoin(Claudenda clau,
      final HasValue<T> widget,
      final Beacon<T> beacon)
  {
    super(clau, new Converter<T, T>()
    {
      @Override
      protected T in(T value)
      {
        return value;
      }

      @Override
      protected T out(T value)
      {
        return value;
      }
    }, widget, beacon);
  }
}
