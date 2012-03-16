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
import archorg.arch.gwa.client.serialization.model.Transition;
import archorg.arch.gwa.client.serialization.model.parts.BeaconStateEngineAggregation;
import archorg.arch.gwa.shared.Input;
import archorg.arch.gwa.shared.Output;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ChildModel
  implements
  HasObjectStateEngine
{
  public ChildModel(
    final int default_value,
    final EventChannel<Void> envchan,
    final Observer<? super Object> envco,
    final SimpleBeacon<String> message_impl,
    final Trigger<Object> reset_message)
  {
    input = new SimpleBeaconImpl<Integer>(default_value);
    engine =
      new BeaconStateEngineAggregation(new NonNullIntegerBeacon("input", input,
        default_value)
      {
        @Override
        protected Integer transform(
          Integer value,
          Transition a)
        {
          if (a == next_action_impl)
            return value + 1;
          return value;
        }
      })
      {
        boolean linked = false;

        public void link()
        {
          super.link();
          if (linked)
            return;
          linked = true;
          input.subscribe(reset_message);
          input.subscribe(envco);
        }

        @Override
        public void postLoad()
        {
          super.postLoad();
          Input inp = new Input();
          message_impl.setIfNotEqual(null);
          inp.input = input.get();
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
      };
  }

  private final Transition action_impl = new Transition();

  public StatefulAction getActionCurrent()
  {
    return new StatefulActionImpl(action_impl);
  }

  private final Transition next_action_impl = new Transition();

  public StatefulAction getActionNext()
  {
    return new StatefulActionImpl(next_action_impl);
  }

  public final SimpleBeacon<Integer> input;

  private SimpleBeaconImpl<ArrayList<Integer>> results_impl =
    new SimpleBeaconImpl<ArrayList<Integer>>();

  public SimpleBeaconReadable<? extends Iterable<? extends Integer>> results =
    results_impl;

  private final ObjectStateEngine engine;

  @Override
  public ObjectStateEngine getObjectStateEngine()
  {
    return engine;
  }
}
