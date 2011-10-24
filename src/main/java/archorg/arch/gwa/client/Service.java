package archorg.arch.gwa.client;

import archorg.arch.gwa.shared.Input;
import archorg.arch.gwa.shared.Output;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("serviz")
public interface Service extends RemoteService
{
  Output serve(Input input);
}
