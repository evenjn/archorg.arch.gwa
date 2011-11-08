package archorg.arch.gwa.client;

import it.celi.research.balrog.beacon.BeaconObserver;
import it.celi.research.balrog.beacon.Change;
import it.celi.research.balrog.claudenda.Claudenda;
import it.celi.research.balrog.claudenda.Claudendum;
import it.celi.research.balrog.event.Observable;

import java.util.ArrayList;

public abstract class ClaudendumBeaconObserver<T> implements BeaconObserver<T>
{
  public ArrayList<Observable<? extends Change<? extends T>>> observed;

  private class InnerClaudendum implements Claudendum
  {
    @Override
    public void close() throws Exception
    {
      for (Observable<? extends Change<? extends T>> o : observed)
        o.unsubscribe(ClaudendumBeaconObserver.this);
    }
  }

  public ClaudendumBeaconObserver(Claudenda clau)
  {
    clau.add(new InnerClaudendum());
  }

  @Override
  public
      void
      acknowledgeBeginningSubscription(Observable<? extends Change<? extends T>> observable)
  {
    if (observed == null)
      observed = new ArrayList<Observable<? extends Change<? extends T>>>();
    observed.add(observable);
  }

  @Override
  public
      void
      acknowledgeEndSubscription(Observable<? extends Change<? extends T>> observable)
  {
    observed.remove(observable);
  }
}
