package archorg.arch.gwa.client.join;

import com.google.gwt.i18n.client.NumberFormat;

public class StringToFloatConverter extends Converter<String, Float>
{
  @Override
  protected Float in(String value)
  {
    if (value == null)
      return null;
    else
      try
      {
        return Float.valueOf(value);
      }
      catch (NumberFormatException e)
      {
        return null;
      }
  }

  @Override
  protected String out(Float value)
  {
    if (value == null)
      return "";
    else
      return format.format(value.doubleValue());
  }

  private static final NumberFormat format = NumberFormat.getFormat("###.###");
}
