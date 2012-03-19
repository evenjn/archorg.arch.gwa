package archorg.arch.gwa.client.serialization;

import it.celi.research.balrog.event.Observable;
import it.celi.research.balrog.event.Observer;

public interface EnvironmentEventBus
  extends
  Observer<Object>,
  Observable<Void>
{
  void sendSignal();
}
