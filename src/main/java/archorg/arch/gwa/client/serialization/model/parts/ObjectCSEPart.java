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

  private final boolean noReset;

  /**
   * This flag is set to true when a part that does not reset is loading an
   * empty model. In such cases, postLoad should not do anything. It will just
   * reset this flag.
   */
  private boolean skipNextPostload = false;

  public String getPartId()
  {
    return partId;
  }

  /**
   * 
   * @param object
   * @param partId
   * @param noReset
   *          specifies the behaviour of the loading system in the special case
   *          when the model contains no serialization information about this
   *          part. If noReset is set to true, the loading system will leave the
   *          part in its current state. If noReset is set to false, the loading
   *          system will instruct the part to load the empty state, which
   *          typically results in the part being reset to its default state.
   * 
   * @return
   */
  public static <T extends HasSerializationEngine> ObjectCSEPart<T> nu(
    T object,
    String partId,
    boolean noReset)
  {
    return new ObjectCSEPart<T>(object, partId, noReset);
  }

  protected ObjectCSEPart(
    T object,
    String partId,
    boolean noReset)
  {
    this.object = object;
    this.partId = partId;
    this.noReset = noReset;
  }

  @Override
  public boolean dump(
    WritableStateModel s,
    String container_id,
    Transition a)
  {
    String vid = object.getSerializationEngine().writeDestinationState(s,
      a);
    if (vid == null)
      return false;
    s.storeValueForPart(container_id,
      partId,
      vid);
    return true;
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
      if (noReset)
      {
        skipNextPostload = true;
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
    object.getSerializationEngine().loadState(validate,
      s,
      string);
  }

  @Override
  public void connectToEnvironment()
  {
    object.getSerializationEngine().connectToEnvironment();
  }

  @Override
  public void postLoad()
  {
    if (!skipNextPostload)
      object.getSerializationEngine().postLoad();
    skipNextPostload = false;
  }

  @Override
  public void resetToDefaultState()
  {
    object.getSerializationEngine().resetToDefaultState();
  }
}
