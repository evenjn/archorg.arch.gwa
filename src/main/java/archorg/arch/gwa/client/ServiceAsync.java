package archorg.arch.gwa.client;

import archorg.arch.gwa.shared.Input;
import archorg.arch.gwa.shared.Output;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ServiceAsync
{
  void serve(Input input,
      AsyncCallback<Output> callback);
}
