package archorg.arch.gwa.client.serialization;

import it.celi.research.balrog.claudenda.Claudenda;
import it.celi.research.balrog.claudenda.Claudendum;
import it.celi.research.balrog.event.Observable;
import it.celi.research.balrog.event.StatelessObserver;

import java.util.Map;

public abstract class SerializableState
{
  public SerializableState(Claudenda clau)
  {
    PostLoadingTriggers postLoadingTriggers = new PostLoadingTriggers();
    clau.add(postLoadingTriggers);
  }

  public abstract void dump(StateSerialization s,
      Map<String, String> map);

  public abstract void load(StateDeserialization s,
      String id,
      boolean dryrun) throws StateSerializationFormatException;

  /**
   * Sets the model to its default state.
   */
  public abstract void resetToDefault();

  public abstract boolean isAtDefault();

  /**
   * Restores transient parts and/or executes actions.
   * 
   * we want this triggered ability to trigger when we are loading a serialized
   * state because it affects only transient parts.
   * 
   * this could be problematic in that if the effect of this trigger depends on
   * the value of transient parts which are going to be affected by pending
   * transient triggers
   * 
   * therefore, we recommend that the effect of this trigger does not depend on
   * the value of transient parts. However this is not possible to enforce
   * programmatically.
   * 
   * 
   */
  public abstract void afterLoading();

  private class PostLoadingTriggers extends StatelessObserver<Void>
    implements Claudendum
  {
    PostLoadingTriggers()
    {
      URIStateManager.getLoadingCompleteOb().subscribe(this);
    }

    @Override
    public void notice(Observable<? extends Void> observable,
        Void message)
    {
      afterLoading();
    }

    @Override
    public void close() throws Exception
    {
      URIStateManager.getLoadingCompleteOb().subscribe(this);
    }
  }
}
