package archorg.arch.gwa.client.uristate;

import archorg.arch.gwa.client.serialization.StateManager;
import archorg.arch.gwa.client.serialization.StateModel;
import archorg.arch.gwa.client.serialization.StateSerializationFormatException;

public class URIFragmentStateManager
  extends
  StateManager
{
  @Override
  protected StateModel create()
  {
    return new URIFragmentStateModel();
  }

  @Override
  protected StateModel create(
    String decode) throws StateSerializationFormatException
  {
    return new URIFragmentStateModel(decode);
  }
}
