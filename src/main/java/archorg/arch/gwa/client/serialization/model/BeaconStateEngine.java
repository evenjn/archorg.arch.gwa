package archorg.arch.gwa.client.serialization.model;

import archorg.arch.gwa.client.serialization.StatefulAction;

public interface BeaconStateEngine
  extends
  StateLoader
{
  void dump(
    WritableStateModel s,
    String container_id,
    StatefulAction a);
}
