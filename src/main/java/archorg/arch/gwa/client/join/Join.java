package archorg.arch.gwa.client.join;

import it.celi.research.balrog.beacon.SimpleBeacon;
import it.celi.research.balrog.beacon.SimpleBeaconChange;
import it.celi.research.balrog.beacon.SimpleBeaconObserver;
import it.celi.research.balrog.event.Observable;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;

public class Join<X, Y>
{
  private boolean ignore = false;

  public static <X, Y> void join(
    final Converter<X, Y> converter,
    final HasValue<X> widget,
    final SimpleBeacon<Y> beacon)
  {
    new Join<X, Y>(converter, widget, beacon);
  }

  protected Join(
    final Converter<X, Y> converter,
    final HasValue<X> widget,
    final SimpleBeacon<Y> beacon)
  {
    widget.addValueChangeHandler(new ValueChangeHandler<X>()
    {
      @Override
      public void onValueChange(
        ValueChangeEvent<X> event)
      {
        if (ignore)
          return;
        ignore = true;
        beacon.setNevertheless(converter.in(event.getValue()));
        ignore = false;
      }
    });
    beacon.subscribe(new SimpleBeaconObserver<Y>()
    {
      @Override
      public void notice(
        Observable<? extends SimpleBeaconChange<? extends Y>> observable,
        SimpleBeaconChange<? extends Y> message)
      {
        widget.setValue(converter.out(message.getNew()));
      }
    });
    widget.setValue(converter.out(beacon.get()));
  }
}
