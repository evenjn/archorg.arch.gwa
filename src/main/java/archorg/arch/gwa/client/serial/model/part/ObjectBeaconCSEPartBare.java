package archorg.arch.gwa.client.serial.model.part;

import it.celi.research.balrog.beacon.SimpleBeacon;
import it.celi.research.balrog.claudenda.Claudenda;
import it.celi.research.balrog.claudenda.ClaudendaService;
import it.celi.research.balrog.claudenda.ClaudendaServiceFactory;
import archorg.arch.gwa.client.serial.model.HasSerializationEngineBare;
import archorg.arch.gwa.client.serialization.model.ReadableStateModel;
import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.SimpleTransition;
import archorg.arch.gwa.client.serialization.model.Transition;
import archorg.arch.gwa.client.serialization.model.WritableStateModel;

public abstract class ObjectBeaconCSEPartBare<T extends HasSerializationEngineBare>
  implements
  CompositeSerializationEnginePartBare
{
  private final String partId;

  private final SimpleBeacon<T> beacon;

  private final boolean default_is_null;

  public abstract T create(
    Claudenda clau);

  protected ObjectBeaconCSEPartBare(
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
            create(clau).getSerializationEngineBare().writeDestinationState(s,
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
        ClaudendaService clau = ClaudendaServiceFactory.create(this.getClass());
        String vid =
          create(clau).getSerializationEngineBare().writeDestinationState(s,
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
        beacon.get().getSerializationEngineBare().writeDestinationState(s,
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
  public void load(
    ReadableStateModel s,
    String id) throws SerializationException
  {
    if (!s.hasValueForPart(id,
      partId))
    {
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
        claudenda_service = ClaudendaServiceFactory.create(this.getClass());
        beacon.setIfNotEqual(create(claudenda_service));
      }
      // this system asks the object in the beacon to load an empty state,
      // it's a reset!
      beacon.get().getSerializationEngineBare().loadState(s,
        null);
      return;
    }
    String string = s.getValueForPart(id,
      partId);
    if (string == null)
    {
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
    T t;
    if (beacon.isNull())
    {
      claudenda_service = ClaudendaServiceFactory.create(this.getClass());
      t = create(claudenda_service);
      t.getSerializationEngineBare().loadState(s,
        string);
      beacon.setIfNotEqual(t);
    } else
    {
      t = beacon.get();
    }
    t.getSerializationEngineBare().loadState(s,
      string);
    return;
  }
}
