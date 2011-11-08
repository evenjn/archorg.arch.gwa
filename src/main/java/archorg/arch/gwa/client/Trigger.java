package archorg.arch.gwa.client;

import it.celi.research.balrog.beacon.Change;
import it.celi.research.balrog.beacon.StatelessBeaconObserver;
import it.celi.research.balrog.event.EventChannel;
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

  private static EventChannel<Void> triggerProcessingComplete =
    new EventChannel<Void>();

  public static Observable<Void> getEndOfProcess()
  {
    return triggerProcessingComplete;
  }

  private static int stack = 0;

  private static void open()
  {
    stack++;
  }

  private static void close()
  {
    stack--;
    if (stack == 0)
      triggerProcessingComplete.notify(null);
  }

  public static boolean isProcessing()
  {
    return stack != 0;
  }

  @Override
  public void notice(Observable<? extends Change<? extends T>> observable,
      Change<? extends T> message)
  {
    if (!enabled)
      return;
    open();
    onTrigger(observable,
      message);
    close();
  }

  public abstract void
      onTrigger(Observable<? extends Change<? extends T>> observable,
          Change<? extends T> message);
}