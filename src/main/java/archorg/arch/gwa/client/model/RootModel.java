package archorg.arch.gwa.client.model;

import it.celi.research.balrog.beacon.SimpleBeacon;
import it.celi.research.balrog.beacon.SimpleBeaconChange;
import it.celi.research.balrog.beacon.SimpleBeaconReadable;
import it.celi.research.balrog.event.Observable;
import archorg.arch.gwa.client.beacon.NullDefaultBeacon;
import archorg.arch.gwa.client.beacon.SerializableHasSerializableStateBeacon;
import archorg.arch.gwa.client.serialization.CompositeSerializableState;
import archorg.arch.gwa.client.serialization.HasSerializableState;
import archorg.arch.gwa.client.serialization.SerializableState;
import archorg.arch.gwa.client.serialization.Trigger;
import archorg.arch.gwa.client.serialization.TriggerBeacon;

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
  HasSerializableState
{
  // output only, transient
  private TriggerBeacon<String> message_impl = new TriggerBeacon<String>(null);

  // input/output only, non-transient
  private TriggerBeacon<Boolean> has_child_impl = new TriggerBeacon<Boolean>(
    false);

  // output only, non-transient
  private NullDefaultBeacon<ChildModel> child_impl =
    new NullDefaultBeacon<ChildModel>();

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

  public RootModel()
  {
    has_child_impl.subscribe(create_child_trigger);
    has_child_impl.subscribe(reset_message_trigger);
    child_impl.subscribe(reset_message_trigger);
    has_child_impl.setSaveOnEvent(true);
    state = new MySerializableState();
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
        child_impl.setNevertheless(new ChildModel(message_impl,
          reset_message_trigger));
      } else
      {
        child_impl.setIfNotEqual(null);
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

  @Override
  public SerializableState getSerializableState()
  {
    return state;
  }

  private SerializableState state;

  private class MySerializableState
    extends
    CompositeSerializableState
  {
    public MySerializableState()
    {
      compose(new SerializableHasSerializableStateBeacon<ChildModel>("child",
        child_impl)
      {
        // our problem here is that we can't just create and destroy ChildModels
        // without managing the claudenda
        @Override
        public ChildModel create(
          boolean dryrun)
        {
          ChildModel childModel =
            new ChildModel(message_impl, reset_message_trigger);
          return childModel;
        }
      });
    }

    @Override
    public void postLoad()
    {
      super.postLoad();
      if (child_impl.isNotNull())
        has_child_impl.setIfNotEqual(true);
      else
        has_child_impl.setIfNotEqual(false);
    }

    @Override
    protected void resetTransient()
    {
      message_impl.setIfNotEqual(null);
      has_child_impl.setIfNotEqual(false);
    }
  }
}
