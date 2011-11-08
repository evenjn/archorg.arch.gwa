package archorg.arch.gwa.client.serialization.beacon;

import it.celi.research.balrog.beacon.Beacon;

import java.util.Map;

import archorg.arch.gwa.client.serialization.StateDeserialization;
import archorg.arch.gwa.client.serialization.StateSerialization;
import archorg.arch.gwa.client.serialization.StateSerializationFormatException;

public class SerializableNonNullBooleanBeacon
  implements SerializableBeacon<Boolean>
{
  private final String beaconID;

  private final Beacon<Boolean> beacon;

  public Beacon<Boolean> getBeacon()
  {
    return beacon;
  }

  public SerializableNonNullBooleanBeacon(String beaconID,
      Beacon<Boolean> beacon)
  {
    this.beaconID = beaconID;
    this.beacon = beacon;
  }

  public boolean isAtDefault()
  {
    return beacon.isAtDefault();
  }

  public void resetToDefault()
  {
    beacon.resetToDefault(true);
  }

  public void load(StateDeserialization s,
      Map<String, String> map,
      boolean dryrun) throws StateSerializationFormatException
  {
    if (map.containsKey(beaconID))
    {
      String string = map.get(beaconID);
      // here we should wrap everything in a serializable beacon with
      // restricted
      // values so that one checks if it's null, if it is a legal value, etc.
      if (string == null)
        throw new StateSerializationFormatException("illegal null value");
      else
      {
        try
        {
          boolean parseBoolean = Boolean.parseBoolean(string);
          if (!dryrun)
            beacon.setIfNotEqual(parseBoolean);
        }
        catch (Exception e)
        {
          throw new StateSerializationFormatException("Boolean.parseBoolean("
              + string + ")");
        }
      }
    } else
    {
      if (!dryrun)
        beacon.resetToDefault(true);
    }
  }

  public void dump(StateSerialization s,
      Map<String, String> map)
  {
    if (!beacon.isAtDefault())
    {
      if (beacon.isNull())
        throw new IllegalStateException(
          "null boolean is currently not supported");
      map.put(beaconID,
        beacon.get().toString());
    }
  }
}
