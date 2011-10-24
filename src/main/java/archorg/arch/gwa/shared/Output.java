package archorg.arch.gwa.shared;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Output implements Serializable
{
  public Integer input;

  public boolean errorOccurred;

  public String errorMessage;

  public ArrayList<Integer> output = new ArrayList<Integer>();
}
