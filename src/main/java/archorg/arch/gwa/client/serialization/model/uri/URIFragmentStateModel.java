package archorg.arch.gwa.client.serialization.model.uri;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import archorg.arch.gwa.client.serialization.model.StateModelImpl;
import archorg.arch.gwa.client.serialization.model.SerializationException;

import com.google.gwt.http.client.URL;

public class URIFragmentStateModel
  extends
  StateModelImpl
{
  public URIFragmentStateModel()
  {}

  public URIFragmentStateModel(
    String s)
    throws SerializationException
  {
    if (s.length() == 1)
      return;
    // throw new StateSerializationFormatException("default state");
    if (s.length() < 2)
      throw new SerializationException("too short");
    s = s.substring(1);
    String[] split = s.split(re_outer);
    if (split.length == 0)
      throw new SerializationException("no components");
    if (split.length % 2 == 1)
      throw new SerializationException("odd number of components");
    boolean is_id = false;
    String id = null;
    for (String current : split)
    {
      if (current.isEmpty())
        throw new SerializationException("empty outer segment");
      is_id = !is_id;
      if (is_id)
      {
        if (!current.matches("[0-9]+"))
          throw new SerializationException("bad_entity_name");
        id = current;
      } else
      {
        if (!current.contains(inner_assignment))
          throw new SerializationException("no inner splitter");
        String[] split2 = current.split(re_inner);
        if (split2.length == 0)
          throw new SerializationException("no components");
        if (split2.length % 2 == 1)
          throw new SerializationException(
            "odd number of components");
        HashMap<String, String> map = new HashMap<String, String>();
        boolean is_fieldname = false;
        String fieldname = null;
        for (String piece : split2)
        {
          is_fieldname = !is_fieldname;
          if (piece.isEmpty())
            throw new SerializationException("empty inner segment");
          if (is_fieldname)
          {
            fieldname = piece;
            if (!fieldname.matches("[a-z]([a-z_]*[a-z])?"))
              throw new SerializationException("bad_field_name");
          } else
            map.put(fieldname,
              unescape(piece));
        }
        if (map_s.containsKey(id))
          throw new SerializationException("duplicate key");
        map_s.put(id,
          map);
      }
    }
  }

  public String toString()
  {
    StringBuilder sb = null;
    for (String id : id_sequence)
    {
      Map<String, String> map = map_s.get(id);
      StringBuilder sbi = null;
      for (Entry<String, String> o : map.entrySet())
      {
        if (sbi == null)
        {
          sbi = new StringBuilder();
        } else
          sbi.append(inner_separator);
        sbi.append(escape(o.getKey()));
        sbi.append(inner_assignment);
        sbi.append(escape(o.getValue()));
      }
      if (sbi == null)
        continue;
      if (sb == null)
      {
        sb = new StringBuilder();
        sb.append("!");
      } else
        sb.append(outer_separator);
      sb.append(id);
      sb.append(outer_assignment);
      sb.append(sbi.toString());
    }
    if (sb == null)
      return "!";
    return sb.toString();
  }

  private final static String outer_separator = ";";

  private final static String outer_assignment = ":";

  private final static String re_outer = "[:;]";

  private final static String inner_assignment = "=";

  private final static String inner_separator = ",";

  private final static String re_inner = "[=,]";

  private final static String null_symbol = "/";

  private final static String empty_symbol = "@";

  private static String unescape(
    String s) throws SerializationException
  {
    if (s.equals(null_symbol))
      return null;
    if (s.equals(empty_symbol))
      return "";
    if (!s.matches("[a-zA-Z0-9-_\\.!~\\*'\\(\\)%]+"))
      throw new SerializationException(s);
    return URL.decodeQueryString(s);
  }

  private static String escape(
    String s)
  {
    if (s == null)
      return null_symbol;
    if (s.isEmpty())
      return empty_symbol;
    return URL.encodeQueryString(s);
  }
}
