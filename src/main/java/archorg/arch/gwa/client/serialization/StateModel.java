package archorg.arch.gwa.client.serialization;

public interface StateModel
{
  void load(HasSerializableState root) throws StateSerializationFormatException;

  void dump(HasSerializableState s);
}
