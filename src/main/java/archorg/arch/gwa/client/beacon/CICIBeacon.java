package archorg.arch.gwa.client.beacon;

import it.celi.research.balrog.beacon.SimpleBeacon;
import archorg.arch.gwa.client.serialization.BeaconHasBoth;
import archorg.arch.gwa.client.serialization.HasBoth;
import archorg.arch.gwa.client.serialization.ReadableStateModel;
import archorg.arch.gwa.client.serialization.SerializableBeaconState;
import archorg.arch.gwa.client.serialization.StateLoader;
import archorg.arch.gwa.client.serialization.StateSerializationFormatException;
import archorg.arch.gwa.client.serialization.StatefulAction;
import archorg.arch.gwa.client.serialization.WritableStateModel;

/**
 * The default can only be null
 * 
 * @author evenjn
 * 
 * @param <T>
 */
public abstract class CICIBeacon<T extends HasBoth>
  implements
  BeaconHasBoth
{
  private final String beaconID;

  private final SimpleBeacon<T> beacon;

  public abstract T create(
    boolean for_validation_only);

  public SimpleBeacon<T> getBeacon()
  {
    return beacon;
  }

  public CICIBeacon(
    String beaconID,
    SimpleBeacon<T> beacon)
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
  public StateLoader getStateLoader()
  {
    return new StateLoader()
    {
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
        beacon.setIfNotEqual(create(true));
        beacon.get().getStateLoader().load(s,
          string);
      }

      @Override
      public void resetToDefault()
      {
        beacon.setIfNotEqual(null);
      }

      @Override
      public void postLoad()
      {
        if (beacon.isNotNull())
          beacon.get().getStateLoader().postLoad();
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
        beacon.get().getStateLoader().load(s,
          string);
      }
    };
  }

  @Override
  public SerializableBeaconState getSerializableState()
  {
    return new SerializableBeaconState()
    {
      @Override
      public void dump(
        WritableStateModel s,
        String container_id,
        StatefulAction a)
      {
        if (beacon.isNull())
          return;
        String vid = beacon.get().getSerializableState().dump(s,
          a);
        s.fold(container_id,
          beaconID,
          vid);
      }
    };
  }
}
