package archorg.arch.gwa.client.join;

public class StringToIntegerConverter extends Converter<String, Integer>
{
  @Override
  protected Integer in(String value)
  {
    if (value == null)
      return null;
    else
      try
      {
        return Integer.valueOf(value);
      }
      catch (NumberFormatException e)
      {
        return null;
      }
  }

  @Override
  protected String out(Integer value)
  {
    if (value == null)
      return null;
    else
      return value.toString();
  }
}
