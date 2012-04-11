package archorg.arch.gwa.client.stateful.model.parts;

import java.util.ArrayList;

import archorg.arch.gwa.client.stateful.model.LoadingEngine;

public class CompositeLoadingEngine
  implements
  LoadingEngine
{
  private ArrayList<CompositeLoadingEnginePart> ios =
    new ArrayList<CompositeLoadingEnginePart>();

  protected CompositeLoadingEngine(
    CompositeLoadingEnginePart... beacons)
  {
    for (CompositeLoadingEnginePart beacon : beacons)
      ios.add(beacon);
  }

  public static CompositeLoadingEngine create(
    CompositeLoadingEnginePart... beacons)
  {
    return new CompositeLoadingEngine(beacons);
  }

  @Override
  public void postLoad()
  {
    for (CompositeLoadingEnginePart bs : ios)
      bs.postLoad();
  }

  @Override
  public void connectToEnvironment()
  {
    for (CompositeLoadingEnginePart bs : ios)
      bs.connectToEnvironment();
  }
}
