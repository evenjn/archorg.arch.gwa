package archorg.arch.gwa.client.model;

import it.celi.research.balrog.beacon.SimpleBeacon;
import it.celi.research.balrog.beacon.SimpleBeaconChange;
import it.celi.research.balrog.beacon.SimpleBeaconImpl;
import it.celi.research.balrog.beacon.SimpleBeaconReadable;
import it.celi.research.balrog.event.EventChannel;
import it.celi.research.balrog.event.Observable;
import it.celi.research.balrog.event.Observer;
import archorg.arch.gwa.client.serialization.StateTransitionActionImpl;
import archorg.arch.gwa.client.serialization.Trigger;
import archorg.arch.gwa.client.serialization.model.HasSerializationEngine;
import archorg.arch.gwa.client.serialization.model.SerializationEngine;
import archorg.arch.gwa.client.serialization.model.parts.CompositeSerializationEngine;
import archorg.arch.gwa.client.serialization.model.parts.ObjectBeaconCSEPart;

/**
 * Because View objects are connected to Model objects with a double link (via a
 * Join object), they are a single system from the standpoint of memory
 * management. For this reason, it is important not to recycle Model objects,
 * because associating new View objects to old Model objects does not release
 * the old View objects connected to those Model objects.
 * 
 * @author evenjn
 * 
 */
public class RootModel
  implements
  HasSerializationEngine
{
  // output only, transient
  private SimpleBeaconImpl<String> message_impl = new SimpleBeaconImpl<String>(
    null);

  // input/output only, non-transient
  private SimpleBeaconImpl<Boolean> has_child_impl =
    new SimpleBeaconImpl<Boolean>(true);

  // output only, non-transient
  private SimpleBeaconImpl<ChildModel> child_impl =
    new SimpleBeaconImpl<ChildModel>();

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

  public SimpleBeaconReadable<? extends ChildModel> getChildBR()
  {
    return child_impl;
  }

  private final EventChannel<Void> envchan;

  private final Observer<? super Object> envco;

  public RootModel(
    EventChannel<Void> envchan,
    Observer<? super Object> envco)
  {
    this.envchan = envchan;
    this.envco = envco;
  }

  private Trigger<Boolean> create_child_trigger = new Trigger<Boolean>()
  {
    @Override
    public void onTrigger(
      Observable<? extends SimpleBeaconChange<? extends Boolean>> observable,
      SimpleBeaconChange<? extends Boolean> message)
    {
      if (message.newEqualsOld())
        return;
      if (message.getNew())
      {
        child_impl.setNevertheless(new ChildModel(0, envchan, envco,
          message_impl, reset_message_trigger));
        envchan.notify(null);
      } else
      {
        child_impl.setIfNotEqual(null);
        envchan.notify(null);
      }
    }
  };

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

  private final ObjectBeaconCSEPart<ChildModel> child_wrapper =
    new ObjectBeaconCSEPart<ChildModel>("child", child_impl, false)
    {
      @Override
      public ChildModel create()
      {
        ChildModel childModel =
          new ChildModel(0, envchan, envco, message_impl, reset_message_trigger);
        return childModel;
      }
    };

  private final SerializationEngine engine2 = new CompositeSerializationEngine(child_wrapper)
  {
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
      // child_impl.setNevertheless(new ChildModel(envchan, envco, message_impl,
      // reset_message_trigger));
      // has_child_impl.subscribe(create_child_trigger);
      has_child_impl.subscribe(reset_message_trigger);
      child_impl.subscribe(reset_message_trigger);
      has_child_impl.subscribe(envco);
      final StateTransitionActionImpl zchild_action =
        new StateTransitionActionImpl(child_wrapper.TOGGLENULLDEFAULT);
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
  public SerializationEngine getSerializationEngine()
  {
    return engine2;
  }
}
