package archorg.arch.gwa.client.serialization.model.parts.value;

import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.parts.ValueSerializationEngine;

public class IntegerSerializationEngine
  implements
  ValueSerializationEngine<Integer>
{
  public static final IntegerSerializationEngine DEFAULT =
    new IntegerSerializationEngine();

  @Override
  public Integer deserialize(
    String serialized) throws SerializationException
  {
    if (serialized == null)
      return null;
    try
    {
      return Integer.parseInt(serialized);
    }
    catch (NumberFormatException e)
    {
      throw new SerializationException(e.getMessage());
    }
  }

  @Override
  public String serialize(
    Integer value)
  {
    if (value == null)
      return null;
    return value.toString();
  }
}
