package archorg.arch.gwa.client.serialization;

public interface SerializableState
{
  String dump(
    WritableStateModel s,
    StatefulAction a);
}
