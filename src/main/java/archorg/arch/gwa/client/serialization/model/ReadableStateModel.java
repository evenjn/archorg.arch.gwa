package archorg.arch.gwa.client.serialization.model;

public interface ReadableStateModel
{
  boolean hasValueForPart(
    String elementId,
    String partId) throws SerializationException;

  String getValueForPart(
    String elementId,
    String partId) throws SerializationException;
}
