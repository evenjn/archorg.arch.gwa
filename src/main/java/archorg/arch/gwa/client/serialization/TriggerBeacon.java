package archorg.arch.gwa.client.serialization;

import it.celi.research.balrog.beacon.SimpleBeaconChange;
import it.celi.research.balrog.beacon.SimpleBeaconImpl;

/**
 * A Beacon with a static Observable that notifies when all TriggerBeacons have
 * finished propagating their notifications.
 * 
 * @author evenjn
 * 
 * @param <T>
 */
public class TriggerBeacon<T>
  extends
  SimpleBeaconImpl<T>
{
  private static StateManager statemanager;

  public static void setStateManager(
    StateManager sm)
  {
    statemanager = sm;
  }

  private static int stack = 0;

  protected boolean saveOnEvent = false;

  public void setSaveOnEvent(
    boolean value)
  {
    saveOnEvent = value;
  }

  public TriggerBeacon()
  {};

  public TriggerBeacon(
    T defaultvalue)
  {
    super(defaultvalue);
  }

  public void notify(
    SimpleBeaconChange<? extends T> change)
  {
    stack++;
    super.notify(change);
    stack--;
    if (stack == 0 && saveOnEvent)
      statemanager.store();
  }
}
