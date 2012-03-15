package archorg.arch.gwa.client.serialization;

import it.celi.research.balrog.beacon.SimpleBeaconImpl;
import it.celi.research.balrog.beacon.SimpleBeaconReadable;
import it.celi.research.balrog.event.EventChannel;
import it.celi.research.balrog.event.Observable;
import it.celi.research.balrog.event.Observer;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;

public abstract class StateManager
{
  private HasBoth root;

  private boolean loading = false;

  private EventChannel<String> message_beacon = new EventChannel<String>();

  public Observable<String> getMessage()
  {
    return message_beacon;
  }

  public StateManager()
  {
    History.addValueChangeHandler(new ValueChangeHandler<String>()
    {
      public void onValueChange(
        ValueChangeEvent<String> event)
      {
        String historyToken = event.getValue();
        load(historyToken);
      }
    });
  }

  public void setRoot(
    HasBoth root)
  {
    this.root = root;
    String token = History.getToken();
    if (token.length() != 0)
      load(token);
    environment_change.notify(null);
  }

  private void load(
    String historyToken)
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

  public SimpleBeaconReadable<String> createActionBeacon(
    final StatefulAction sa)
  {
    final SimpleBeaconImpl<String> next_state =
      new SimpleBeaconImpl<String>("initializing");
    Observer<Void> observer = new Observer<Void>()
    {
      @Override
      public void notice(
        Observable<? extends Void> observable,
        Void message)
      {
        next_state.setNevertheless(serialize(sa));
      }
    };
    environment_change.subscribe(observer);
    return next_state;
  }

  private EventChannel<Void> environment_change = new EventChannel<Void>();

  public Observer<Object> getEnvironmentChangeObserver()
  {
    return environmentChangeObserver;
  }

  private Observer<Object> environmentChangeObserver = new Observer<Object>()
  {
    @Override
    public void notice(
      Observable<? extends Object> observable,
      Object message)
    {
      environment_change.notify(null);
    }
  };

  /**
   * Stores the current state of the application in the URL.
   */
  public String serialize(
    StatefulAction a)
  {
    StateModel si = create();
    si.dump(root,
      a);
    return si.toString();
  }

  public void store(
    String historyToken)
  {
    if (loading)
      return;
    History.newItem(historyToken,
      true);
  }

  protected abstract StateModel create();

  protected abstract StateModel create(
    String decode) throws StateSerializationFormatException;
}
