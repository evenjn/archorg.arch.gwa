package archorg.arch.gwa.client.model;

import it.celi.research.balrog.beacon.Beacon;
import it.celi.research.balrog.beacon.BeaconImpl;
import it.celi.research.balrog.beacon.BeaconReader;
import it.celi.research.balrog.beacon.BeaconWriter;
import it.celi.research.balrog.beacon.Change;
import it.celi.research.balrog.event.Observable;
import archorg.arch.gwa.client.Trigger;
import archorg.arch.gwa.client.URIStateManager;
import archorg.arch.gwa.client.beacon.NullDefaultBeacon;
import archorg.arch.gwa.client.beacon.SerializableHasSerializableStateBeacon;
import archorg.arch.gwa.client.serialization.CompositeSerializableState;
import archorg.arch.gwa.client.serialization.HasSerializableState;
import archorg.arch.gwa.client.serialization.SerializableState;

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
public class RootModel implements HasSerializableState
{
  // output only, transient
  private BeaconImpl<String> message_impl = new BeaconImpl<String>(null);

  // input/output only, non-transient
  private BeaconImpl<Boolean> has_child_impl = new BeaconImpl<Boolean>(false);

  // output only, non-transient
  private NullDefaultBeacon<ChildModel> child_impl =
    new NullDefaultBeacon<ChildModel>();

  public BeaconWriter<? super String> getMessageBW()
  {
    return message_impl;
  }

  public BeaconReader<? extends String> getMessageBR()
  {
    return message_impl;
  }

  public Beacon<Boolean> getHasChildB()
  {
    return has_child_impl;
  }

  public BeaconReader<? extends ChildModel> getChildBR()
  {
    return child_impl;
  }

  public RootModel()
  {
    // triggers will always resolve before serialization may occur
    // it is important to dispose triggers (just like listeners) because they
    // may hang on unreachable objects.
    // objects
    has_child_impl.subscribe(create_child_trigger);
    // serialization
    state = new MySerializableState();
    // tells the hardlink manager to store the state whenever this observable
    // broadcasts an event
    URIStateManager.saveOnEvent(has_child_impl);
  }

  private Trigger<Boolean> create_child_trigger = new Trigger<Boolean>()
  {
    @Override
    public void
        onTrigger(Observable<? extends Change<? extends Boolean>> observable,
            Change<? extends Boolean> message)
    {
      if (message.newEqualsOld())
        return;
      if (message.getNew())
      {
        child_impl.setNevertheless(new ChildModel(message_impl));
      } else
      {
        child_impl.setIfNotEqual(null);
      }
    }
  };

  @Override
  public SerializableState getSerializableState()
  {
    return state;
  }

  private SerializableState state;

  private class MySerializableState extends CompositeSerializableState
  {
    public MySerializableState()
    {
      compose(new SerializableHasSerializableStateBeacon<ChildModel>("child",
        child_impl)
      {
        // our problem here is that we can't just create and destroy ChildModels
        // without managing the claudenda
        @Override
        public ChildModel create(boolean dryrun)
        {
          ChildModel childModel = new ChildModel(message_impl);
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
      message_impl.resetToDefault(true);
      has_child_impl.resetToDefault(true);
    }
  }
}
