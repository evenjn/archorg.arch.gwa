package archorg.arch.gwa.client.serialization;

public interface ReadableStateModel
{
  boolean specifies(String elementId,
      String part) throws StateSerializationFormatException;

  String unfold(String elementId,
      String part) throws StateSerializationFormatException;

  boolean isDefaultMarker(String id);
}
