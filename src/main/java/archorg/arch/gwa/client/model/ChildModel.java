package archorg.arch.gwa.client.model;

import it.celi.research.balrog.beacon.Beacon;
import it.celi.research.balrog.beacon.BeaconImpl;
import it.celi.research.balrog.beacon.BeaconReader;
import it.celi.research.balrog.beacon.BeaconWriter;
import it.celi.research.balrog.claudenda.Claudenda;

import java.util.ArrayList;
import java.util.Map;

import archorg.arch.gwa.client.Client;
import archorg.arch.gwa.client.join.Actuator;
import archorg.arch.gwa.client.serialization.URIStateManager;
import archorg.arch.gwa.client.serialization.HasSerializableState;
import archorg.arch.gwa.client.serialization.SerializableState;
import archorg.arch.gwa.client.serialization.StateDeserialization;
import archorg.arch.gwa.client.serialization.StateSerialization;
import archorg.arch.gwa.client.serialization.StateSerializationFormatException;
import archorg.arch.gwa.shared.Input;
import archorg.arch.gwa.shared.Output;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ChildModel implements HasSerializableState
{
  private final BeaconWriter<String> message_impl;

  public ChildModel(Claudenda clau,
      BeaconWriter<String> message_impl)
  {
    this.message_impl = message_impl;
    sc = new SerializableState(clau)
    {
      public void load(StateDeserialization load,
          String id,
          boolean dryrun) throws StateSerializationFormatException
      {
        Map<String, String> map = load.get(id,
          dryrun);
        if (map.containsKey("input"))
        {
          String s = map.get("input");
          try
          {
            input_impl.setIfNotEqual(Integer.parseInt(s));
          }
          catch (NumberFormatException e)
          {
            throw new StateSerializationFormatException("Integer.parseInt(" + s
                + ")");
          }
        } else
        {
          input_impl.resetToDefault(true);
        }
      }

      @Override
      public void dump(StateSerialization s,
          Map<String, String> map)
      {
        map.put("input",
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
      public void afterLoading()
      {
        // we probably want to have a way to tell whether we really want to
        // serve or if there is something else we should do.
        // perhaps encode the action that triggered the state as a persistent
        // part of the state and read here whether it was this action that
        // was executed last.
        serve();
      }
    };
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

  private SerializableState sc;

  /**
   * Removes all listeners so that they can be garbage-collected.
   */
  public void clearSubscribers()
  {
    input_impl.clearSubscribers();
    results_impl.clearSubscribers();
  }

  @Override
  public SerializableState getSerializableState()
  {
    return sc;
  }
}
