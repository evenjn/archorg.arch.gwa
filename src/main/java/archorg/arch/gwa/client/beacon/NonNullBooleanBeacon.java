package archorg.arch.gwa.client.beacon;

import it.celi.research.balrog.beacon.SimpleBeacon;
import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.parts.ValueBeaconCSEPart;

public abstract class NonNullBooleanBeacon
  extends
  ValueBeaconCSEPart<Boolean>
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
    String value) throws SerializationException
  {
    if (value == null)
      throw new SerializationException("Illegal null value");
    else
    {
      try
      {
        return Boolean.parseBoolean(value);
      }
      catch (Exception e)
      {
        throw new SerializationException("Boolean.parseBoolean("
            + value + ") failed.");
      }
    }
  }
}
