package archorg.arch.gwa.client.serialization;

import it.celi.research.balrog.beacon.SimpleBeaconImpl;
import it.celi.research.balrog.beacon.SimpleBeaconReadable;
import it.celi.research.balrog.event.EventChannel;
import it.celi.research.balrog.event.Observable;
import it.celi.research.balrog.event.Observer;
import archorg.arch.gwa.client.serialization.model.HasSerializationEngine;
import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.Transition;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;

public class StateManager
{
  private HasSerializationEngine root;

  private boolean loading = false;

  private EventChannel<String> message_beacon = new EventChannel<String>();

  private final StateModelFactory factory;

  public Observable<String> getMessage()
  {
    return message_beacon;
  }

  public StateManager(
    StateModelFactory factory)
  {
    this.factory = factory;
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
    HasSerializationEngine root)
  {
    this.root = root;
    // environment_change.notify(null);
  }

  private void load(
    String historyToken)
  {
    String encoded = URL.decodeQueryString(historyToken);
    if (encoded.isEmpty())
    {
      // if the token is empty, behave as if the token was a reset token.
      encoded = "!";
    }
    if (!encoded.startsWith("!"))
      // this is a regular link
      return;
    loading = true;
    Trigger.setEnabled(false);
    try
    {
      factory.load(root,
        encoded);
      environment_change.notify(null);
    }
    catch (SerializationException e)
    {
      String s = e.getMessage();
      if (s == null)
        s = "-";
      message_beacon.notify("The URI you attempted to load is not valid.");
    }
    finally
    {
      loading = false;
      Trigger.setEnabled(true);
    }
  }

  public SimpleBeaconReadable<String> createActionBeacon(
    final Transition sa)
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

  /**
   * Stores the current state of the application in the URL.
   */
  public String serialize(
    Transition transition)
  {
    if (root == null)
      throw new IllegalStateException("Root has not been set yet.");
    return factory.dump(root,
      transition);
  }

  public void store(
    String historyToken)
  {
    if (loading)
      return;
    History.newItem(historyToken,
      true);
  }

  public EnvironmentEventBus getEnvironmentEventBus()
  {
    return environment_change;
  }

  private MyEnvironmentEventBus environment_change =
    new MyEnvironmentEventBus();

  private static class MyEnvironmentEventBus
    extends
    EventChannel<Void>
    implements
    EnvironmentEventBus
  {
    @Override
    public void notice(
      Observable<? extends Object> observable,
      Object message)
    {
      sendSignal();
    }

    @Override
    public void sendSignal()
    {
      notify(null);
    }
  }
}
