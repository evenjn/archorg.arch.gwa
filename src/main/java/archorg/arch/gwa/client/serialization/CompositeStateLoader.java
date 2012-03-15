package archorg.arch.gwa.client.serialization;

import java.util.ArrayList;


public abstract class CompositeStateLoader
  implements
  StateLoader,
  SerializableState
{
  private ArrayList<HasBoth> ios = new ArrayList<HasBoth>();

  public void compose(
    HasBoth beacon)
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
  public void dump(
    WritableStateModel s,
    String id,
    StatefulAction a)
  {
    for (HasSerializableState bs : ios)
      if (!bs.getSerializableState().isAtDefault(a))
        bs.getSerializableState().dump(s,
          id,
          a);
  }

  protected abstract void resetTransient();
}
