package archorg.arch.gwa.client.join;

import it.celi.research.balrog.beacon.Beacon;
import it.celi.research.balrog.beacon.BeaconObserver;
import it.celi.research.balrog.beacon.Change;
import it.celi.research.balrog.event.Observable;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;

public class Join<X, Y>
{
  private boolean ignore = false;

  public static <X, Y> void join(final Converter<X, Y> converter,
      final HasValue<X> widget,
      final Beacon<Y> beacon)
  {
    new Join<X, Y>(converter, widget, beacon);
  }

  protected Join(final Converter<X, Y> converter,
      final HasValue<X> widget,
      final Beacon<Y> beacon)
  {
    widget.addValueChangeHandler(new ValueChangeHandler<X>()
    {
      @Override
      public void onValueChange(ValueChangeEvent<X> event)
      {
        if (ignore)
          return;
        ignore = true;
        beacon.setNevertheless(converter.in(event.getValue()));
        ignore = false;
      }
    });
    beacon.subscribe(new BeaconObserver<Y>()
    {
      @Override
      public void notice(Observable<? extends Change<? extends Y>> o,
          Change<? extends Y> change)
      {
        widget.setValue(converter.out(change.getNew()));
      }
    });
    widget.setValue(converter.out(beacon.get()));
  }
}
