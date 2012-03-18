package archorg.arch.gwa.client.serialization;

import archorg.arch.gwa.client.serialization.model.HasSerializationEngine;
import archorg.arch.gwa.client.serialization.model.SerializationException;
import archorg.arch.gwa.client.serialization.model.Transition;

public interface StateModelFactory
{
  String dump(
    HasSerializationEngine state,
    Transition transition);

  void load(
    HasSerializationEngine root,
    String encoded) throws SerializationException;
}
