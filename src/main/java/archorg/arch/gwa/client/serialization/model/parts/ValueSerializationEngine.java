package archorg.arch.gwa.client.serialization.model.parts;

import archorg.arch.gwa.client.serialization.model.SerializationException;

public interface ValueSerializationEngine<T>
{
  T deserialize(
    String serialized) throws SerializationException;

  String serialize(
    T value);
}
