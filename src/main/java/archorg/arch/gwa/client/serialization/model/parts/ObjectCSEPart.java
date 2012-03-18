package archorg.arch.gwa.client.serialization.model.parts;

import archorg.arch.gwa.client.serialization.model.HasSerializationEngine;
import archorg.arch.gwa.client.serialization.model.ReadableStateModel;
import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.Transition;
import archorg.arch.gwa.client.serialization.model.WritableStateModel;

public class ObjectCSEPart<T extends HasSerializationEngine>
  implements
  CompositeSerializationEnginePart
{
  private final T object;

  private final String partId;

  public ObjectCSEPart(
    T object,
    String partId)
  {
    this.object = object;
    this.partId = partId;
  }

  @Override
  public void dump(
    WritableStateModel s,
    String container_id,
    Transition a)
  {
    String vid = object.getSerializationEngine().writeDestinationState(s,
      a);
    s.storeValueForPart(container_id,
      partId,
      vid);
  }

  @Override
  public void load(
    boolean validate,
    ReadableStateModel s,
    String id) throws SerializationException
  {
    if (!s.hasValueForPart(id,
      partId))
    {
      if (validate)
      {
        // if this invocation is for validation of the serialization, then
        // this system does not
        // have anything to validate, because the serialization is empty here.
        return;
      }
      // this system asks the object to load an empty state,
      // it's a reset!
      object.getSerializationEngine().loadState(validate,
        s,
        null);
      return;
    }
    String string = s.getValueForPart(id,
      partId);
    if (string == null)
    {
      throw new SerializationException("Model is null.");
    }
    // it's a real string.
    object.getSerializationEngine().loadState(true,
      s,
      string);
  }

  @Override
  public void link()
  {
    object.getSerializationEngine().connectToEnvironment();
  }

  @Override
  public void unlink()
  {
    object.getSerializationEngine().disconnectFromEnvironment();
  }

  @Override
  public void postLoad()
  {
    object.getSerializationEngine().postLoad();
  }
}
