package archorg.arch.gwa.client.serialization.model.parts;

import it.celi.research.balrog.beacon.SimpleBeacon;
import archorg.arch.gwa.client.serialization.model.ReadableStateModel;
import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.Transition;
import archorg.arch.gwa.client.serialization.model.WritableStateModel;

public abstract class ValueBeaconCSEPart<T>
  implements
  CompositeSerializationEnginePart
{
  private final String beaconID;

  private final SimpleBeacon<T> beacon;

  private final T default_value;

  public ValueBeaconCSEPart(
    String beaconID,
    SimpleBeacon<T> beacon,
    T default_value)
  {
    this.beaconID = beaconID;
    this.beacon = beacon;
    this.default_value = default_value;
  }

  protected abstract String encode(
    T value);

  protected abstract T decode(
    String value) throws SerializationException;

  protected abstract T transform(
    T value,
    Transition a);

  @Override
  public void dump(
    WritableStateModel s,
    String container_id,
    Transition a)
  {
    T transformed = transform(beacon.get(),
      a);
    if (transformed == null && default_value == null)
      return;
    if (transformed != null && default_value != null
        && transformed.equals(default_value))
      return;
    String vid = encode(transformed);
    s.storeValueForPart(container_id,
      beaconID,
      vid);
  }

  @Override
  public void postLoad()
  {}

  @Override
  public void load(
    boolean validate,
    ReadableStateModel s,
    String id) throws SerializationException
  {
    if (!s.hasValueForPart(id,
      beaconID))
    {
      if (!validate)
        beacon.setIfNotEqual(default_value);
      return;
    }
    String string = s.getValueForPart(id,
      beaconID);
    T decoded = decode(string);
    if (!validate)
      beacon.setIfNotEqual(decoded);
  }

  @Override
  public void link()
  {
    // nothing to link
  }

  @Override
  public void unlink()
  {
    // nothing to unlink
  }
}
