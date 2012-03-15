package archorg.arch.gwa.client.serialization;

public interface StateLoader
{
  void resetToDefault();

  void load(
    ReadableStateModel s,
    String id) throws StateSerializationFormatException;

  void validate(
    ReadableStateModel s,
    String id) throws StateSerializationFormatException;

  void postLoad();
}
