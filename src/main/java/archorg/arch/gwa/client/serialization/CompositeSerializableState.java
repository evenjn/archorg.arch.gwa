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
    String id,
    StatefulAction a)
  {
    for (SerializableState bs : ios)
      if (!bs.isAtDefault(a))
        bs.dump(s,
          id,
          a);
  }
}
