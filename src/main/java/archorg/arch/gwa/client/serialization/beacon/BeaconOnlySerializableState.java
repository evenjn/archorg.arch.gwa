package archorg.arch.gwa.client.serialization.beacon;

import it.celi.research.balrog.beacon.Beacon;
import it.celi.research.balrog.claudenda.Claudenda;

import java.util.ArrayList;
import java.util.Map;

import archorg.arch.gwa.client.serialization.SerializableState;
import archorg.arch.gwa.client.serialization.StateDeserialization;
import archorg.arch.gwa.client.serialization.StateSerialization;
import archorg.arch.gwa.client.serialization.StateSerializationFormatException;

public abstract class BeaconOnlySerializableState extends SerializableState
{
  public BeaconOnlySerializableState(Claudenda clau)
  {
    super(clau);
  }

  private ArrayList<SerializableBeacon<?>> ios =
    new ArrayList<SerializableBeacon<?>>();

  private ArrayList<Beacon<?>> os = new ArrayList<Beacon<?>>();

  public void addTransientBeacon(Beacon<?> beacon)
  {
    os.add(beacon);
  }

  public <T> void addNonTransientBeacon(SerializableBeacon<T> beacon)
  {
    ios.add(beacon);
  }

  @Override
  public void dump(StateSerialization s,
      Map<String, String> map)
  {
    for (SerializableBeacon<?> bs : ios)
      bs.dump(s,
        map);
  }

  @Override
  public void load(StateDeserialization s,
      String id,
      boolean dryrun) throws StateSerializationFormatException
  {
    Map<String, String> map = s.get(id,
      dryrun);
    for (SerializableBeacon<?> bs : ios)
      bs.load(s,
        map,
        dryrun);
    if (!dryrun)
      for (Beacon<?> b : os)
        b.resetToDefault(true);
  }

  @Override
  public void resetToDefault()
  {
    for (SerializableBeacon<?> bs : ios)
      bs.resetToDefault();
    for (Beacon<?> b : os)
      b.resetToDefault(true);
  }

  @Override
  public boolean isAtDefault()
  {
    for (SerializableBeacon<?> bs : ios)
      if (!bs.isAtDefault())
        return false;
    return true;
  }
}
