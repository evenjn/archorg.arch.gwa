package archorg.arch.gwa.client.serial.model;

import archorg.arch.gwa.client.serialization.model.ReadableStateModel;
import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.Transition;
import archorg.arch.gwa.client.serialization.model.WritableStateModel;

public interface SerializationEngineBare
{
  /**
   * 
   * @param statemodel
   *          The writable state model where to write the destination state
   * @param transition
   *          the transition used to reach the next state
   * @return the identifier of the portion of the state model that was
   *         contributed by this engine. null if no contribution was provided.
   */
  String writeDestinationState(
    WritableStateModel statemodel,
    Transition transition);

  void loadState(
    ReadableStateModel statemodel,
    String elementId) throws SerializationException;
}
