package archorg.arch.gwa.client.serialization.model.parts;

import archorg.arch.gwa.client.serialization.model.ReadableStateModel;
import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.Transition;
import archorg.arch.gwa.client.serialization.model.WritableStateModel;

public interface CompositeSerializationEnginePart
{
  /**
   * 
   * @param s
   * @param container_id
   * @param a
   * @return true when some contribution to the model was dumped. false if this
   *         part is in its default state.
   */
  boolean dump(
    WritableStateModel s,
    String container_id,
    Transition a);

  void load(
    boolean validate,
    ReadableStateModel s,
    String id) throws SerializationException;

  void connectToEnvironment();

  void postLoad();
}
