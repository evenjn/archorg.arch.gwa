package archorg.arch.gwa.client.serialization.model.uri;

import archorg.arch.gwa.client.serialization.StateModelFactory;
import archorg.arch.gwa.client.serialization.model.HasSerializationEngine;
import archorg.arch.gwa.client.serialization.model.StateModelImpl;
import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.Transition;

public class URIStateModelFactory
  implements
  StateModelFactory
{
  @Override
  public String dump(
    HasSerializationEngine state,
    Transition a)
  {
    StateModelImpl si = new URIFragmentStateModel();
    si.readNextStateFrom(state,
      a);
    return si.toString();
  }

  @Override
  public void load(
    HasSerializationEngine root,
    String encoded) throws SerializationException
  {
    StateModelImpl load = new URIFragmentStateModel(encoded);
    load.injectStateInto(root);
  }
}
