package archorg.arch.gwa.client.serialization.model.parts.value;

import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.parts.ValueSerializationEngine;

public class BooleanSerializationEngine
  implements
  ValueSerializationEngine<Boolean>
{
  public static final BooleanSerializationEngine DEFAULT =
    new BooleanSerializationEngine();

  @Override
  public Boolean deserialize(
    String serialized) throws SerializationException
  {
    if (serialized == null)
      return null;
    return Boolean.parseBoolean(serialized);
  }

  @Override
  public String serialize(
    Boolean value)
  {
    if (value == null)
      return null;
    return value.toString();
  }
}
