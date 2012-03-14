package archorg.arch.gwa.client.model;

import it.celi.research.balrog.beacon.SimpleBeacon;
import it.celi.research.balrog.beacon.SimpleBeaconChange;
import it.celi.research.balrog.beacon.SimpleBeaconReadable;
import it.celi.research.balrog.event.Observable;

import java.util.ArrayList;

import archorg.arch.gwa.client.Client;
import archorg.arch.gwa.client.serialization.HasSerializableState;
import archorg.arch.gwa.client.serialization.ReadableStateModel;
import archorg.arch.gwa.client.serialization.SerializableState;
import archorg.arch.gwa.client.serialization.StateSerializationFormatException;
import archorg.arch.gwa.client.serialization.Trigger;
import archorg.arch.gwa.client.serialization.TriggerBeacon;
import archorg.arch.gwa.client.serialization.WritableStateModel;
import archorg.arch.gwa.shared.Input;
import archorg.arch.gwa.shared.Output;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ChildModel
  implements
  HasSerializableState
{
  private final SimpleBeacon<String> message_impl;

  public ChildModel(
    SimpleBeacon<String> message_impl,
    Trigger<Object> reset_message)
  {
    this.message_impl = message_impl;
    action_impl.setSaveOnEvent(true);
    action_impl.subscribe(new Trigger<Void>()
    {
      @Override
      public void onTrigger(
        Observable<? extends SimpleBeaconChange<? extends Void>> observable,
        SimpleBeaconChange<? extends Void> message)
      {
        serve();
      }
    });
    input_impl.subscribe(reset_message);
  }

  private TriggerBeacon<Void> action_impl = new TriggerBeacon<Void>(null);

  public SimpleBeacon<Void> getActionW()
  {
    return action_impl;
  }

  private TriggerBeacon<Integer> input_impl = new TriggerBeacon<Integer>(1);

  public SimpleBeacon<Integer> input = input_impl;

  private TriggerBeacon<ArrayList<Integer>> results_impl =
    new TriggerBeacon<ArrayList<Integer>>();

  public SimpleBeaconReadable<? extends Iterable<? extends Integer>> results =
    results_impl;

  private void serve()
  {
    Input inp = new Input();
    message_impl.setIfNotEqual(null);
    inp.input = input_impl.get();
    Client.stub.serve(inp,
      new AsyncCallback<Output>()
      {
        @Override
        public void onFailure(
          Throwable caught)
        {
          message_impl.setIfNotEqual(caught.getClass().getName());
        }

        @Override
        public void onSuccess(
          Output result)
        {
          if (result.errorOccurred)
            message_impl.setIfNotEqual(result.errorMessage);
          else
            results_impl.setNevertheless(result.output);
        }
      });
  }

  @Override
  public SerializableState getSerializableState()
  {
    return state;
  }

  private SerializableState state = new MySerializableState();

  private class MySerializableState
    implements
    SerializableState
  {
    @Override
    public void dump(
      WritableStateModel s,
      String id)
    {
      if (!input.valueEquals(1))
        s.fold(id,
          "input",
          input.get().toString());
    }

    // @Override
    // public void resetToDefault()
    // {
    // input_impl.setIfNotEqual(1);
    // }
    //
    @Override
    public boolean isAtDefault()
    {
      return input_impl.valueEquals(1);
    }

    @Override
    public void load(
      ReadableStateModel s,
      String id) throws StateSerializationFormatException
    {
      if (!s.specifies(id,
        "input"))
      {
        input_impl.setIfNotEqual(1);
        return;
      }
      String du = s.unfold(id,
        "input");
      try
      {
        int parseInt = Integer.parseInt(du);
        input_impl.setIfNotEqual(parseInt);
      }
      catch (NumberFormatException e)
      {
        throw new StateSerializationFormatException("Integer.parseInt(" + du
            + ")");
      }
    }

    @Override
    public void validate(
      ReadableStateModel s,
      String id) throws StateSerializationFormatException
    {
      if (!s.specifies(id,
        "input"))
        return;
      String intr = s.unfold(id,
        "input");
      try
      {
        Integer.parseInt(intr);
      }
      catch (NumberFormatException e)
      {
        throw new StateSerializationFormatException("Integer.parseInt(" + intr
            + ")");
      }
    }

    @Override
    public void postLoad()
    {
      serve();
    }
  }
}
