package archorg.arch.gwa.client.serialization;

public interface SerializableState
{
  void resetToDefault();

  void load(ReadableStateModel s,
      String id) throws StateSerializationFormatException;

  void validate(ReadableStateModel s,
      String id) throws StateSerializationFormatException;

  void postLoad();

  boolean isAtDefault();

  void dump(WritableStateModel s,
      String id);
}
