package archorg.arch.gwa.client.serialization.model;

public interface WritableStateModel
{
  void fold(
    String id,
    String part,
    String value);

  String getID();
}
