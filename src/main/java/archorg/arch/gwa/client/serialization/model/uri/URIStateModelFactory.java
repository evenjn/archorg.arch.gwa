package archorg.arch.gwa.client.serialization.model.uri;

import archorg.arch.gwa.client.serialization.StateModelFactory;
import archorg.arch.gwa.client.serialization.StatefulAction;
import archorg.arch.gwa.client.serialization.model.HasObjectStateEngine;
import archorg.arch.gwa.client.serialization.model.StateModelImpl;
import archorg.arch.gwa.client.serialization.model.StateSerializationFormatException;

public class URIStateModelFactory
  implements
  StateModelFactory
{
  @Override
  public String dump(
    HasObjectStateEngine state,
    StatefulAction a)
  {
    StateModelImpl si = new URIFragmentStateModel();
    si.dump(state,
      a);
    return si.toString();
  }

  @Override
  public void load(
    HasObjectStateEngine root,
    String encoded) throws StateSerializationFormatException
  {
    StateModelImpl load = new URIFragmentStateModel(encoded);
    load.load(root);
  }
}
