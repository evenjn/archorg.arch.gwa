package archorg.arch.gwa.client.serialization.model;

public interface WritableStateModel
{
  void storeValueForPart(
    String elementId,
    String partId,
    String serializedValue);

  String getID();
}
