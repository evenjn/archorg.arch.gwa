package archorg.arch.gwa.client.serial.model.part;

import it.celi.research.balrog.beacon.SimpleBeacon;
import archorg.arch.gwa.client.serialization.model.ReadableStateModel;
import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.Transition;
import archorg.arch.gwa.client.serialization.model.WritableStateModel;
import archorg.arch.gwa.client.serialization.model.parts.ValueSerializationEngine;

public abstract class ValueBeaconCSEPartBare<T>
  implements
  CompositeSerializationEnginePartBare
{
  private final String beaconID;

  private final SimpleBeacon<T> beacon;

  private final T default_value;

  private final ValueSerializationEngine<T> engine;

  public static <T> ValueBeaconCSEPartBare<T> createStatic(
    String beaconID,
    SimpleBeacon<T> beacon,
    T default_value,
    ValueSerializationEngine<T> engine)
  {
    return new ValueBeaconCSEPartBare<T>(beaconID, beacon, default_value,
      engine)
    {
      @Override
      protected T transform(
        T value,
        Transition a)
      {
        return value;
      }
    };
  }

  protected ValueBeaconCSEPartBare(
    String beaconID,
    SimpleBeacon<T> beacon,
    T default_value,
    ValueSerializationEngine<T> engine)
  {
    this.beaconID = beaconID;
    this.beacon = beacon;
    this.default_value = default_value;
    this.engine = engine;
  }

  protected abstract T transform(
    T value,
    Transition a);

  @Override
  public boolean dump(
    WritableStateModel s,
    String container_id,
    Transition a)
  {
    T transformed = transform(beacon.get(),
      a);
    if (transformed == null && default_value == null)
      return false;
    if (transformed != null && default_value != null
        && transformed.equals(default_value))
      return false;
    String vid = engine.serialize(transformed);
    s.storeValueForPart(container_id,
      beaconID,
      vid);
    return true;
  }

  @Override
  public void load(
    ReadableStateModel s,
    String id) throws SerializationException
  {
    if (!s.hasValueForPart(id,
      beaconID))
    {
      beacon.setIfNotEqual(default_value);
      return;
    }
    String string = s.getValueForPart(id,
      beaconID);
    T decoded = engine.deserialize(string);
    beacon.setIfNotEqual(decoded);
  }
}
