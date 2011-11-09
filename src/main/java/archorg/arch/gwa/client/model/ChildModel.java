package archorg.arch.gwa.client.model;

import it.celi.research.balrog.beacon.Beacon;
import it.celi.research.balrog.beacon.BeaconImpl;
import it.celi.research.balrog.beacon.BeaconReader;
import it.celi.research.balrog.beacon.BeaconWriter;

import java.util.ArrayList;

import archorg.arch.gwa.client.Client;
import archorg.arch.gwa.client.URIStateManager;
import archorg.arch.gwa.client.join.Actuator;
import archorg.arch.gwa.client.serialization.HasSerializableState;
import archorg.arch.gwa.client.serialization.ReadableStateModel;
import archorg.arch.gwa.client.serialization.SerializableState;
import archorg.arch.gwa.client.serialization.StateSerializationFormatException;
import archorg.arch.gwa.client.serialization.WritableStateModel;
import archorg.arch.gwa.shared.Input;
import archorg.arch.gwa.shared.Output;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ChildModel implements HasSerializableState
{
  private final BeaconWriter<String> message_impl;

  public ChildModel(BeaconWriter<String> message_impl)
  {
    this.message_impl = message_impl;
  }

  private BeaconImpl<Integer> input_impl = new BeaconImpl<Integer>(1);

  public Beacon<Integer> input = input_impl;

  private BeaconImpl<ArrayList<Integer>> results_impl =
    new BeaconImpl<ArrayList<Integer>>();

  public BeaconReader<? extends Iterable<? extends Integer>> results =
    results_impl;

  private void serve()
  {
    // here maybe we want to save the action into a persistent part of the
    // state, or as an "extra"
    URIStateManager.store();
    Input inp = new Input();
    inp.input = input_impl.get();
    Client.stub.serve(inp,
      new AsyncCallback<Output>()
      {
        @Override
        public void onFailure(Throwable caught)
        {
          message_impl.setIfNotEqual(caught.getClass().getName());
        }

        @Override
        public void onSuccess(Output result)
        {
          if (result.errorOccurred)
            message_impl.setIfNotEqual(result.errorMessage);
          else
            results_impl.setNevertheless(result.output);
        }
      });
  }

  public Actuator ask = new Actuator()
  {
    @Override
    public void actuate()
    {
      serve();
    }
  };

  @Override
  public SerializableState getSerializableState()
  {
    return state;
  }

  private SerializableState state = new MySerializableState();

  private class MySerializableState implements SerializableState
  {
    @Override
    public void dump(WritableStateModel s,
        String id)
    {
      if (!input.isAtDefault())
        s.fold(id,
          "input",
          input.get().toString());
    }

    @Override
    public void resetToDefault()
    {
      input_impl.resetToDefault(true);
    }

    @Override
    public boolean isAtDefault()
    {
      return input_impl.isAtDefault();
    }

    @Override
    public void load(ReadableStateModel s,
        String id) throws StateSerializationFormatException
    {
      if (!s.specifies(id,
        "input"))
      {
        input_impl.resetToDefault(true);
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
    public void validate(ReadableStateModel s,
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
