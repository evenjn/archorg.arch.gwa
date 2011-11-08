package archorg.arch.gwa.client.serialization.beacon;

import it.celi.research.balrog.beacon.Beacon;

import java.util.Map;

import archorg.arch.gwa.client.serialization.StateDeserialization;
import archorg.arch.gwa.client.serialization.StateSerialization;
import archorg.arch.gwa.client.serialization.StateSerializationFormatException;

public interface SerializableBeacon<T>
{
  public void resetToDefault();

  public void load(StateDeserialization s,
      Map<String, String> map,
      boolean dryrun) throws StateSerializationFormatException;

  public boolean isAtDefault();

  public void dump(StateSerialization s,
      Map<String, String> map);

  public Beacon<T> getBeacon();
}
