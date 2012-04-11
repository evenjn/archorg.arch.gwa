package archorg.arch.gwa.client.stateful.model.parts;

import archorg.arch.gwa.client.stateful.model.HasLoadingEngine;

public class ObjectCLEPart<T extends HasLoadingEngine>
  implements
  CompositeLoadingEnginePart
{
  private final T object;

  private final boolean noReset;

  /**
   * This flag is set to true when a part that does not reset is loading an
   * empty model. In such cases, postLoad should not do anything. It will just
   * reset this flag.
   */
  private boolean skipNextPostload = false;

  /**
   * 
   * @param object
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
  public static <T extends HasLoadingEngine> ObjectCLEPart<T> create(
    T object,
    boolean noReset)
  {
    return new ObjectCLEPart<T>(object, noReset);
  }

  protected ObjectCLEPart(
    T object,
    boolean noReset)
  {
    this.object = object;
    this.noReset = noReset;
  }

  @Override
  public void connectToEnvironment()
  {
    object.getLoadingEngine().connectToEnvironment();
  }

  @Override
  public void postLoad()
  {
    if (!skipNextPostload)
      object.getLoadingEngine().postLoad();
    skipNextPostload = false;
  }
}
