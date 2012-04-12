package archorg.arch.gwa.client.serialization;

import it.celi.research.balrog.beacon.SimpleBeaconImpl;
import it.celi.research.balrog.beacon.SimpleBeaconReadable;
import it.celi.research.balrog.claudenda.Claudenda;
import it.celi.research.balrog.claudenda.Claudendum;
import it.celi.research.balrog.event.Observable;
import it.celi.research.balrog.event.Observer;
import archorg.arch.gwa.client.serialization.model.Transition;

public class StateTransitionActionImpl
  implements
  StateTransitionAction
{
  private static StateManager statemanager;

  public static void setStateManager(
    StateManager sm)
  {
    statemanager = sm;
  }

  private final SimpleBeaconImpl<String> next_state;

  private final Transition transition;

  public StateTransitionActionImpl(
    Claudenda clau,
    final Transition transition)
  {
    this.transition = transition;
    next_state = new SimpleBeaconImpl<String>("initializing");
    statemanager.getEnvironmentEventBus().subscribe(observer);
    clau.add(new Claudendum()
    {
      @Override
      public void close()
      {
        statemanager.getEnvironmentEventBus().unsubscribe(observer);
      }
    });
  }

  @Override
  public void execute()
  {
    statemanager.store(next_state.get());
  }

  public void calculate()
  {
    next_state.setNevertheless(statemanager.serialize(transition));
  }

  private Observer<Void> observer = new Observer<Void>()
  {
    @Override
    public void notice(
      Observable<? extends Void> observable,
      Void message)
    {
      next_state.setNevertheless(statemanager.serialize(transition));
    }
  };

  @Override
  public SimpleBeaconReadable<? extends String> getResultingState()
  {
    return next_state;
  }
}
