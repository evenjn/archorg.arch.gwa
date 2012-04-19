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
  private ArrayList<CompositeSerializationEnginePart> ios =
    new ArrayList<CompositeSerializationEnginePart>();

  protected CompositeSerializationEngine(
    CompositeSerializationEnginePart... beacons)
  {
    for (CompositeSerializationEnginePart beacon : beacons)
      ios.add(beacon);
  }

  public static CompositeSerializationEngine nu(
    CompositeSerializationEnginePart... beacons)
  {
    return new CompositeSerializationEngine(beacons);
  }

  @Override
  public void loadState(
    boolean validate,
    ReadableStateModel statemodel,
    String elementID) throws SerializationException
  {
    for (CompositeSerializationEnginePart bs : ios)
      bs.load(validate,
        statemodel,
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
    WritableStateModel statemodel,
    Transition transition)
  {
    String id = statemodel.getID();
    boolean anything_interesting = false;
    for (CompositeSerializationEnginePart bs : ios)
    {
      boolean interesting = bs.dump(statemodel,
        id,
        transition);
      if (interesting)
        anything_interesting = true;
    }
    if (anything_interesting)
      return id;
    else
      return null;
  }

  @Override
  public void connectToEnvironment()
  {
    for (CompositeSerializationEnginePart bs : ios)
      bs.connectToEnvironment();
  }

  @Override
  public void resetToDefaultState()
  {
    for (CompositeSerializationEnginePart bs : ios)
      bs.resetToDefaultState();
  }
}
