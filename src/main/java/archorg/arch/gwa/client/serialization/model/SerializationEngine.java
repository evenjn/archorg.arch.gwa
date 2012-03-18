package archorg.arch.gwa.client.serialization.model;

public interface SerializationEngine
{
  /**
   * 
   * @param statemodel
   *          The writable state model where to write the destination state
   * @param transition
   *          the transition used to reach the next state
   * @return the identifier of the portion of the state model that was
   *         contributed by this engine
   */
  String writeDestinationState(
    WritableStateModel statemodel,
    Transition transition);

  void loadState(
    boolean validate,
    ReadableStateModel s,
    String id) throws SerializationException;

  void connectToEnvironment();

  void disconnectFromEnvironment();

  void postLoad();
}
