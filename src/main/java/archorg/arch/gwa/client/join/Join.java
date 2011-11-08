package archorg.arch.gwa.client.join;

import it.celi.research.balrog.beacon.Beacon;
import it.celi.research.balrog.beacon.BeaconObserver;
import it.celi.research.balrog.beacon.Change;
import it.celi.research.balrog.claudenda.Claudenda;
import it.celi.research.balrog.event.Observable;
import archorg.arch.gwa.client.ClaudendumBeaconObserver;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;

public class Join<X, Y>
{
  private boolean ignore = false;

  private BeaconObserver<Y> observer;

  public static <X, Y> void join(Claudenda clau,
      final Converter<X, Y> converter,
      final HasValue<X> widget,
      final Beacon<Y> beacon)
  {
    new Join<X, Y>(clau, converter, widget, beacon);
  }

  protected Join(Claudenda clau,
      final Converter<X, Y> converter,
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
    observer = new ClaudendumBeaconObserver<Y>(clau)
    {
      @Override
      public void notice(Observable<? extends Change<? extends Y>> o,
          Change<? extends Y> change)
      {
        widget.setValue(converter.out(change.getNew()));
      }
    };
    beacon.subscribe(observer);
    widget.setValue(converter.out(beacon.get()));
  }
}
