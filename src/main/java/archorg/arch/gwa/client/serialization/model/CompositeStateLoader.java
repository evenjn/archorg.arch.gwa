package archorg.arch.gwa.client.serialization.model;

import java.util.ArrayList;

import archorg.arch.gwa.client.serialization.StatefulAction;

public abstract class CompositeStateLoader
  implements
  ObjectStateEngine
{
  private ArrayList<HasBeaconStateEngine> ios =
    new ArrayList<HasBeaconStateEngine>();

  public void compose(
    HasBeaconStateEngine beacon)
  {
    ios.add(beacon);
  }

  @Override
  public void load(
    boolean validate,
    ReadableStateModel s,
    String elementID) throws StateSerializationFormatException
  {
    for (HasBeaconStateEngine bs : ios)
      bs.getBeaconStateEngine().load(validate,
        s,
        elementID);
    if (!validate)
      resetTransient();
  }

  @Override
  public void postLoad()
  {
    for (HasBeaconStateEngine bs : ios)
      bs.getBeaconStateEngine().postLoad();
  }

  @Override
  public String dump(
    WritableStateModel s,
    StatefulAction a)
  {
    String id = s.getID();
    for (HasBeaconStateEngine bs : ios)
    {
      bs.getBeaconStateEngine().dump(s,
        id,
        a);
    }
    return id;
  }

  protected abstract void resetTransient();
}
