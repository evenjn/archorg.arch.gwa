package archorg.arch.gwa.client.serialization;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.http.client.URL;

public class SSerializer
{
  private final static String outer2 = ";";

  private final static String outer1 = ":";

  private final static String re_outer = "[:;]";

  private final static String inner1 = "=";

  private final static String re_inner1 = "=";

  private final static String inner2 = ",";

  private final static String re_inner2 = ",";

  private final static String re_inner = "[=,]";

  private final static String null_symbol = "/";

  private final static String empty_symbol = "@";

  private static class SSI implements SSerialization
  {
    private String dump = null;

    public SSI(SSerializable s)
    {
      if (s.isAtDefaults())
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
          sb.append(outer2);
        iz = new Integer(i);
        sb.append(iz);
        sb.append(outer1);
        sb.append(map_s.get(iz.toString()));
      }
      dump = sb.toString();
    }

    private int sequence = 0;

    private HashMap<SSerializable, String> map =
      new HashMap<SSerializable, String>();

    private HashMap<String, String> map_s = new HashMap<String, String>();

    public String getID(SSerializable s)
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
      s.dump(this,
        dump);
      for (Entry<String, String> o : dump.entrySet())
      {
        if (sb == null)
        {
          sb = new StringBuilder();
        } else
          sb.append(inner2);
        sb.append(escape(o.getKey()));
        sb.append(inner1);
        sb.append(escape(o.getValue()));
      }
      map_s.put(id,
        sb.toString());
      return id;
    }
  }

  public static String dump(SSerializable s)
  {
    SSI ser = new SSI(s);
    return ser.dump;
  }

  public static SDeserialization load(String s)
      throws SSerializationFormatException
  {
    return new DSI(s);
  }

  private static class DSI implements SDeserialization
  {
    HashMap<String, Map<String, String>> map_s =
      new HashMap<String, Map<String, String>>();

    private DSI(String s) throws SSerializationFormatException
    {
      if (!s.startsWith("!"))
        throw new SSerializationFormatException("empty string");
      if (s.length() == 1)
        throw new SSerializationFormatException("default state");
      if (s.length() < 2)
        throw new SSerializationFormatException("too short");
      s = s.substring(1);
      String[] split = s.split(re_outer);
      if (split.length == 0)
        throw new SSerializationFormatException("no components");
      if (split.length % 2 == 1)
        throw new SSerializationFormatException("odd number of components");
      boolean is_id = false;
      String id = null;
      for (String current : split)
      {
        if (current.isEmpty())
          throw new SSerializationFormatException("empty outer segment");
        is_id = !is_id;
        if (is_id)
        {
          if (!current.matches("[0-9]+"))
            throw new SSerializationFormatException("bad_entity_name");
          id = current;
        } else
        {
          if (!current.contains(inner1))
            throw new SSerializationFormatException("no inner splitter");
          String[] split2 = current.split(re_inner);
          if (split2.length == 0)
            throw new SSerializationFormatException("no components");
          if (split2.length % 2 == 1)
            throw new SSerializationFormatException("odd number of components");
          HashMap<String, String> map = new HashMap<String, String>();
          boolean is_fieldname = false;
          String fieldname = null;
          for (String piece : split2)
          {
            is_fieldname = !is_fieldname;
            if (piece.isEmpty())
              throw new SSerializationFormatException("empty inner segment");
            if (is_fieldname)
            {
              fieldname = piece;
              if (!fieldname.matches("[a-z]([a-z_]*[a-z])?"))
                throw new SSerializationFormatException("bad_field_name");
            } else
              map.put(fieldname,
                unescape(piece));
          }
          map_s.put(id,
            map);
        }
      }
    }

    public Map<String, String> get(String id,
        boolean dryrun) throws SSerializationFormatException
    {
      Map<String, String> result = map_s.get(id);
      if (!dryrun)
        if (result != null)
          map_s.put(id,
            null);
        else
          throw new SSerializationFormatException(
            "No such object available. The data structure is not a tree or the deserialization process is flawed");
      if (result == null)
      {
        throw new SSerializationFormatException(
          "No such object available. The data structure is not a tree or the deserialization process is flawed");
      }
      return result;
    }

    // HashMap<String, SSerializable> map_des =
    // new HashMap<String, SSerializable>();
    //
    // @Override
    // public SSerializable getDeserialized(String id)
    // {
    // return map_des.get(id);
    // }
    //
    // @Override
    // public void putDeserialized(String id,
    // SSerializable deserialized)
    // {
    // if (map_des.containsKey(id))
    // throw new IllegalStateException("key already used");
    // map_des.put(id,
    // deserialized);
    // }
    @Override
    public String getRootID()
    {
      return "0";
    }
  }

  private static String unescape(String s) throws SSerializationFormatException
  {
    if (s.equals(null_symbol))
      return null;
    if (s.equals(empty_symbol))
      return "";
    if (!s.matches("[a-zA-Z0-9-_\\.!~\\*'\\(\\)%]+"))
      throw new SSerializationFormatException(s);
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
