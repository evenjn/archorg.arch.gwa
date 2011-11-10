package archorg.arch.gwa.client.serialization;

import it.celi.research.balrog.beacon.Change;
import it.celi.research.balrog.beacon.StatelessBeaconObserver;
import it.celi.research.balrog.event.Observable;

/**
 * 
 * we don't want this triggered ability to trigger when we are loading a
 * serialized state. or do we? perhaps we should let the trigger go, anyways the
 * serialized state will have something to say about this. the problem is, the
 * effects of this trigger may affect elements that have been already loaded,
 * and if the effect is a function of elements, this will cause terrible
 * terrible damage.
 * 
 * that is why we disable triggers when twe are loading. perhaps we want to
 * encapsulate this special behaviour inside a class that is not the usual
 * beaconobserver but a Trigger
 * 
 * TODO there is a bug in that if many triggers trigger on the same event the
 * state-save will resolve as soon as the first of the triggers is complete
 * 
 * @author evenjn
 * 
 * @param <T>
 */
public abstract class Trigger<T> extends StatelessBeaconObserver<T>
{
  public static void setEnabled(boolean value)
  {
    enabled = value;
  }

  private static boolean enabled = true;

  @Override
  public void notice(Observable<? extends Change<? extends T>> observable,
      Change<? extends T> message)
  {
    if (!enabled)
      return;
    onTrigger(observable,
      message);
  }

  public abstract void
      onTrigger(Observable<? extends Change<? extends T>> observable,
          Change<? extends T> message);
}
