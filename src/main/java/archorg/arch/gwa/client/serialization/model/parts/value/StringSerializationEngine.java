package archorg.arch.gwa.client.serialization.model.parts.value;

import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.parts.ValueSerializationEngine;

public class StringSerializationEngine
  implements
  ValueSerializationEngine<String>
{
  public static final StringSerializationEngine DEFAULT =
    new StringSerializationEngine();

  @Override
  public String deserialize(
    String serialized) throws SerializationException
  {
    return serialized;
  }

  @Override
  public String serialize(
    String value)
  {
    return value;
  }
}
