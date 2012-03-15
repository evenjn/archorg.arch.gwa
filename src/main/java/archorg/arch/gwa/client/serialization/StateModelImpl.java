package archorg.arch.gwa.client.serialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StateModelImpl
  implements
  ReadableStateModel,
  WritableStateModel
{
  public void load(
    HasStateLoader root) throws StateSerializationFormatException
  {
    root.getStateLoader().validate(this,
      "0");
    root.getStateLoader().load(this,
      "0");
    root.getStateLoader().postLoad();
  }

  public void dump(
    HasSerializableState s,
    StatefulAction a)
  {
    SerializableState serializableState = s.getSerializableState();
    String newID = newID();
    serializableState.dump(this,
      newID,
      a);
  }

  protected int sequence = 0;

  protected ArrayList<String> id_sequence = new ArrayList<String>();

  protected HashMap<String, Map<String, String>> map_s =
    new HashMap<String, Map<String, String>>();

  @Override
  public boolean specifies(
    String elementId,
    String part) throws StateSerializationFormatException
  {
    Map<String, String> map = map_s.get(elementId);
    if (map == null)
      throw new IllegalArgumentException("Element ID not valid.");
    return map.containsKey(part);
  }

  @Override
  public String unfold(
    String elementId,
    String part) throws StateSerializationFormatException
  {
    Map<String, String> map = map_s.get(elementId);
    if (map == null)
      throw new IllegalArgumentException("Element ID not valid.");
    if (!map.containsKey(part))
      throw new IllegalArgumentException(
        "Serialization does not contain information about Part ID " + part);
    return map.get(part);
  }

  @Override
  public String newID()
  {
    String id = new Integer(sequence).toString();
    sequence = sequence + 1;
    map_s.put(id,
      new HashMap<String, String>());
    id_sequence.add(id);
    return id;
  }

  @Override
  public void fold(
    String id,
    String part,
    String value)
  {
    Map<String, String> map = map_s.get(id);
    if (map == null)
      throw new IllegalArgumentException("Element ID not valid.");
    map.put(part,
      value);
  }

  @Override
  public String defaultMarker()
  {
    return "D";
  }

  @Override
  public boolean isDefaultMarker(
    String id)
  {
    return id.equals("D");
  }
}
