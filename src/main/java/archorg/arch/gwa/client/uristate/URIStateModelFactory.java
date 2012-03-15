package archorg.arch.gwa.client.uristate;

import archorg.arch.gwa.client.serialization.HasSerializableState;
import archorg.arch.gwa.client.serialization.HasStateLoader;
import archorg.arch.gwa.client.serialization.StateModelFactory;
import archorg.arch.gwa.client.serialization.StateModelImpl;
import archorg.arch.gwa.client.serialization.StateSerializationFormatException;
import archorg.arch.gwa.client.serialization.StatefulAction;

public class URIStateModelFactory
  implements
  StateModelFactory
{
  @Override
  public String dump(
    HasSerializableState state,
    StatefulAction a)
  {
    StateModelImpl si = new URIFragmentStateModel();
    si.dump(state,
      a);
    return si.toString();
  }

  @Override
  public void load(
    HasStateLoader root,
    String encoded) throws StateSerializationFormatException
  {
    StateModelImpl load = new URIFragmentStateModel(encoded);
    load.load(root);
  }
}
