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
import archorg.arch.gwa.client.serial.model.HasSerializationEngineBare;
import archorg.arch.gwa.client.serial.model.SerializationEngineBare;
import archorg.arch.gwa.client.serial.model.part.CompositeSerializationEngineBare;
import archorg.arch.gwa.client.serial.model.part.ValueBeaconCSEPartBare;
import archorg.arch.gwa.client.serialization.EnvironmentEventBus;
import archorg.arch.gwa.client.serialization.Trigger;
import archorg.arch.gwa.client.serialization.model.parts.value.IntegerSerializationEngine;
import archorg.arch.gwa.client.stateful.model.HasLoadingEngine;
import archorg.arch.gwa.client.stateful.model.LoadingEngine;
import archorg.arch.gwa.client.stateful.model.parts.CompositeLoadingEngine;
import archorg.arch.gwa.shared.Input;
import archorg.arch.gwa.shared.Output;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ChildFactory
{
  private final int default_value;

  private final EnvironmentEventBus eeb;

  private final SimpleBeacon<String> message_impl;

  private final Trigger<Object> reset_message;

  public ChildFactory(
    final int default_value,
    final EnvironmentEventBus eeb,
    final SimpleBeacon<String> message_impl,
    final Trigger<Object> reset_message)
  {
    this.default_value = default_value;
    this.eeb = eeb;
    this.message_impl = message_impl;
    this.reset_message = reset_message;
  }

  public ChildModel createModel(
    Claudenda clau)
  {
    return new ChildModel(clau);
  }

  public ChildState createState(
    Integer value)
  {
    return new ChildState(value);
  }

  public ChildState createState()
  {
    return new ChildState(0);
  }

  public final class ChildModel
    implements
    HasLoadingEngine
  {
    private final LoadingEngine engine;

    private ClaudendaService claudenda_service;

    public ChildModel(
      final Claudenda clau)
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
      engine = new CompositeLoadingEngine()
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

    public final SimpleBeacon<Integer> input;

    private SimpleBeaconImpl<ArrayList<Integer>> results_impl =
      new SimpleBeaconImpl<ArrayList<Integer>>();

    public SimpleBeaconReadable<? extends Iterable<? extends Integer>> results =
      results_impl;

    @Override
    public LoadingEngine getLoadingEngine()
    {
      return engine;
    }
  }

  public final class ChildState
    implements
    HasSerializationEngineBare
  {
    private ChildState(
      Integer value)
    {
      innerinput =
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
      innerinput.setIfNotEqual(value);
      ValueBeaconCSEPartBare<Integer> valueBeaconCSEPartBare =
        ValueBeaconCSEPartBare.createStatic("input",
          innerinput,
          default_value,
          IntegerSerializationEngine.DEFAULT);
      engine = CompositeSerializationEngineBare.create(valueBeaconCSEPartBare);
    }

    private final SafeSimpleBeaconImpl<Integer> innerinput;

    private final SerializationEngineBare engine;

    public Integer getValue()
    {
      return innerinput.get();
    };

    @Override
    public SerializationEngineBare getSerializationEngineBare()
    {
      return engine;
    }
  }
}
