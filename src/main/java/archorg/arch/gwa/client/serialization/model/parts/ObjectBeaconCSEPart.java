package archorg.arch.gwa.client.serialization.model.parts;

import it.celi.research.balrog.beacon.SimpleBeacon;
import archorg.arch.gwa.client.serialization.model.HasSerializationEngine;
import archorg.arch.gwa.client.serialization.model.ReadableStateModel;
import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.Transition;
import archorg.arch.gwa.client.serialization.model.WritableStateModel;

public abstract class ObjectBeaconCSEPart<T extends HasSerializationEngine>
  implements
  CompositeSerializationEnginePart
{
  private final String partId;

  private final SimpleBeacon<T> beacon;

  private final boolean default_is_null;

  public abstract T create();

  public ObjectBeaconCSEPart(
    String partId,
    SimpleBeacon<T> beacon,
    boolean default_is_null)
  {
    this.partId = partId;
    this.beacon = beacon;
    this.default_is_null = default_is_null;
  }

  public final Transition SETNULL = new Transition();

  public final Transition SETDEFAULT = new Transition();

  public final Transition TOGGLENULLDEFAULT = new Transition();

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
        String vid = create().getSerializationEngine().writeDestinationState(s,
          a);
        s.storeValueForPart(container_id,
          partId,
          vid);
      } else
      {
        s.storeValueForPart(container_id,
          partId,
          null);
      }
      return;
    }
    if (a == SETNULL)
    {
      s.storeValueForPart(container_id,
        partId,
        null);
      return;
    }
    if (a == SETDEFAULT)
    {
      if (default_is_null)
      {
        s.storeValueForPart(container_id,
          partId,
          null);
      } else
      {
        String vid = create().getSerializationEngine().writeDestinationState(s,
          a);
        s.storeValueForPart(container_id,
          partId,
          vid);
      }
      return;
    }
    if (beacon.isNull())
    {
      if (!default_is_null)
      {
        s.storeValueForPart(container_id,
          partId,
          null);
      }
      return;
    } else
    {
      String vid = beacon.get().getSerializationEngine().writeDestinationState(s,
        a);
      s.storeValueForPart(container_id,
        partId,
        vid);
    }
  }

  @Override
  public void postLoad()
  {
    if (beacon.isNotNull())
      beacon.get().getSerializationEngine().postLoad();
  }

  @Override
  public void load(
    boolean validate,
    ReadableStateModel s,
    String id) throws SerializationException
  {
    if (!s.hasValueForPart(id,
      partId))
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
          beacon.get().getSerializationEngine().disconnectFromEnvironment();
        beacon.setIfNotEqual(null);
        return;
      }
      if (beacon.isNull())
        beacon.setIfNotEqual(create());
      // this system asks the object in the beacon to load an empty state,
      // it's a reset!
      beacon.get().getSerializationEngine().loadState(validate,
        s,
        null);
      return;
    }
    String string = s.getValueForPart(id,
      partId);
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
        beacon.get().getSerializationEngine().disconnectFromEnvironment();
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
      t.getSerializationEngine().loadState(true,
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
      t.getSerializationEngine().loadState(false,
        s,
        string);
      return;
    }
  }

  @Override
  public void link()
  {
    if (beacon.isNotNull())
      beacon.get().getSerializationEngine().connectToEnvironment();
  }

  @Override
  public void unlink()
  {
    if (beacon.isNotNull())
      beacon.get().getSerializationEngine().disconnectFromEnvironment();
  }
}
