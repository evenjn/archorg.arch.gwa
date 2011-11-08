package archorg.arch.gwa.client.serialization;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.http.client.URL;

public class StateSerializer
{
  private final static String outer_separator = ";";

  private final static String outer_assignment = ":";

  private final static String re_outer = "[:;]";

  private final static String inner_assignment = "=";

  private final static String inner_separator = ",";

  private final static String re_inner = "[=,]";

  private final static String null_symbol = "/";

  private final static String empty_symbol = "@";

  private static class SSI implements StateSerialization
  {
    private String dump = null;

    public SSI(HasSerializableState s)
    {
      if (s.getSerializableState().isAtDefault())
      {
        dump = "!";
        return;
      }
      getID(s);
      StringBuilder sb = null;
      Integer iz = null;
      for (int i = 0; i < sequence; i++)
      {
        if (sb == null)
        {
          sb = new StringBuilder();
          sb.append("!");
        } else
          sb.append(outer_separator);
        iz = new Integer(i);
        sb.append(iz);
        sb.append(outer_assignment);
        sb.append(map_s.get(iz.toString()));
      }
      dump = sb.toString();
    }

    private int sequence = 0;

    private HashMap<HasSerializableState, String> map =
      new HashMap<HasSerializableState, String>();

    private HashMap<String, String> map_s = new HashMap<String, String>();

    public String getID(HasSerializableState s) // rename this method to a more
                                                // suitable name
    {
      String id = map.get(s);
      if (id != null)
        return id;
      id = new Integer(sequence).toString();
      sequence = sequence + 1;
      map.put(s,
        id);
      StringBuilder sb = null;
      HashMap<String, String> dump = new HashMap<String, String>();
      s.getSerializableState().dump(this,
        dump);
      for (Entry<String, String> o : dump.entrySet())
      {
        if (sb == null)
        {
          sb = new StringBuilder();
        } else
          sb.append(inner_separator);
        sb.append(escape(o.getKey()));
        sb.append(inner_assignment);
        sb.append(escape(o.getValue()));
      }
      map_s.put(id,
        sb.toString());
      return id;
    }
  }

  public static String dump(HasSerializableState s)
  {
    SSI ser = new SSI(s);
    return ser.dump;
  }

  public static StateDeserialization load(String s)
      throws StateSerializationFormatException
  {
    return new DSI(s);
  }

  private static class DSI implements StateDeserialization
  {
    HashMap<String, Map<String, String>> map_s =
      new HashMap<String, Map<String, String>>();

    private DSI(String s) throws StateSerializationFormatException
    {
      if (!s.startsWith("!"))
        throw new StateSerializationFormatException("empty string");
      if (s.length() == 1)
        throw new StateSerializationFormatException("default state");
      if (s.length() < 2)
        throw new StateSerializationFormatException("too short");
      s = s.substring(1);
      String[] split = s.split(re_outer);
      if (split.length == 0)
        throw new StateSerializationFormatException("no components");
      if (split.length % 2 == 1)
        throw new StateSerializationFormatException("odd number of components");
      boolean is_id = false;
      String id = null;
      for (String current : split)
      {
        if (current.isEmpty())
          throw new StateSerializationFormatException("empty outer segment");
        is_id = !is_id;
        if (is_id)
        {
          if (!current.matches("[0-9]+"))
            throw new StateSerializationFormatException("bad_entity_name");
          id = current;
        } else
        {
          if (!current.contains(inner_assignment))
            throw new StateSerializationFormatException("no inner splitter");
          String[] split2 = current.split(re_inner);
          if (split2.length == 0)
            throw new StateSerializationFormatException("no components");
          if (split2.length % 2 == 1)
            throw new StateSerializationFormatException(
              "odd number of components");
          HashMap<String, String> map = new HashMap<String, String>();
          boolean is_fieldname = false;
          String fieldname = null;
          for (String piece : split2)
          {
            is_fieldname = !is_fieldname;
            if (piece.isEmpty())
              throw new StateSerializationFormatException("empty inner segment");
            if (is_fieldname)
            {
              fieldname = piece;
              if (!fieldname.matches("[a-z]([a-z_]*[a-z])?"))
                throw new StateSerializationFormatException("bad_field_name");
            } else
              map.put(fieldname,
                unescape(piece));
          }
          if (map_s.containsKey(id))
            throw new StateSerializationFormatException("duplicate key");
          map_s.put(id,
            map);
        }
      }
    }

    public Map<String, String> get(String id,
        boolean dryrun) throws StateSerializationFormatException
    {
      Map<String, String> result = map_s.get(id);
      if (!dryrun)
        if (result != null)
          map_s.put(id,
            null);
        else
          throw new StateSerializationFormatException(
            "No such object available. The data structure is not a tree or the deserialization process is flawed");
      if (result == null)
      {
        throw new StateSerializationFormatException(
          "No such object available. The data structure is not a tree or the deserialization process is flawed");
      }
      return result;
    }

    @Override
    public String getRootID()
    {
      return "0";
    }
  }

  private static String unescape(String s)
      throws StateSerializationFormatException
  {
    if (s.equals(null_symbol))
      return null;
    if (s.equals(empty_symbol))
      return "";
    if (!s.matches("[a-zA-Z0-9-_\\.!~\\*'\\(\\)%]+"))
      throw new StateSerializationFormatException(s);
    return URL.decodeQueryString(s);
  }

  private static String escape(String s)
  {
    if (s == null)
      return null_symbol;
    if (s.isEmpty())
      return empty_symbol;
    return URL.encodeQueryString(s);
  }
}
