package archorg.arch.gwa.client.serialization;

import it.celi.research.balrog.beacon.BeaconWriter;
import it.celi.research.balrog.event.EventChannel;
import it.celi.research.balrog.event.Observable;
import archorg.arch.gwa.client.Trigger;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;

public class URIStateManager
{
  private static HasSerializableState root;

  private static boolean loading = false;

  private static EventChannel<Void> loadingComplete = new EventChannel<Void>();

  public static Observable<Void> getLoadingCompleteOb()
  {
    return loadingComplete;
  }

  private static BeaconWriter<? super String> messagepipe;

  public static boolean isLoading()
  {
    return loading;
  }

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
      StateDeserialization load = StateSerializer.load(decode);
      root.getSerializableState().load(load,
        load.getRootID(),
        true);
      root.getSerializableState().load(load,
        load.getRootID(),
        false);
      loadingComplete.notify(null);
    }
    catch (StateSerializationFormatException e)
    {
      messagepipe
        .setNevertheless("The URI you attempted to load is not valid.");
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
    String dump = StateSerializer.dump(root);
    History.newItem(dump,
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
}
