package archorg.arch.gwa.client.model;

import it.celi.research.balrog.beacon.SimpleBeacon;
import it.celi.research.balrog.beacon.SimpleBeaconImpl;
import it.celi.research.balrog.beacon.SimpleBeaconReadable;
import it.celi.research.balrog.event.Observer;

import java.util.ArrayList;

import archorg.arch.gwa.client.Client;
import archorg.arch.gwa.client.serialization.HasBoth;
import archorg.arch.gwa.client.serialization.ReadableStateModel;
import archorg.arch.gwa.client.serialization.SerializableState;
import archorg.arch.gwa.client.serialization.StateLoader;
import archorg.arch.gwa.client.serialization.StateSerializationFormatException;
import archorg.arch.gwa.client.serialization.StatefulAction;
import archorg.arch.gwa.client.serialization.StatefulActionImpl;
import archorg.arch.gwa.client.serialization.Trigger;
import archorg.arch.gwa.client.serialization.WritableStateModel;
import archorg.arch.gwa.shared.Input;
import archorg.arch.gwa.shared.Output;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ChildModel
  implements
  HasBoth
{
  private final SimpleBeacon<String> message_impl;

  public ChildModel(
    Observer<? super Object> envco,
    SimpleBeacon<String> message_impl,
    Trigger<Object> reset_message)
  {
    this.message_impl = message_impl;
    if (reset_message != null)
      input_impl.subscribe(reset_message);
    if (envco != null)
      input_impl.subscribe(envco);
  }

  private final StatefulActionImpl action_impl = new StatefulActionImpl();

  public StatefulAction getActionCurrent()
  {
    return action_impl;
  }

  private final StatefulActionImpl next_action_impl = new StatefulActionImpl();

  public StatefulAction getActionNext()
  {
    return next_action_impl;
  }

  private SimpleBeaconImpl<Integer> input_impl = new SimpleBeaconImpl<Integer>(
    1);

  public SimpleBeacon<Integer> input = input_impl;

  private SimpleBeaconImpl<ArrayList<Integer>> results_impl =
    new SimpleBeaconImpl<ArrayList<Integer>>();

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

  public SerializableState getSerializableState()
  {
    return new SerializableState()
    {
      @Override
      public void dump(
        WritableStateModel s,
        String id,
        StatefulAction a)
      {
        int curr = input.get();
        if (a == next_action_impl)
          curr = curr + 1;
        if (curr != 1)
          s.fold(id,
            "input",
            "" + curr);
      }

      @Override
      public boolean isAtDefault(
        StatefulAction a)
      {
        int curr = input.get();
        if (a == next_action_impl)
          curr = curr + 1;
        return curr == 1;
      }
    };
  }

  @Override
  public StateLoader getStateLoader()
  {
    // TODO Auto-generated method stub
    return new StateLoader()
    {
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
          throw new StateSerializationFormatException("Integer.parseInt("
              + intr + ")");
        }
      }

      @Override
      public void postLoad()
      {
        serve();
      }

      @Override
      public void resetToDefault()
      {
        input_impl.setIfNotEqual(1);
      }
    };
  }
}
