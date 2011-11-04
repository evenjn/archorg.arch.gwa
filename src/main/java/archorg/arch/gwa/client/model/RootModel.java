package archorg.arch.gwa.client.model;

import it.celi.research.balrog.beacon.Beacon;
import it.celi.research.balrog.beacon.BeaconImpl;
import it.celi.research.balrog.beacon.BeaconReader;

import java.util.ArrayList;
import java.util.Map;

import archorg.arch.gwa.client.Service;
import archorg.arch.gwa.client.ServiceAsync;
import archorg.arch.gwa.client.join.Actuator;
import archorg.arch.gwa.client.serialization.SDeserialization;
import archorg.arch.gwa.client.serialization.SSerializable;
import archorg.arch.gwa.client.serialization.SSerialization;
import archorg.arch.gwa.client.serialization.SSerializationFormatException;
import archorg.arch.gwa.shared.Input;
import archorg.arch.gwa.shared.Output;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RootModel implements SSerializable
{
  private boolean executed = false;

  private BeaconImpl<String> message_impl = new BeaconImpl<String>();

  private BeaconImpl<Integer> input_impl = new BeaconImpl<Integer>();

  private BeaconImpl<ArrayList<Integer>> results_impl =
    new BeaconImpl<ArrayList<Integer>>();

  private final ServiceAsync stub = GWT.create(Service.class);

  private void serve()
  {
    Input inp = new Input();
    inp.input = input_impl.get();
    executed = true;
    // here we should save
    stub.serve(inp,
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

  public Beacon<Integer> input = input_impl;

  public BeaconReader<? extends Iterable<? extends Integer>> results =
    results_impl;

  public BeaconReader<? extends String> message = message_impl;

  public Actuator ask = new Actuator()
  {
    @Override
    public void actuate()
    {
      serve();
    }
  };

  public void load(SDeserialization load,
      String id,
      boolean dryrun) throws SSerializationFormatException
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
        throw new SSerializationFormatException("Integer.parseInt(" + s + ")");
      }
    } else
    {
      input_impl.resetToDefault(true);
    }
    serve();
  }

  @Override
  public void dump(SSerialization s,
      Map<String, String> map)
  {
    map.put("input",
      input.get().toString());
  }

  @Override
  public void resetToDefaults()
  {
    input_impl.resetToDefault(true);
  }

  @Override
  public boolean isAtDefaults()
  {
    return input_impl.isAtDefault();
  }
}
