package archorg.arch.gwa.client.serialization;

import java.util.Map;

public interface StateDeserialization
{
  Map<String, String> get(String id,
      boolean dryrun) throws StateSerializationFormatException;

  String getRootID();
  // SSerializable getDeserialized(String id);
  //
  // void putDeserialized(String id,
  // SSerializable deserialized);
}
