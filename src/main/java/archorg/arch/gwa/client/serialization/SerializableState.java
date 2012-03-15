package archorg.arch.gwa.client.serialization;


public interface SerializableState
{
  boolean isAtDefault(
    StatefulAction a);

  void dump(
    WritableStateModel s,
    String id,
    StatefulAction a);
}
