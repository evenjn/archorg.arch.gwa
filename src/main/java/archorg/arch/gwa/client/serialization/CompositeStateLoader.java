package archorg.arch.gwa.client.serialization;

import java.util.ArrayList;

public abstract class CompositeStateLoader
  implements
  StateLoader,
  SerializableState
{
  private ArrayList<BeaconHasBoth> ios = new ArrayList<BeaconHasBoth>();

  public void compose(
    BeaconHasBoth beacon)
  {
    ios.add(beacon);
  }

  @Override
  public void load(
    ReadableStateModel s,
    String elementID) throws StateSerializationFormatException
  {
    for (HasStateLoader bs : ios)
      bs.getStateLoader().load(s,
        elementID);
    resetTransient();
  }

  @Override
  public void validate(
    ReadableStateModel s,
    String elementID) throws StateSerializationFormatException
  {
    for (HasStateLoader bs : ios)
      bs.getStateLoader().validate(s,
        elementID);
  }

  @Override
  public void resetToDefault()
  {
    for (HasStateLoader bs : ios)
      bs.getStateLoader().resetToDefault();
    resetTransient();
  }

  @Override
  public void postLoad()
  {
    for (HasStateLoader bs : ios)
      bs.getStateLoader().postLoad();
  }

  @Override
  public String dump(
    WritableStateModel s,
    StatefulAction a)
  {
    String id = s.getID();
    boolean isdefault = true;
    for (BeaconHasBoth bs : ios)
    {
      boolean not_empty = bs.getSerializableState().dump(s,
        id,
        a);
      if (not_empty)
        isdefault = false;
    }
    if (isdefault)
      return s.defaultMarker();
    else
      return id;
  }

  protected abstract void resetTransient();
}
