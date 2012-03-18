package archorg.arch.gwa.client.serialization.model.parts;

import archorg.arch.gwa.client.serialization.model.ReadableStateModel;
import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.Transition;
import archorg.arch.gwa.client.serialization.model.WritableStateModel;

public interface CompositeSerializationEnginePart
{
  void dump(
    WritableStateModel s,
    String container_id,
    Transition a);

  void load(
    boolean validate,
    ReadableStateModel s,
    String id) throws SerializationException;

  void link();

  void unlink();

  void postLoad();
}
