package archorg.arch.gwa.client.serialization;

import it.celi.research.balrog.beacon.SimpleBeaconImpl;
import it.celi.research.balrog.beacon.SimpleBeaconReadable;
import it.celi.research.balrog.event.Observable;
import it.celi.research.balrog.event.Observer;

public class StatefulActionImpl
  implements
  StatefulAction
{
  private static StateManager statemanager;

  public static void setStateManager(
    StateManager sm)
  {
    statemanager = sm;
  }

  private final SimpleBeaconImpl<String> next_state;

  public StatefulActionImpl()
  {
    next_state = new SimpleBeaconImpl<String>("initializing");
    Observer<Void> observer = new Observer<Void>()
    {
      @Override
      public void notice(
        Observable<? extends Void> observable,
        Void message)
      {
        next_state.setNevertheless(statemanager
          .serialize(StatefulActionImpl.this));
      }
    };
    statemanager.getEnvironmentChangeChannel().subscribe(observer);
  }

  @Override
  public void execute()
  {
    statemanager.store(next_state.get());
  }

  @Override
  public SimpleBeaconReadable<? extends String> getResultingState()
  {
    return next_state;
  }
}
