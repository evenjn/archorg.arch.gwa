package archorg.arch.gwa.client.serialization.model.parts;

import it.celi.research.balrog.beacon.SimpleBeacon;
import archorg.arch.gwa.client.serialization.model.BeaconStateEngine;
import archorg.arch.gwa.client.serialization.model.HasBeaconStateEngine;
import archorg.arch.gwa.client.serialization.model.HasObjectStateEngine;
import archorg.arch.gwa.client.serialization.model.ReadableStateModel;
import archorg.arch.gwa.client.serialization.model.StateSerializationFormatException;
import archorg.arch.gwa.client.serialization.model.Transition;
import archorg.arch.gwa.client.serialization.model.WritableStateModel;

public abstract class ObjectBeaconWrapper<T extends HasObjectStateEngine>
  implements
  HasBeaconStateEngine
{
  private final String beaconID;

  private final SimpleBeacon<T> beacon;

  private final boolean default_is_null;

  public abstract T create();

  public ObjectBeaconWrapper(
    String beaconID,
    SimpleBeacon<T> beacon,
    boolean default_is_null)
  {
    this.beaconID = beaconID;
    this.beacon = beacon;
    this.default_is_null = default_is_null;
  }

  public final Transition SETNULL = new Transition();

  public final Transition SETDEFAULT = new Transition();

  public final Transition TOGGLENULLDEFAULT = new Transition();

  private final BeaconStateEngine engine = new BeaconStateEngine()
  {
    @Override
    public void dump(
      WritableStateModel s,
      String container_id,
      Transition a)
    {
      if (a == TOGGLENULLDEFAULT)
      {
        if (beacon.isNull())
        {
          String vid = create().getObjectStateEngine().dump(s,
            a);
          s.fold(container_id,
            beaconID,
            vid);
        } else
        {
          s.fold(container_id,
            beaconID,
            null);
        }
        return;
      }
      if (a == SETNULL)
      {
        s.fold(container_id,
          beaconID,
          null);
        return;
      }
      if (a == SETDEFAULT)
      {
        if (default_is_null)
        {
          s.fold(container_id,
            beaconID,
            null);
        } else
        {
          String vid = create().getObjectStateEngine().dump(s,
            a);
          s.fold(container_id,
            beaconID,
            vid);
        }
        return;
      }
      if (beacon.isNull())
      {
        if (!default_is_null)
        {
          s.fold(container_id,
            beaconID,
            null);
        }
        return;
      } else
      {
        String vid = beacon.get().getObjectStateEngine().dump(s,
          a);
        s.fold(container_id,
          beaconID,
          vid);
      }
    }

    @Override
    public void postLoad()
    {
      if (beacon.isNotNull())
        beacon.get().getObjectStateEngine().postLoad();
    }

    @Override
    public void load(
      boolean validate,
      ReadableStateModel s,
      String id) throws StateSerializationFormatException
    {
      if (!s.specifies(id,
        beaconID))
      {
        if (validate)
        {
          // if this invocation is for validation of the serialization, then
          // this system does not
          // have anything to validate, because the serialization is empty here.
          return;
        }
        // this is real loading and nothing to load. it's a reset!
        if (default_is_null)
        {
          // it's a reset to null
          if (beacon.isNotNull())
            // detaches all observers contained in this object
            beacon.get().getObjectStateEngine().unlink();
          beacon.setIfNotEqual(null);
          return;
        }
        if (beacon.isNull())
          beacon.setIfNotEqual(create());
        // this system asks the object in the beacon to load an empty state,
        // it's a reset!
        beacon.get().getObjectStateEngine().load(validate,
          s,
          id);
        return;
      }
      String string = s.unfold(id,
        beaconID);
      if (string == null)
      {
        if (validate)
        {
          // nothing to validate.
          return;
        }
        // it's a set to null
        if (beacon.isNotNull())
          // detaches all observers contained in this object
          beacon.get().getObjectStateEngine().unlink();
        beacon.setIfNotEqual(null);
        return;
      }
      // it's a real string.
      if (validate)
      {
        T t;
        if (beacon.isNull())
        {
          t = create();
        } else
        {
          t = beacon.get();
        }
        t.getObjectStateEngine().load(true,
          s,
          string);
      }
      if (!validate)
      {
        T t;
        if (beacon.isNull())
        {
          t = create();
          beacon.setIfNotEqual(t);
        } else
        {
          t = beacon.get();
        }
        t.getObjectStateEngine().load(false,
          s,
          string);
        return;
      }
    }

    @Override
    public void link()
    {
      if (beacon.isNotNull())
        beacon.get().getObjectStateEngine().link();
    }

    @Override
    public void unlink()
    {
      if (beacon.isNotNull())
        beacon.get().getObjectStateEngine().unlink();
    }
  };

  @Override
  public BeaconStateEngine getBeaconStateEngine()
  {
    return engine;
  }
}
