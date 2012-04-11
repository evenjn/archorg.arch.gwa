package archorg.arch.gwa.client.model;

import it.celi.research.balrog.beacon.SimpleBeacon;
import it.celi.research.balrog.beacon.SimpleBeaconChange;
import it.celi.research.balrog.beacon.SimpleBeaconImpl;
import it.celi.research.balrog.beacon.SimpleBeaconReadable;
import it.celi.research.balrog.claudenda.Claudenda;
import it.celi.research.balrog.event.Observable;
import it.celi.research.balrog.event.Observer;
import archorg.arch.gwa.client.model.ChildFactory.ChildState;
import archorg.arch.gwa.client.serial.model.HasSerializationEngineBare;
import archorg.arch.gwa.client.serial.model.SerializationEngineBare;
import archorg.arch.gwa.client.serial.model.part.CompositeSerializationEngineBare;
import archorg.arch.gwa.client.serial.model.part.ObjectBeaconCSEPartBare;
import archorg.arch.gwa.client.serialization.EnvironmentEventBus;
import archorg.arch.gwa.client.serialization.StateTransitionActionImpl;
import archorg.arch.gwa.client.serialization.Trigger;
import archorg.arch.gwa.client.stateful.model.HasLoadingEngine;
import archorg.arch.gwa.client.stateful.model.LoadingEngine;
import archorg.arch.gwa.client.stateful.model.parts.CompositeLoadingEngine;
import archorg.arch.gwa.client.stateful.model.parts.ObjectBeaconCLEPart;

public class RootFactory
{
  private final ChildFactory cf;

  public RootFactory(
    ChildFactory cf)
  {
    this.cf = cf;
  }

  public final class RootModel
    implements
    HasLoadingEngine
  {
    // output only, transient
    private SimpleBeaconImpl<String> message_impl =
      new SimpleBeaconImpl<String>(null);

    // input/output only, non-transient
    private SimpleBeaconImpl<Boolean> has_child_impl =
      new SimpleBeaconImpl<Boolean>(true);

    // output only, non-transient
    private SimpleBeaconImpl<ChildFactory.ChildModel> child_impl =
      new SimpleBeaconImpl<ChildFactory.ChildModel>();

    public SimpleBeacon<? super String> getMessageBW()
    {
      return message_impl;
    }

    public SimpleBeaconReadable<? extends String> getMessageBR()
    {
      return message_impl;
    }

    public SimpleBeacon<Boolean> getHasChildB()
    {
      return has_child_impl;
    }

    public SimpleBeaconReadable<? extends ChildFactory.ChildModel> getChildBR()
    {
      return child_impl;
    }

    private final EnvironmentEventBus eeb;

    private final Claudenda clau;

    public RootModel(
      Claudenda clau,
      EnvironmentEventBus eeb)
    {
      this.clau = clau;
      this.eeb = eeb;
    }

    private Trigger<Object> reset_message_trigger = new Trigger<Object>()
    {
      @Override
      public void onTrigger(
        Observable<? extends SimpleBeaconChange<? extends Object>> observable,
        SimpleBeaconChange<? extends Object> message)
      {
        message_impl.setIfNotEqual(null);
      }
    };

    private final ObjectBeaconCLEPart<ChildFactory.ChildModel> child_wrapper =
      new ObjectBeaconCLEPart<ChildFactory.ChildModel>(child_impl)
      {
        @Override
        public ChildFactory.ChildModel create(
          Claudenda clau)
        {
          ChildFactory.ChildModel childModel = cf.createModel(clau);
          return childModel;
        }
      };

    private final LoadingEngine engine = new CompositeLoadingEngine(
      child_wrapper)
    {
      private StateTransitionActionImpl zchild_action;

      @Override
      public void postLoad()
      {
        super.postLoad();
        if (child_impl.isNotNull())
          has_child_impl.setIfNotEqual(true);
        else
          has_child_impl.setIfNotEqual(false);
        message_impl.setIfNotEqual(null);
      }

      private boolean linked = false;

      @Override
      public void connectToEnvironment()
      {
        super.connectToEnvironment();
        if (linked)
          return;
        linked = true;
        has_child_impl.subscribe(reset_message_trigger);
        child_impl.subscribe(reset_message_trigger);
        has_child_impl.subscribe(eeb);
        zchild_action =
          new StateTransitionActionImpl(clau, child_wrapper.TOGGLENULLDEFAULT);
        has_child_impl.subscribe(new Observer<Object>()
        {
          @Override
          public void notice(
            Observable<? extends Object> observable,
            Object message)
          {
            zchild_action.execute();
          }
        });
      }
    };

    @Override
    public LoadingEngine getLoadingEngine()
    {
      return engine;
    }
  }

  public RootState create()
  {
    return new RootState(null);
  }

  public final class RootState
    implements
    HasSerializationEngineBare
  {
    public RootState(
      ChildFactory.ChildState child)
    {
      child_state.setIfNotEqual(child);
    }

    private SimpleBeaconImpl<ChildFactory.ChildState> child_state =
      new SimpleBeaconImpl<ChildFactory.ChildState>();

    private final ObjectBeaconCSEPartBare<ChildFactory.ChildState> child_wrapper =
      new ObjectBeaconCSEPartBare<ChildFactory.ChildState>("child",
        child_state, false)
      {
        @Override
        public ChildState create(
          Claudenda clau)
        {
          return cf.createState();
        }
      };

    private final SerializationEngineBare engine =
      CompositeSerializationEngineBare.create(child_wrapper);

    @Override
    public SerializationEngineBare getSerializationEngineBare()
    {
      return engine;
    }
  }
}
