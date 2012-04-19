package archorg.arch.gwa.client.serialization.model;

import archorg.arch.gwa.client.serialization.Resettable;

public interface SerializationEngine
  extends
  Resettable
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
    boolean validate,
    ReadableStateModel statemodel,
    String elementId) throws SerializationException;

  /**
   * connectToEnvironment is invoked during loading, after loadState and before
   * postLoad. This method allows to carry out operations that are part of the
   * initialization of the element, that must be carried out after all the basic
   * structure has been loaded. This operation is regularly invoked during each
   * loading operation, but it should affect the object only one time during its
   * life time.
   * 
   */
  void connectToEnvironment();

  void postLoad();
}
