package archorg.arch.gwa.client.beacon;

import it.celi.research.balrog.beacon.SimpleBeacon;
import archorg.arch.gwa.client.serialization.HasBoth;
import archorg.arch.gwa.client.serialization.ReadableStateModel;
import archorg.arch.gwa.client.serialization.SerializableState;
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
  HasBoth
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
        if (s.isDefaultMarker(string))
          beacon.get().getStateLoader().resetToDefault();
        else
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
        if (s.isDefaultMarker(string))
          beacon.get().getStateLoader().resetToDefault();
        else
          beacon.get().getStateLoader().load(s,
            string);
      }
    };
  }

  @Override
  public SerializableState getSerializableState()
  {
    return new SerializableState()
    {
      public boolean isAtDefault(
        StatefulAction a)
      {
        if (!beacon.isNull())
          return false;
        return true;
      }

      @Override
      public void dump(
        WritableStateModel s,
        String id,
        StatefulAction a)
      {
        // default can only be null
        // if default, this method will never be called
        String vid = s.defaultMarker();
        if (!beacon.get().getSerializableState().isAtDefault(a))
        {
          vid = s.newID();
          beacon.get().getSerializableState().dump(s,
            vid,
            a);
        }
        s.fold(id,
          beaconID,
          vid);
      }
    };
  }
}
