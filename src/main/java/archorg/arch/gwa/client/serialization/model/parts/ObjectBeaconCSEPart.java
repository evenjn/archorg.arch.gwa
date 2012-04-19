package archorg.arch.gwa.client.serialization.model.parts;

import it.celi.research.balrog.beacon.SimpleBeacon;
import it.celi.research.balrog.claudenda.Claudenda;
import it.celi.research.balrog.claudenda.ClaudendaService;
import it.celi.research.balrog.claudenda.ClaudendaServiceFactory;
import archorg.arch.gwa.client.serialization.model.HasSerializationEngine;
import archorg.arch.gwa.client.serialization.model.ReadableStateModel;
import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.SimpleTransition;
import archorg.arch.gwa.client.serialization.model.Transition;
import archorg.arch.gwa.client.serialization.model.WritableStateModel;

public abstract class ObjectBeaconCSEPart<T extends HasSerializationEngine>
  implements
  CompositeSerializationEnginePart
{
  private final String partId;

  private final SimpleBeacon<T> beacon;

  private final boolean default_is_null;

  public abstract T create(
    Claudenda clau);

  protected ObjectBeaconCSEPart(
    String partId,
    SimpleBeacon<T> beacon,
    boolean default_is_null)
  {
    this.partId = partId;
    this.beacon = beacon;
    this.default_is_null = default_is_null;
  }

  public final Transition SETNULL = new SimpleTransition();

  public final Transition SETDEFAULT = new SimpleTransition();

  public final Transition TOGGLENULLDEFAULT = new SimpleTransition();

  private ClaudendaService claudenda_service;

  @Override
  public boolean dump(
    WritableStateModel s,
    String container_id,
    Transition a)
  {
    if (a == TOGGLENULLDEFAULT)
    {
      if (beacon.isNull())
      {
        if (default_is_null)
        {
          // nothing to do! this behaves as a setnull
          return false;
        } else
        {
          ClaudendaService clau =
            ClaudendaServiceFactory.create(this.getClass().getName(),
              "dump");
          String vid =
            create(clau).getSerializationEngine().writeDestinationState(s,
              a);
          clau.close();
          if (vid == null)
            return false;
          else
          {
            s.storeValueForPart(container_id,
              partId,
              vid);
            return true;
          }
        }
      } else
      {
        s.storeValueForPart(container_id,
          partId,
          null);
        if (default_is_null)
          return false;
        else
          return true;
      }
    }
    if (a == SETNULL)
    {
      s.storeValueForPart(container_id,
        partId,
        null);
      if (default_is_null)
        return false;
      else
        return true;
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
        ClaudendaService clau =
          ClaudendaServiceFactory.create(getClass().getName());
        String vid =
          create(clau).getSerializationEngine().writeDestinationState(s,
            a);
        if (vid == null)
          vid = s.getID();
        s.storeValueForPart(container_id,
          partId,
          vid);
        clau.close();
      }
      return false;
    }
    if (beacon.isNull())
    {
      if (!default_is_null)
      {
        s.storeValueForPart(container_id,
          partId,
          null);
        return true;
      } else
      {
        return false;
      }
    } else
    {
      String vid =
        beacon.get().getSerializationEngine().writeDestinationState(s,
          a);
      if (vid == null)
        vid = s.getID();
      s.storeValueForPart(container_id,
        partId,
        vid);
      if (default_is_null)
        return true;
      else
      {
        if (vid == null)
          return false;
        else
          return true;
      }
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
        {
          claudenda_service.close();
        }
        beacon.setIfNotEqual(null);
        return;
      }
      if (beacon.isNull())
      {
        claudenda_service =
          ClaudendaServiceFactory.create(getClass().getName());
        beacon.setIfNotEqual(create(claudenda_service));
      }
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
      {
        // detaches all observers contained in this object
        claudenda_service.close();
        claudenda_service = null;
      }
      beacon.setIfNotEqual(null);
      return;
    }
    // it's a real string.
    if (validate)
    {
      T t;
      if (beacon.isNull())
      {
        ClaudendaService clau =
          ClaudendaServiceFactory.create(getClass().getName());
        t = create(clau);
        t.getSerializationEngine().loadState(true,
          s,
          string);
        clau.close();
      } else
      {
        t = beacon.get();
        t.getSerializationEngine().loadState(true,
          s,
          string);
      }
    }
    if (!validate)
    {
      T t;
      if (beacon.isNull())
      {
        claudenda_service =
          ClaudendaServiceFactory.create(getClass().getName());
        t = create(claudenda_service);
        t.getSerializationEngine().loadState(false,
          s,
          string);
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
  public void connectToEnvironment()
  {
    if (beacon.isNotNull())
      beacon.get().getSerializationEngine().connectToEnvironment();
  }

  @Override
  public void resetToDefaultState()
  {
    if (default_is_null)
    {
      // it's a reset to null
      if (beacon.isNotNull())
      {
        claudenda_service.close();
      }
      beacon.setIfNotEqual(null);
      return;
    }
    if (beacon.isNull())
    {
      claudenda_service = ClaudendaServiceFactory.create(getClass().getName());
      beacon.setIfNotEqual(create(claudenda_service));
    }
    // it's a reset!
    beacon.get().getSerializationEngine().resetToDefaultState();
  }
}
