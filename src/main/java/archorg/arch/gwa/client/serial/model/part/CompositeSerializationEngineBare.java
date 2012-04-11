package archorg.arch.gwa.client.serial.model.part;

import java.util.ArrayList;

import archorg.arch.gwa.client.serial.model.SerializationEngineBare;
import archorg.arch.gwa.client.serialization.model.ReadableStateModel;
import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.Transition;
import archorg.arch.gwa.client.serialization.model.WritableStateModel;

public class CompositeSerializationEngineBare
  implements
  SerializationEngineBare
{
  private ArrayList<CompositeSerializationEnginePartBare> ios =
    new ArrayList<CompositeSerializationEnginePartBare>();

  protected CompositeSerializationEngineBare(
    CompositeSerializationEnginePartBare... beacons)
  {
    for (CompositeSerializationEnginePartBare beacon : beacons)
      ios.add(beacon);
  }

  public static CompositeSerializationEngineBare create(
    CompositeSerializationEnginePartBare... beacons)
  {
    return new CompositeSerializationEngineBare(beacons);
  }

  @Override
  public void loadState(
    ReadableStateModel statemodel,
    String elementID) throws SerializationException
  {
    for (CompositeSerializationEnginePartBare bs : ios)
      bs.load(statemodel,
        elementID);
  }

  @Override
  public String writeDestinationState(
    WritableStateModel statemodel,
    Transition transition)
  {
    String id = statemodel.getID();
    boolean anything_interesting = false;
    for (CompositeSerializationEnginePartBare bs : ios)
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
}
