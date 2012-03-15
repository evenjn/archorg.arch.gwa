package archorg.arch.gwa.client.serialization;

public interface WritableStateModel
{
  String newID();

  String defaultMarker();

  void fold(
    String id,
    String part,
    String value);
}
