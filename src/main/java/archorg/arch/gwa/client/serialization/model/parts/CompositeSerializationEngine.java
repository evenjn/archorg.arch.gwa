package archorg.arch.gwa.client.serialization.model.parts;

import java.util.ArrayList;

import archorg.arch.gwa.client.serialization.model.ReadableStateModel;
import archorg.arch.gwa.client.serialization.model.SerializationEngine;
import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.Transition;
import archorg.arch.gwa.client.serialization.model.WritableStateModel;

public class CompositeSerializationEngine
  implements
  SerializationEngine
{
  private ArrayList<CompositeSerializationEnginePart> ios = new ArrayList<CompositeSerializationEnginePart>();

  public CompositeSerializationEngine(
    CompositeSerializationEnginePart... beacons)
  {
    for (CompositeSerializationEnginePart beacon : beacons)
      ios.add(beacon);
  }

  @Override
  public void loadState(
    boolean validate,
    ReadableStateModel s,
    String elementID) throws SerializationException
  {
    for (CompositeSerializationEnginePart bs : ios)
      bs.load(validate,
        s,
        elementID);
  }

  @Override
  public void postLoad()
  {
    for (CompositeSerializationEnginePart bs : ios)
      bs.postLoad();
  }

  @Override
  public String writeDestinationState(
    WritableStateModel s,
    Transition a)
  {
    String id = s.getID();
    for (CompositeSerializationEnginePart bs : ios)
    {
      bs.dump(s,
        id,
        a);
    }
    return id;
  }

  @Override
  public void connectToEnvironment()
  {
    for (CompositeSerializationEnginePart bs : ios)
      bs.link();
  }

  @Override
  public void disconnectFromEnvironment()
  {
    for (CompositeSerializationEnginePart bs : ios)
      bs.unlink();
  }
}
