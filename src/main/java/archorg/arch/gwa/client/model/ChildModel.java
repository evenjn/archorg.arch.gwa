package archorg.arch.gwa.client.model;

import it.celi.research.balrog.beacon.SimpleBeacon;
import it.celi.research.balrog.beacon.SimpleBeaconImpl;
import it.celi.research.balrog.beacon.SimpleBeaconReadable;
import it.celi.research.balrog.event.EventChannel;
import it.celi.research.balrog.event.Observer;

import java.util.ArrayList;

import archorg.arch.gwa.client.Client;
import archorg.arch.gwa.client.beacon.NonNullIntegerBeacon;
import archorg.arch.gwa.client.serialization.StatefulAction;
import archorg.arch.gwa.client.serialization.StatefulActionImpl;
import archorg.arch.gwa.client.serialization.Trigger;
import archorg.arch.gwa.client.serialization.model.HasObjectStateEngine;
import archorg.arch.gwa.client.serialization.model.ObjectStateEngine;
import archorg.arch.gwa.client.serialization.model.parts.BeaconStateEngineAggregation;
import archorg.arch.gwa.shared.Input;
import archorg.arch.gwa.shared.Output;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ChildModel
  implements
  HasObjectStateEngine
{
  private final SimpleBeacon<String> message_impl;

  // private final EventChannel<Void> envchan;
  public ChildModel(
    EventChannel<Void> envchan,
    Observer<? super Object> envco,
    SimpleBeacon<String> message_impl,
    Trigger<Object> reset_message)
  {
    // this.envchan = envchan;
    this.message_impl = message_impl;
    if (reset_message != null)
      input_impl.subscribe(reset_message);
    if (envco != null)
    {
      input_impl.subscribe(envco);
    }
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

  private final ObjectStateEngine engine = new BeaconStateEngineAggregation(
    new NonNullIntegerBeacon("input", input, 1)
    {
      @Override
      protected Integer transform(
        Integer value,
        StatefulAction a)
      {
        if (a == next_action_impl)
          return value + 1;
        return value;
      }

      @Override
      protected void postLoad()
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
    });

  @Override
  public ObjectStateEngine getObjectStateEngine()
  {
    return engine;
  }
}
