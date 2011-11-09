package archorg.arch.gwa.client.join;

import it.celi.research.balrog.beacon.Beacon;

import com.google.gwt.user.client.ui.HasValue;

public class EquiJoin<T> extends Join<T, T>
{
  public static <T> void join(final HasValue<T> widget,
      final Beacon<T> beacon)
  {
    new EquiJoin<T>(widget, beacon);
  }

  protected EquiJoin(final HasValue<T> widget,
      final Beacon<T> beacon)
  {
    super(new Converter<T, T>()
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
