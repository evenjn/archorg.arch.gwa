package archorg.arch.gwa.client.serialization;

import java.util.ArrayList;

public abstract class CompositeSerializableState
  implements
  SerializableState
{
  private ArrayList<SerializableState> ios = new ArrayList<SerializableState>();

  public void compose(
    SerializableState beacon)
  {
    ios.add(beacon);
  }

  @Override
  public void dump(
    WritableStateModel s,
    String id)
  {
    for (SerializableState bs : ios)
      if (!bs.isAtDefault())
        bs.dump(s,
          id);
  }

  @Override
  public void load(
    ReadableStateModel s,
    String elementID) throws StateSerializationFormatException
  {
    for (SerializableState bs : ios)
      bs.load(s,
        elementID);
    resetTransient();
  }

  @Override
  public void validate(
    ReadableStateModel s,
    String elementID) throws StateSerializationFormatException
  {
    for (SerializableState bs : ios)
      bs.validate(s,
        elementID);
  }

  // @Override
  // public void resetToDefault()
  // {
  // for (SerializableState bs : ios)
  // bs.resetToDefault();
  // resetTransient();
  // }
   @Override
   public boolean isAtDefault()
   {
   for (SerializableState bs : ios)
   if (!bs.isAtDefault())
   return false;
   return true;
   }
  @Override
  public void postLoad()
  {
    for (SerializableState bs : ios)
      bs.postLoad();
  }

  protected abstract void resetTransient();
}
