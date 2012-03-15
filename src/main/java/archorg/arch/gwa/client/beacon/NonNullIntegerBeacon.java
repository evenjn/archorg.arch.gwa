package archorg.arch.gwa.client.beacon;

import it.celi.research.balrog.beacon.SimpleBeacon;
import archorg.arch.gwa.client.serialization.model.StateSerializationFormatException;
import archorg.arch.gwa.client.serialization.model.parts.ValueBeaconWrapper;

public abstract class NonNullIntegerBeacon
  extends
  ValueBeaconWrapper<Integer>
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
    String value) throws StateSerializationFormatException
  {
    if (value == null)
      throw new StateSerializationFormatException("Illegal null value");
    else
    {
      try
      {
        return Integer.parseInt(value);
      }
      catch (Exception e)
      {
        throw new StateSerializationFormatException("Integer.parseInteger("
            + value + ") failed.");
      }
    }
  }
}
