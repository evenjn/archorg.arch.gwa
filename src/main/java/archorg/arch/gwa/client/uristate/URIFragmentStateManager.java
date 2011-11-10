package archorg.arch.gwa.client.uristate;

import archorg.arch.gwa.client.serialization.HasSerializableState;
import archorg.arch.gwa.client.serialization.StateManager;
import archorg.arch.gwa.client.serialization.StateModel;
import archorg.arch.gwa.client.serialization.StateSerializationFormatException;

public class URIFragmentStateManager extends StateManager
{
  public URIFragmentStateManager(HasSerializableState root)
  {
    super(root);
  }

  @Override
  protected StateModel create()
  {
    return new URIFragmentStateModel();
  }

  @Override
  protected StateModel create(String decode)
      throws StateSerializationFormatException
  {
    return new URIFragmentStateModel(decode);
  }
}
