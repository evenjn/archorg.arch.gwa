package archorg.arch.gwa.client.serialization.model.parts;

import java.util.ArrayList;

import archorg.arch.gwa.client.serialization.StatefulAction;
import archorg.arch.gwa.client.serialization.model.HasBeaconStateEngine;
import archorg.arch.gwa.client.serialization.model.ObjectStateEngine;
import archorg.arch.gwa.client.serialization.model.ReadableStateModel;
import archorg.arch.gwa.client.serialization.model.StateSerializationFormatException;
import archorg.arch.gwa.client.serialization.model.WritableStateModel;

public class BeaconStateEngineAggregation
  implements
  ObjectStateEngine
{
  private ArrayList<HasBeaconStateEngine> ios =
    new ArrayList<HasBeaconStateEngine>();

  public BeaconStateEngineAggregation(
    HasBeaconStateEngine... beacons)
  {
    for (HasBeaconStateEngine beacon : beacons)
      ios.add(beacon);
  }

  @Override
  public void load(
    boolean validate,
    ReadableStateModel s,
    String elementID) throws StateSerializationFormatException
  {
    for (HasBeaconStateEngine bs : ios)
      bs.getBeaconStateEngine().load(validate,
        s,
        elementID);
  }

  @Override
  public void postLoad()
  {
    for (HasBeaconStateEngine bs : ios)
      bs.getBeaconStateEngine().postLoad();
  }

  @Override
  public String dump(
    WritableStateModel s,
    StatefulAction a)
  {
    String id = s.getID();
    for (HasBeaconStateEngine bs : ios)
    {
      bs.getBeaconStateEngine().dump(s,
        id,
        a);
    }
    return id;
  }
}
