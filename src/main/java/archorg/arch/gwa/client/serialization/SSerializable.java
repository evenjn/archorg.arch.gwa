package archorg.arch.gwa.client.serialization;

import java.util.Map;

public interface SSerializable
{
  void dump(SSerialization s,
      Map<String, String> map);

  void load(SDeserialization s,
      String id,
      boolean dryrun) throws SSerializationFormatException;

  void resetToDefaults();

  boolean isAtDefaults();
}
