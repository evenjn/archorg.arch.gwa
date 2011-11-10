package archorg.arch.gwa.client.serialization;

import it.celi.research.balrog.event.EventChannel;
import it.celi.research.balrog.event.Observable;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;

public abstract class StateManager
{
  private HasSerializableState root;

  private boolean loading = false;

  private EventChannel<String> message_beacon = new EventChannel();

  public Observable<String> getMessage()
  {
    return message_beacon;
  }

  public StateManager(HasSerializableState root)
  {
    root = root;
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

  private void load(String historyToken)
  {
    loading = true;
    Trigger.setEnabled(false);
    String decode = URL.decodeQueryString(historyToken);
    try
    {
      StateModel load = create(decode);
      load.load(root);
    }
    catch (StateSerializationFormatException e)
    {
      String s = e.getMessage();
      if (s == null)
        s = "-";
      message_beacon.notify("The URI you attempted to load is not valid. ");
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
  public void store()
  {
    if (loading)
      return;
    StateModel si = create();
    si.dump(root);
    History.newItem(si.toString(),
      false);
  }

  protected abstract StateModel create();

  protected abstract StateModel create(String decode)
      throws StateSerializationFormatException;
}
