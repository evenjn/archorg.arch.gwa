package archorg.arch.gwa.client.serialization.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StateModelImpl
  implements
  ReadableStateModel,
  WritableStateModel
{
  public void load(
    HasObjectStateEngine root) throws StateSerializationFormatException
  {
    root.getObjectStateEngine().load(true,
      this,
      "0");
    root.getObjectStateEngine().load(false,
      this,
      "0");
    root.getObjectStateEngine().postLoad();
  }

  public void dump(
    HasObjectStateEngine s,
    Transition a)
  {
    ObjectStateEngine serializableState = s.getObjectStateEngine();
    serializableState.dump(this,
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
      return false;
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

  private String newID()
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
  public String getID()
  {
    return newID();
  }
}
