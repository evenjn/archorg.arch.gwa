package archorg.arch.gwa.client.serial.model.part;

import archorg.arch.gwa.client.serial.model.HasSerializationEngineBare;
import archorg.arch.gwa.client.serialization.model.ReadableStateModel;
import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.Transition;
import archorg.arch.gwa.client.serialization.model.WritableStateModel;

public class ObjectCSEPartBare<T extends HasSerializationEngineBare>
  implements
  CompositeSerializationEnginePartBare
{
  private final T object;

  private final String partId;

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
  public static <T extends HasSerializationEngineBare> ObjectCSEPartBare<T> create(
    T object,
    String partId)
  {
    return new ObjectCSEPartBare<T>(object, partId);
  }

  protected ObjectCSEPartBare(
    T object,
    String partId)
  {
    this.object = object;
    this.partId = partId;
  }

  @Override
  public boolean dump(
    WritableStateModel s,
    String container_id,
    Transition a)
  {
    String vid = object.getSerializationEngineBare().writeDestinationState(s,
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
    ReadableStateModel s,
    String id) throws SerializationException
  {
    if (!s.hasValueForPart(id,
      partId))
    {
      // this system asks the object to load an empty state,
      // it's a reset!
      object.getSerializationEngineBare().loadState(s,
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
    object.getSerializationEngineBare().loadState(s,
      string);
  }
}
