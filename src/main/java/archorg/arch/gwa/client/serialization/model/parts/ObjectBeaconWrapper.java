package archorg.arch.gwa.client.serialization.model.parts;

import it.celi.research.balrog.beacon.SimpleBeacon;
import archorg.arch.gwa.client.serialization.model.BeaconStateEngine;
import archorg.arch.gwa.client.serialization.model.HasBeaconStateEngine;
import archorg.arch.gwa.client.serialization.model.HasObjectStateEngine;
import archorg.arch.gwa.client.serialization.model.ReadableStateModel;
import archorg.arch.gwa.client.serialization.model.StateSerializationFormatException;
import archorg.arch.gwa.client.serialization.model.Transition;
import archorg.arch.gwa.client.serialization.model.WritableStateModel;

public abstract class ObjectBeaconWrapper<T extends HasObjectStateEngine>
  implements
  HasBeaconStateEngine
{
  private final String beaconID;

  private final SimpleBeacon<T> beacon;

  private final boolean default_is_null;

  public abstract T create(
    boolean for_validation_only);

  public ObjectBeaconWrapper(
    String beaconID,
    SimpleBeacon<T> beacon,
    boolean default_is_null)
  {
    this.beaconID = beaconID;
    this.beacon = beacon;
    this.default_is_null = default_is_null;
  }

  public final static Transition SETNULL = new Transition();

  public final static Transition SETDEFAULT = new Transition();

  public final static Transition TOGGLENULLDEFAULT = new Transition();

  private final BeaconStateEngine engine = new BeaconStateEngine()
  {
    @Override
    public void dump(
      WritableStateModel s,
      String container_id,
      Transition a)
    {
      if (a == TOGGLENULLDEFAULT)
      {
        if (beacon.isNull())
        {
          String vid = create(true).getObjectStateEngine().dump(s,
            a);
          s.fold(container_id,
            beaconID,
            vid);
        } else
        {
          s.fold(container_id,
            beaconID,
            null);
        }
        return;
      }
      if (a == SETNULL)
      {
        s.fold(container_id,
          beaconID,
          null);
        return;
      }
      if (a == SETDEFAULT)
      {
        String vid = create(true).getObjectStateEngine().dump(s,
          a);
        s.fold(container_id,
          beaconID,
          vid);
        return;
      }
      if (beacon.isNull())
      {
        if (!default_is_null)
        {
          s.fold(container_id,
            beaconID,
            null);
        }
        return;
      } else
      {
        String vid = beacon.get().getObjectStateEngine().dump(s,
          a);
        s.fold(container_id,
          beaconID,
          vid);
      }
    }

    @Override
    public void postLoad()
    {
      if (beacon.isNotNull())
        beacon.get().getObjectStateEngine().postLoad();
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
        {
          if (default_is_null)
            beacon.setIfNotEqual(null);
          else
            beacon.setIfNotEqual(create(validate));
        }
        return;
      }
      String string = s.unfold(id,
        beaconID);
      if (string == null)
        beacon.setIfNotEqual(null);
      else
      {
        beacon.setIfNotEqual(create(validate));
        beacon.get().getObjectStateEngine().load(validate,
          s,
          string);
      }
    }
  };

  @Override
  public BeaconStateEngine getBeaconStateEngine()
  {
    return engine;
  }
}
