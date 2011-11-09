package archorg.arch.gwa.client;

import it.celi.research.balrog.beacon.BeaconWriter;
import it.celi.research.balrog.event.Observable;
import it.celi.research.balrog.event.StatelessObserver;
import archorg.arch.gwa.client.serialization.HasSerializableState;
import archorg.arch.gwa.client.serialization.StateSerializationFormatException;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;

public class URIStateManager
{
  private static HasSerializableState root;

  private static boolean loading = false;

  private static BeaconWriter<? super String> messagepipe;

  public static void init(HasSerializableState root,
      BeaconWriter<? super String> messagepipe)
  {
    URIStateManager.root = root;
    URIStateManager.messagepipe = messagepipe;
    History.addValueChangeHandler(new ValueChangeHandler<String>()
    {
      public void onValueChange(ValueChangeEvent<String> event)
      {
        String historyToken = event.getValue();
        load(historyToken);
      }
    });
    String token = History.getToken();
    if (token.length() != 0)
      load(token);
  }

  private static void load(String historyToken)
  {
    loading = true;
    Trigger.setEnabled(false);
    String decode = URL.decodeQueryString(historyToken);
    try
    {
      URIFragmentStateModel load = new URIFragmentStateModel(decode);
      load.load(root);
    }
    catch (StateSerializationFormatException e)
    {
      String s = e.getMessage();
      if (s == null)
        s = "-";
      messagepipe
        .setNevertheless("The URI you attempted to load is not valid. " + s);
    }
    finally
    {
      loading = false;
      Trigger.setEnabled(true);
    }
  }

  /**
   * Stores the current state of the application in the URL.
   */
  public static void store()
  {
    if (loading)
      return;
    URIFragmentStateModel si = new URIFragmentStateModel();
    si.dump(root);
    History.newItem(si.toString(),
      false);
  }

  /**
   * tells the hardlink manager to store the state whenever this observable
   * broadcasts an event
   * 
   * @param o
   */
  public static void saveOnEvent(Observable<?> o)
  {
    o.subscribe(new StateStoreObserver());
  }

  /**
   * Whenever this observer receives a notification, it tries to store the
   * current state of the model. If the state loading process is ongoing, the
   * attempt is aborted. If some triggers are being processed (i.e. the stack is
   * not empty), it waits until all the triggers resolve (it waits until it is
   * empty). Otherwise, it resolves immediately.
   * 
   * It does not trigger if a copy of this ability is already on the stack.
   * 
   * 
   * @author Marco Trevisan
   * 
   */
  private static class StateStoreObserver extends StatelessObserver<Object>
  {
    static boolean delayed = false;

    @Override
    public void notice(Observable<? extends Object> observable,
        Object message)
    {
      if (URIStateManager.loading)
        return;
      if (Trigger.isProcessing())
      {
        if (delayed)
          return;
        delayed = true;
        // it may happen that the end of the Trigger processing never occurs.
        // in that case, this observer will hang on until the next time it
        // occurs.
        // in the meantime, the observable that originate the change that
        // triggered the state store action may have disappeared.
        // but at this point of the development it seems to be a very unlikely
        // situation (it is unlikely that further development will result in a
        // system where this situation may occur).
        // if many triggers are connected to the same event, this delayed
        // observer will receive a notification after the first of those
        // triggers
        // completed
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
}
