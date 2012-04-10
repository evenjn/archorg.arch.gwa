package archorg.arch.gwa.client.model;

import it.celi.research.balrog.beacon.SimpleBeacon;
import it.celi.research.balrog.beacon.SimpleBeaconImpl;
import it.celi.research.balrog.beacon.SimpleBeaconReadable;
import it.celi.research.balrog.claudenda.Claudenda;
import it.celi.research.balrog.claudenda.ClaudendaService;
import it.celi.research.balrog.claudenda.ClaudendaServiceFactory;
import it.celi.research.balrog.claudenda.Claudendum;

import java.util.ArrayList;

import archorg.arch.gwa.client.Client;
import archorg.arch.gwa.client.beacon.SafeSimpleBeaconImpl;
import archorg.arch.gwa.client.serialization.EnvironmentEventBus;
import archorg.arch.gwa.client.serialization.StateTransitionAction;
import archorg.arch.gwa.client.serialization.StateTransitionActionImpl;
import archorg.arch.gwa.client.serialization.Trigger;
import archorg.arch.gwa.client.serialization.model.HasSerializationEngine;
import archorg.arch.gwa.client.serialization.model.SerializationEngine;
import archorg.arch.gwa.client.serialization.model.SimpleTransition;
import archorg.arch.gwa.client.serialization.model.Transition;
import archorg.arch.gwa.client.serialization.model.parts.CompositeSerializationEngine;
import archorg.arch.gwa.client.serialization.model.parts.ValueBeaconCSEPart;
import archorg.arch.gwa.client.serialization.model.parts.value.IntegerSerializationEngine;
import archorg.arch.gwa.shared.Input;
import archorg.arch.gwa.shared.Output;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ChildModel
  implements
  HasSerializationEngine
{
  private ClaudendaService claudenda_service;

  public ChildModel(
    final int default_value,
    final Claudenda clau,
    final EnvironmentEventBus eeb,
    final SimpleBeacon<String> message_impl,
    final Trigger<Object> reset_message)
  {
    input =
      new SafeSimpleBeaconImpl<Integer>(new SimpleBeaconImpl<Integer>(
        default_value))
      {
        @Override
        protected Integer safeReplace(
          Integer value)
        {
          if (value == null || value < 0 || value > 20)
            return default_value;
          return value;
        }
      };
    claudenda_service =
      ClaudendaServiceFactory.create(this.getClass().getName());
    actioncurrent =
      new StateTransitionActionImpl(claudenda_service, action_impl);
    actionnext =
      new StateTransitionActionImpl(claudenda_service, next_action_impl);
    clau.add(new Claudendum()
    {
      @Override
      public void close()
      {
        input.unsubscribe(reset_message);
        input.unsubscribe(eeb);
        claudenda_service.close();
      }
    });
    engine =
      new CompositeSerializationEngine(new ValueBeaconCSEPart<Integer>("input",
        input, default_value, IntegerSerializationEngine.DEFAULT)
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
        boolean connectedToEnvironment = false;

        public void connectToEnvironment()
        {
          super.connectToEnvironment();
          if (connectedToEnvironment)
            return;
          connectedToEnvironment = true;
          input.subscribe(reset_message);
          input.subscribe(eeb);
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

  private final Transition action_impl = new SimpleTransition();

  private final StateTransitionAction actioncurrent;

  private final StateTransitionAction actionnext;

  public StateTransitionAction getActionCurrent()
  {
    return actioncurrent;
  }

  private final Transition next_action_impl = new SimpleTransition();

  public StateTransitionAction getActionNext()
  {
    return actionnext;
  }

  public final SimpleBeacon<Integer> input;

  private SimpleBeaconImpl<ArrayList<Integer>> results_impl =
    new SimpleBeaconImpl<ArrayList<Integer>>();

  public SimpleBeaconReadable<? extends Iterable<? extends Integer>> results =
    results_impl;

  private final SerializationEngine engine;

  @Override
  public SerializationEngine getSerializationEngine()
  {
    return engine;
  }
}
