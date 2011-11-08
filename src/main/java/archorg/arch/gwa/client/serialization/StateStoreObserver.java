package archorg.arch.gwa.client.serialization;

import it.celi.research.balrog.event.Observable;
import it.celi.research.balrog.event.StatelessObserver;
import archorg.arch.gwa.client.Trigger;

/**
 * Whenever this observer receives a notification, it tries to store the current
 * state of the model. If the state loading process is ongoing, the attempt is
 * aborted. If some triggers are being processed (i.e. the stack is not empty),
 * it waits until all the triggers resolve (it waits until it is empty).
 * Otherwise, it resolves immediately.
 * 
 * It does not trigger if a copy of this ability is already on the stack.
 * 
 * 
 * @author Marco Trevisan
 * 
 */
public class StateStoreObserver extends StatelessObserver<Object>
{
  static boolean delayed = false;

  @Override
  public void notice(Observable<? extends Object> observable,
      Object message)
  {
    if (URIStateManager.isLoading())
      return;
    if (Trigger.isProcessing())
    {
      if (delayed)
        return;
      delayed = true;
      // it may happen that the end of the Trigger processing never occurs.
      // in that case, this observer will hang on until the next time it occurs.
      // in the meantime, the observable that originate the change that
      // triggered the state store action may have disappeared.
      // but at this point of the development it seems to be a very unlikely
      // situation (it is unlikely that further development will result in a
      // system where this situation may occur).
      Trigger.getEndOfProcess().subscribe(new StatelessObserver<Void>()
      {
        @Override
        public void notice(Observable<? extends Void> observable,
            Void message)
        {
          URIStateManager.store();
          Trigger.getEndOfProcess().unsubscribe(this);
          delayed = false;
        }
      });
    } else
      URIStateManager.store();
  }
}
