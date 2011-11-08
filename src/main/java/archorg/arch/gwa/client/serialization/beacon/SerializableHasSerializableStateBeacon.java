package archorg.arch.gwa.client.serialization.beacon;

import it.celi.research.balrog.beacon.Beacon;

import java.util.Map;

import archorg.arch.gwa.client.serialization.HasSerializableState;
import archorg.arch.gwa.client.serialization.StateDeserialization;
import archorg.arch.gwa.client.serialization.StateSerialization;
import archorg.arch.gwa.client.serialization.StateSerializationFormatException;

public abstract class SerializableHasSerializableStateBeacon<T extends HasSerializableState>
  implements SerializableBeacon<T>
{
  private final String beaconID;

  private final Beacon<T> beacon;

  public abstract T create(boolean dryrun);

  public Beacon<T> getBeacon()
  {
    return beacon;
  }

  public SerializableHasSerializableStateBeacon(String beaconID,
      Beacon<T> beacon)
  {
    this.beaconID = beaconID;
    this.beacon = beacon;
  }

  public boolean isAtDefault()
  {
    if (!beacon.isAtDefault())
      return false;
    if (!beacon.isNull() && !beacon.get().getSerializableState().isAtDefault())
      return false;
    return true;
  }

  public void resetToDefault()
  {
    beacon.resetToDefault(true);
    if (beacon.isNotNull())
      beacon.get().getSerializableState().resetToDefault();
  }

  @Override
  public void load(StateDeserialization s,
      Map<String, String> map,
      boolean dryrun) throws StateSerializationFormatException
  {
    if (map.containsKey(beaconID))
    {
      String string = map.get(beaconID);
      if (string == null)
      {
        if (!dryrun)
        {
          beacon.setIfNotEqual(null);
        }
      } else
      {
        if (!dryrun)
        {
          if (beacon.isNull())
          {
            beacon.setIfNotEqual(create(dryrun));
          }
          beacon.get().getSerializableState().load(s,
            string,
            dryrun);
        } else
        {
          create(dryrun).getSerializableState().load(s,
            string,
            dryrun);
        }
      }
    } else
    {
      if (!dryrun)
      {
        // if no information is provided, reset it to the default state
        // first, determine if the default is to be null
        beacon.resetToDefault(true);
        // and then if it is not null, ask the object to reset itself.
        if (beacon.isNotNull())
          beacon.get().getSerializableState().resetToDefault();
      }
    }
  }

  @Override
  public void dump(StateSerialization s,
      Map<String, String> map)
  {
    if (!beacon.isAtDefault())
      map.put(beaconID,
        s.getID(beacon.get()));
  }
}
