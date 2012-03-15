package archorg.arch.gwa.client.serialization;

import it.celi.research.balrog.beacon.SimpleBeaconReadable;

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

  private final SimpleBeaconReadable<String> next_state;

  public StatefulActionImpl()
  {
    next_state = statemanager.createActionBeacon(this);
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
