package archorg.arch.gwa.client.serialization;

public interface SerializableBeaconState
{
  void dump(
    WritableStateModel s,
    String container_id,
    StatefulAction a);
}
