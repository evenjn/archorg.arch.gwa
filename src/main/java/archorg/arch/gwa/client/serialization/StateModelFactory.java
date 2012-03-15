package archorg.arch.gwa.client.serialization;

import archorg.arch.gwa.client.serialization.model.HasObjectStateEngine;
import archorg.arch.gwa.client.serialization.model.StateSerializationFormatException;

public interface StateModelFactory
{
  String dump(
    HasObjectStateEngine state,
    StatefulAction a);

  void load(
    HasObjectStateEngine root,
    String encoded) throws StateSerializationFormatException;
}
