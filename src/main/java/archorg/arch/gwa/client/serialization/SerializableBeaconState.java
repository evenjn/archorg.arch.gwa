package archorg.arch.gwa.client.serialization;

public interface SerializableBeaconState
{
  boolean dump(
    WritableStateModel s,
    String container_id,
    StatefulAction a);
}
