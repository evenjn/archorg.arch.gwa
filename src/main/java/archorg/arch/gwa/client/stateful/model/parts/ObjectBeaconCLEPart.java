package archorg.arch.gwa.client.stateful.model.parts;

import it.celi.research.balrog.beacon.SimpleBeacon;
import it.celi.research.balrog.claudenda.Claudenda;
import archorg.arch.gwa.client.stateful.model.HasLoadingEngine;

public abstract class ObjectBeaconCLEPart<T extends HasLoadingEngine>
  implements
  CompositeLoadingEnginePart
{
  private final SimpleBeacon<T> beacon;

  public abstract T create(
    Claudenda clau);

  protected ObjectBeaconCLEPart(
    SimpleBeacon<T> beacon)
  {
    this.beacon = beacon;
  }

  @Override
  public void postLoad()
  {
    if (beacon.isNotNull())
      beacon.get().getLoadingEngine().postLoad();
  }

  @Override
  public void connectToEnvironment()
  {
    if (beacon.isNotNull())
      beacon.get().getLoadingEngine().connectToEnvironment();
  }
}
