package archorg.arch.gwa.client.serialization;

import it.celi.research.balrog.beacon.SimpleBeaconReadable;

public interface StateTransitionAction
{
  void execute();

  SimpleBeaconReadable<? extends String> getResultingState();
}
