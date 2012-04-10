package archorg.arch.gwa.client.serialization.model.parts.value;

import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.parts.ValueSerializationEngine;

public class FloatSerializationEngine
  implements
  ValueSerializationEngine<Float>
{
  public static final FloatSerializationEngine DEFAULT =
    new FloatSerializationEngine();

  @Override
  public Float deserialize(
    String serialized) throws SerializationException
  {
    if (serialized == null)
      return null;
    try
    {
      return Float.parseFloat(serialized);
    }
    catch (NumberFormatException e)
    {
      throw new SerializationException(e.getMessage());
    }
  }

  @Override
  public String serialize(
    Float value)
  {
    if (value == null)
      return null;
    return value.toString();
  }
}
