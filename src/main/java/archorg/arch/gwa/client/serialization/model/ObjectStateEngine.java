package archorg.arch.gwa.client.serialization.model;

import archorg.arch.gwa.client.serialization.StatefulAction;

public interface ObjectStateEngine
  extends
  StateLoader
{
  String dump(
    WritableStateModel s,
    StatefulAction a);
}
