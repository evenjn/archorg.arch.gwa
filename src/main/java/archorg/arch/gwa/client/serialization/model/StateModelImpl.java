package archorg.arch.gwa.client.serialization.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StateModelImpl
  implements
  ReadableStateModel,
  WritableStateModel
{
  public void injectStateInto(
    HasSerializationEngine elementWithStateEngine) throws SerializationException
  {
    elementWithStateEngine.getSerializationEngine().loadState(true,
      this,
      "0");
    elementWithStateEngine.getSerializationEngine().loadState(false,
      this,
      "0");
    elementWithStateEngine.getSerializationEngine().connectToEnvironment();
    elementWithStateEngine.getSerializationEngine().postLoad();
  }

  public void readNextStateFrom(
    HasSerializationEngine elementWithStateEngine,
    Transition transition)
  {
    SerializationEngine serializableState = elementWithStateEngine.getSerializationEngine();
    serializableState.writeDestinationState(this,
      transition);
  }

  protected int sequence = 0;

  protected ArrayList<String> id_sequence = new ArrayList<String>();

  protected HashMap<String, Map<String, String>> map_s =
    new HashMap<String, Map<String, String>>();

  @Override
  public boolean hasValueForPart(
    String elementId,
    String partId) throws SerializationException
  {
    Map<String, String> map = map_s.get(elementId);
    if (map == null)
      return false;
    return map.containsKey(partId);
  }

  @Override
  public String getValueForPart(
    String elementId,
    String partId) throws SerializationException
  {
    Map<String, String> map = map_s.get(elementId);
    if (map == null)
      throw new IllegalArgumentException("Element ID not valid.");
    if (!map.containsKey(partId))
      throw new IllegalArgumentException(
        "Serialization does not contain information about Part ID " + partId);
    return map.get(partId);
  }

  @Override
  public void storeValueForPart(
    String elementId,
    String partId,
    String serializedValue)
  {
    Map<String, String> map = map_s.get(elementId);
    if (map == null)
      throw new IllegalArgumentException("Element ID not valid.");
    map.put(partId,
      serializedValue);
  }

  @Override
  public String getID()
  {
    String id = new Integer(sequence).toString();
    sequence = sequence + 1;
    map_s.put(id,
      new HashMap<String, String>());
    id_sequence.add(id);
    return id;
  }
}
