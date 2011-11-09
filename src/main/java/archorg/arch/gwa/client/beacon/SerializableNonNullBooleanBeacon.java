package archorg.arch.gwa.client.beacon;

import it.celi.research.balrog.beacon.Beacon;
import archorg.arch.gwa.client.serialization.HasSerializableState;
import archorg.arch.gwa.client.serialization.SerializableState;
import archorg.arch.gwa.client.serialization.ReadableStateModel;
import archorg.arch.gwa.client.serialization.WritableStateModel;
import archorg.arch.gwa.client.serialization.StateSerializationFormatException;

public class SerializableNonNullBooleanBeacon implements HasSerializableState
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

  @Override
  public SerializableState getSerializableState()
  {
    return state;
  }

  private MySerializableState state = new MySerializableState();

  private class MySerializableState implements SerializableState
  {
    public boolean isAtDefault()
    {
      return beacon.isAtDefault();
    }

    public void resetToDefault()
    {
      beacon.resetToDefault(true);
    }

    public void dump(WritableStateModel s,
        String id)
    {
      if (beacon.isNull())
        throw new IllegalStateException(
          "null boolean is currently not supported");
      s.fold(id,
        beaconID,
        beacon.get().toString());
    }

    @Override
    public void load(ReadableStateModel s,
        String id) throws StateSerializationFormatException
    {
      if (!s.specifies(id,
        beaconID))
      {
        beacon.resetToDefault(true);
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
    public void validate(ReadableStateModel s,
        String id) throws StateSerializationFormatException
    {
      if (!s.specifies(id,
        beaconID))
      {
        return;
      }
      String string = s.unfold(id,
        beaconID);
      // here we should wrap everything in a serializable beacon with
      // restricted
      // values so that one checks if it's null, if it is a legal value, etc.
      if (string == null)
        throw new StateSerializationFormatException("illegal null value");
    }

    @Override
    public void postLoad()
    {}
  }
}
