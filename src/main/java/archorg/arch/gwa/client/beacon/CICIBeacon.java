package archorg.arch.gwa.client.beacon;

import it.celi.research.balrog.beacon.SimpleBeacon;
import archorg.arch.gwa.client.serialization.StatefulAction;
import archorg.arch.gwa.client.serialization.model.BeaconStateEngine;
import archorg.arch.gwa.client.serialization.model.HasBeaconStateEngine;
import archorg.arch.gwa.client.serialization.model.HasObjectStateEngine;
import archorg.arch.gwa.client.serialization.model.ReadableStateModel;
import archorg.arch.gwa.client.serialization.model.StateSerializationFormatException;
import archorg.arch.gwa.client.serialization.model.WritableStateModel;

/**
 * The default can only be null
 * 
 * @author evenjn
 * 
 * @param <T>
 */
public abstract class CICIBeacon<T extends HasObjectStateEngine>
  implements
  HasBeaconStateEngine
{
  private final String beaconID;

  private final SimpleBeacon<T> beacon;

  public abstract T create(
    boolean for_validation_only);

  public CICIBeacon(
    String beaconID,
    SimpleBeacon<T> beacon)
  {
    this.beaconID = beaconID;
    this.beacon = beacon;
  }

  private final BeaconStateEngine engine = new BeaconStateEngine()
  {
    @Override
    public void dump(
      WritableStateModel s,
      String container_id,
      StatefulAction a)
    {
      if (beacon.isNull())
        return;
      String vid = beacon.get().getObjectStateEngine().dump(s,
        a);
      s.fold(container_id,
        beaconID,
        vid);
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
        // if no information is provided, reset it to the default state
        // first, determine if the default is to be null
        if (!validate)
          beacon.setIfNotEqual(null);
        // and then if it is not null, ask the object to reset itself.
        // if (beacon.isNotNull())
        // beacon.get().getSerializableState().resetToDefault();
        return;
      }
      String string = s.unfold(id,
        beaconID);
      beacon.setIfNotEqual(create(validate));
      beacon.get().getObjectStateEngine().load(validate,
        s,
        string);
    }
  };

  @Override
  public BeaconStateEngine getBeaconStateEngine()
  {
    return engine;
  }
}
