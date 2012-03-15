package archorg.arch.gwa.client.beacon;

import it.celi.research.balrog.beacon.SimpleBeacon;
import archorg.arch.gwa.client.serialization.StatefulAction;
import archorg.arch.gwa.client.serialization.model.BeaconStateEngine;
import archorg.arch.gwa.client.serialization.model.HasBeaconStateEngine;
import archorg.arch.gwa.client.serialization.model.ReadableStateModel;
import archorg.arch.gwa.client.serialization.model.StateSerializationFormatException;
import archorg.arch.gwa.client.serialization.model.WritableStateModel;

public class CINonNullBooleanBeacon
  implements
  HasBeaconStateEngine
{
  private final String beaconID;

  private final SimpleBeacon<Boolean> beacon;

  private final Boolean defaultt;

  public SimpleBeacon<Boolean> getBeacon()
  {
    return beacon;
  }

  public CINonNullBooleanBeacon(
    String beaconID,
    SimpleBeacon<Boolean> beacon,
    Boolean defaultt)
  {
    this.beaconID = beaconID;
    this.beacon = beacon;
    this.defaultt = defaultt;
  }

  private final BeaconStateEngine engine = new BeaconStateEngine()
  {
    public void dump(
      WritableStateModel s,
      String container_id,
      StatefulAction a)
    {
      if (beacon.isNull())
        throw new IllegalStateException(
          "null boolean is currently not supported");
      if (!beacon.valueEquals(defaultt))
      {
        s.fold(container_id,
          beaconID,
          beacon.get().toString());
      }
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
        // ? what to do if nothing is specified?
        if (!validate)
          beacon.setIfNotEqual(defaultt);
        return;
      }
      String string = s.unfold(id,
        beaconID);
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
          if (!validate)
            beacon.setIfNotEqual(parseBoolean);
        }
        catch (Exception e)
        {
          throw new StateSerializationFormatException("Boolean.parseBoolean("
              + string + ")");
        }
      }
    }

    @Override
    public void postLoad()
    {}
  };

  @Override
  public BeaconStateEngine getBeaconStateEngine()
  {
    return engine;
  }
}
