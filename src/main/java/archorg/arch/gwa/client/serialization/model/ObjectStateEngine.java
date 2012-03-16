package archorg.arch.gwa.client.serialization.model;


public interface ObjectStateEngine
  extends
  StateLoader
{
  String dump(
    WritableStateModel s,
    Transition a);
}
