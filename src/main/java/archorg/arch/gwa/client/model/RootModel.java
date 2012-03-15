package archorg.arch.gwa.client.model;

import it.celi.research.balrog.beacon.SimpleBeacon;
import it.celi.research.balrog.beacon.SimpleBeaconChange;
import it.celi.research.balrog.beacon.SimpleBeaconImpl;
import it.celi.research.balrog.beacon.SimpleBeaconReadable;
import it.celi.research.balrog.event.EventChannel;
import it.celi.research.balrog.event.Observable;
import it.celi.research.balrog.event.Observer;
import archorg.arch.gwa.client.beacon.CICIBeacon;
import archorg.arch.gwa.client.beacon.CINonNullBooleanBeacon;
import archorg.arch.gwa.client.serialization.CompositeStateLoader;
import archorg.arch.gwa.client.serialization.HasBoth;
import archorg.arch.gwa.client.serialization.SerializableState;
import archorg.arch.gwa.client.serialization.StateLoader;
import archorg.arch.gwa.client.serialization.StatefulAction;
import archorg.arch.gwa.client.serialization.StatefulActionImpl;
import archorg.arch.gwa.client.serialization.Trigger;

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
  HasBoth
{
  // output only, transient
  private SimpleBeaconImpl<String> message_impl = new SimpleBeaconImpl<String>(
    null);

  // input/output only, non-transient
  private SimpleBeaconImpl<Boolean> has_child_impl =
    new SimpleBeaconImpl<Boolean>(false);

  // output only, non-transient
  private SimpleBeaconImpl<ChildModel> child_impl =
    new SimpleBeaconImpl<ChildModel>();

  private final Observer<? super Object> envco;

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

  private final StatefulActionImpl child_action = new StatefulActionImpl();

  private final EventChannel<Void> envchan;

  public RootModel(
    EventChannel<Void> envchan,
    Observer<? super Object> envco)
  {
    this.envchan = envchan;
    this.envco = envco;
    has_child_impl.subscribe(create_child_trigger);
    has_child_impl.subscribe(reset_message_trigger);
    child_impl.subscribe(reset_message_trigger);
    has_child_impl.subscribe(envco);
    has_child_impl.subscribe(new Observer<Object>()
    {
      @Override
      public void notice(
        Observable<? extends Object> observable,
        Object message)
      {
        child_action.execute();
      }
    });
    // has_child_impl.setSaveOnEvent(true);// omg!
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
        child_impl.setNevertheless(new ChildModel(envchan, envco, message_impl,
          reset_message_trigger));
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

  @Override
  public StateLoader getStateLoader()
  {
    return myboon;
  }

  private final CICIBeacon<ChildModel> ccc = new CICIBeacon<ChildModel>(
    "child", child_impl)
  {
    // our problem here is that we can't just create and destroy ChildModels
    // without managing the claudenda
    @Override
    public ChildModel create(
      boolean dryrun)
    {
      if (dryrun)
      {
        ChildModel childModel = new ChildModel(null, null, message_impl, null);
        return childModel;
      }
      ChildModel childModel =
        new ChildModel(envchan, envco, message_impl, reset_message_trigger);
      return childModel;
    }
  };

  private MyBoon myboon = new MyBoon();

  private class MyBoon
    extends
    CompositeStateLoader
  {
    public MyBoon()
    {
      compose(ccc);
      compose(new CINonNullBooleanBeacon("hc", has_child_impl, false));
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
      // has_child_impl.setIfNotEqual(false);
    }

    @Override
    public boolean isAtDefault(
      StatefulAction a)
    {
      // TODO Auto-generated method stub
      return has_child_impl.valueEquals(false)
          && ccc.getSerializableState().isAtDefault(a);
    }
  }

  @Override
  public SerializableState getSerializableState()
  {
    return myboon;
  }
}
