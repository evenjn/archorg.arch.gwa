package archorg.arch.gwa.client.serialization.model;

public interface StateLoader
{
  void load(
    boolean validate,
    ReadableStateModel s,
    String id) throws StateSerializationFormatException;

  void postLoad();
}
