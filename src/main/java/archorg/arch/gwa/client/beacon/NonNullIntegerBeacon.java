package archorg.arch.gwa.client.beacon;

import it.celi.research.balrog.beacon.SimpleBeacon;
import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.parts.ValueBeaconCSEPart;

public abstract class NonNullIntegerBeacon
  extends
  ValueBeaconCSEPart<Integer>
{
  public NonNullIntegerBeacon(
    String beaconID,
    SimpleBeacon<Integer> beacon,
    Integer default_value)
  {
    super(beaconID, beacon, default_value);
  }

  @Override
  protected String encode(
    Integer value)
  {
    return value.toString();
  }

  @Override
  protected Integer decode(
    String value) throws SerializationException
  {
    if (value == null)
      throw new SerializationException("Illegal null value");
    else
    {
      try
      {
        return Integer.parseInt(value);
      }
      catch (Exception e)
      {
        throw new SerializationException("Integer.parseInteger("
            + value + ") failed.");
      }
    }
  }
}
