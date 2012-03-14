package archorg.arch.gwa.client.beacon;

import it.celi.research.balrog.beacon.SimpleBeacon;
import archorg.arch.gwa.client.serialization.HasSerializableState;
import archorg.arch.gwa.client.serialization.ReadableStateModel;
import archorg.arch.gwa.client.serialization.SerializableState;
import archorg.arch.gwa.client.serialization.StateSerializationFormatException;
import archorg.arch.gwa.client.serialization.WritableStateModel;

/**
 * The default can only be null
 * 
 * @author evenjn
 * 
 * @param <T>
 */
public abstract class SerializableHasSerializableStateBeacon<T extends HasSerializableState>
  implements
  SerializableState
{
  private final String beaconID;

  private final SimpleBeacon<T> beacon;

  public abstract T create(
    boolean for_validation_only);

  public SimpleBeacon<T> getBeacon()
  {
    return beacon;
  }

  public SerializableHasSerializableStateBeacon(
    String beaconID,
    NullDefaultBeacon<T> beacon)
  {
    this.beaconID = beaconID;
    this.beacon = beacon;
  }

  public boolean isAtDefault()
  {
    if (!beacon.isNull())
      return false;
    return true;
  }

  public void resetToDefault()
  {
    beacon.setIfNotEqual(null);
  }

  @Override
  public void load(
    ReadableStateModel s,
    String id) throws StateSerializationFormatException
  {
    if (!s.specifies(id,
      beaconID))
    {
      // if no information is provided, reset it to the default state
      // first, determine if the default is to be null
      beacon.setIfNotEqual(null);
      // and then if it is not null, ask the object to reset itself.
      // if (beacon.isNotNull())
      // beacon.get().getSerializableState().resetToDefault();
      return;
    }
    String string = s.unfold(id,
      beaconID);
    beacon.setIfNotEqual(create(false));
    beacon.get().getSerializableState().load(s,
      string);
  }

  @Override
  public void validate(
    ReadableStateModel s,
    String id) throws StateSerializationFormatException
  {
    if (!s.specifies(id,
      beaconID))
      return;
    String string = s.unfold(id,
      beaconID);
  }

  @Override
  public void dump(
    WritableStateModel s,
    String id)
  {
    // default can only be null
    // if default, this method will never be called
    // String vid = s.defaultMarker();
    if (!beacon.get().getSerializableState().isAtDefault())
    {
      String vid = s.newID();
      beacon.get().getSerializableState().dump(s,
        vid);
    }
    // s.fold(id,
    // beaconID,
    // vid);
  }

  @Override
  public void postLoad()
  {
    if (beacon.isNotNull())
      beacon.get().getSerializableState().postLoad();
  }
}
