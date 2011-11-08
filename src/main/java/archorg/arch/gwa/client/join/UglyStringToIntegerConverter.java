package archorg.arch.gwa.client.join;

public class UglyStringToIntegerConverter extends Converter<String, Integer>
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
        return 1;
      }
  }

  @Override
  protected String out(Integer value)
  {
    if (value == null)
      return "";
    else
      return value.toString();
  }
}
