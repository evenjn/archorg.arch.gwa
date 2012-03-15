package archorg.arch.gwa.client.serialization;

public interface StateModelFactory
{
  String dump(
    HasSerializableState state,
    StatefulAction a);

  void load(
    HasStateLoader root,
    String encoded) throws StateSerializationFormatException;
}
