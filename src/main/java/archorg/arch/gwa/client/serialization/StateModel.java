package archorg.arch.gwa.client.serialization;


public interface StateModel
{
  void load(
    HasStateLoader root) throws StateSerializationFormatException;

  void dump(
    HasSerializableState s,
    StatefulAction a);
}
