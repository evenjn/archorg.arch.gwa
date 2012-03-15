package archorg.arch.gwa.client.serialization.model.parts;

import it.celi.research.balrog.beacon.SimpleBeacon;
import archorg.arch.gwa.client.serialization.StatefulAction;
import archorg.arch.gwa.client.serialization.model.BeaconStateEngine;
import archorg.arch.gwa.client.serialization.model.HasBeaconStateEngine;
import archorg.arch.gwa.client.serialization.model.ReadableStateModel;
import archorg.arch.gwa.client.serialization.model.StateSerializationFormatException;
import archorg.arch.gwa.client.serialization.model.WritableStateModel;

public abstract class ValueBeaconWrapper<T>
  implements
  HasBeaconStateEngine
{
  private final String beaconID;

  private final SimpleBeacon<T> beacon;

  private final T default_value;

  public ValueBeaconWrapper(
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
    String value) throws StateSerializationFormatException;

  protected abstract T transform(
    T value,
    StatefulAction a);

  protected abstract void postLoad();

  private final BeaconStateEngine engine = new BeaconStateEngine()
  {
    @Override
    public void dump(
      WritableStateModel s,
      String container_id,
      StatefulAction a)
    {
      T transformed = transform(beacon.get(),
        a);
      if (transformed == null && default_value == null)
        return;
      if (transformed != null && default_value != null
          && transformed.equals(default_value))
        return;
      String vid = encode(transformed);
      s.fold(container_id,
        beaconID,
        vid);
    }

    @Override
    public void postLoad()
    {
      ValueBeaconWrapper.this.postLoad();
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
        if (!validate)
          beacon.setIfNotEqual(default_value);
        return;
      }
      String string = s.unfold(id,
        beaconID);
      beacon.setIfNotEqual(decode(string));
    }
  };

  @Override
  public BeaconStateEngine getBeaconStateEngine()
  {
    return engine;
  }
}
