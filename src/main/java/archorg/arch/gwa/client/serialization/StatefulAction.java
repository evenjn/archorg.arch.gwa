package archorg.arch.gwa.client.serialization;

import it.celi.research.balrog.beacon.SimpleBeaconReadable;

public interface StatefulAction
{
  void execute();

  SimpleBeaconReadable<? extends String> getResultingState();
}
