package archorg.arch.gwa.client.beacon;

import it.celi.research.balrog.beacon.SimpleBeacon;
import archorg.arch.gwa.client.serialization.model.StateSerializationFormatException;
import archorg.arch.gwa.client.serialization.model.parts.ValueBeaconWrapper;

public abstract class NonNullBooleanBeacon
  extends
  ValueBeaconWrapper<Boolean>
{
  public NonNullBooleanBeacon(
    String beaconID,
    SimpleBeacon<Boolean> beacon,
    Boolean default_value)
  {
    super(beaconID, beacon, default_value);
  }

  @Override
  protected String encode(
    Boolean value)
  {
    return value.toString();
  }

  @Override
  protected Boolean decode(
    String value) throws StateSerializationFormatException
  {
    if (value == null)
      throw new StateSerializationFormatException("Illegal null value");
    else
    {
      try
      {
        return Boolean.parseBoolean(value);
      }
      catch (Exception e)
      {
        throw new StateSerializationFormatException("Boolean.parseBoolean("
            + value + ") failed.");
      }
    }
  }
}
