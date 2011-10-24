package archorg.arch.gwa.server;

import archorg.arch.gwa.client.Service;
import archorg.arch.gwa.shared.Input;
import archorg.arch.gwa.shared.Output;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ServiceImpl extends RemoteServiceServlet implements Service
{
  @Override
  public Output serve(Input input)
  {
    Output output = new Output();
    if (input == null)
    {
      output.errorMessage = "null input";
      output.errorOccurred = true;
      return output;
    }
    output.input = input.input;
    if (input.input == null)
    {
      output.errorMessage = "null input.input";
      output.errorOccurred = true;
      return output;
    }
    if (input.input == Integer.MAX_VALUE || input.input == Integer.MIN_VALUE
        || input.input <= 0 || input.input > 1000)
    {
      output.errorMessage = "argument must be in range 1-1000";
      output.errorOccurred = true;
      return output;
    }
    for (int i = 0; i < input.input; i++)
    {
      output.output.add(i);
    }
    return output;
  }
}
