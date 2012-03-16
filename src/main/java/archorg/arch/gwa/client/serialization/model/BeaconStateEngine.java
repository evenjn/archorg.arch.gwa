package archorg.arch.gwa.client.serialization.model;


public interface BeaconStateEngine
  extends
  StateLoader
{
  void dump(
    WritableStateModel s,
    String container_id,
    Transition a);
}
