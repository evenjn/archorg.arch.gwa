package archorg.arch.gwa.client.serialization;

import archorg.arch.gwa.client.serialization.model.HasObjectStateEngine;
import archorg.arch.gwa.client.serialization.model.StateSerializationFormatException;
import archorg.arch.gwa.client.serialization.model.Transition;

public interface StateModelFactory
{
  String dump(
    HasObjectStateEngine state,
    Transition transition);

  void load(
    HasObjectStateEngine root,
    String encoded) throws StateSerializationFormatException;
}
