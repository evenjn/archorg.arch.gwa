package archorg.arch.gwa.client;

import it.celi.research.balrog.beacon.BeaconImpl;
import it.celi.research.balrog.beacon.Change;
import it.celi.research.balrog.beacon.ChangeImpl;
import it.celi.research.balrog.claudenda.Claudenda;

public class ClaudendumBeacon<T> extends BeaconImpl<T>
{
  private Claudenda claudenda;

  public void setClaudenda(Claudenda clau)
  {
    claudenda = clau;
  }

  private void fixClaudenda()
  {
    if (value != null)
    {
      if (claudenda == null)
        throw new IllegalStateException("No claudenda has ever been provided");
      claudenda.closeAll();
      claudenda = null;
    }
  }

  public Change<T> setNevertheless(T value,
      boolean notify)
  {
    ChangeImpl<T> change = new ChangeImpl<T>(this.value, value);
    fixClaudenda();
    this.value = value;
    if (notify)
      notify(change);
    return change;
  }

  public Change<T> setIfNotEqual(T value,
      boolean notify)
  {
    if (this.value == null && value == null)
      return null;
    if (this.value == null && value != null)
    {
      ChangeImpl<T> change = new ChangeImpl<T>(this.value, value);
      fixClaudenda();
      this.value = value;
      if (notify)
        notify(change);
      return change;
    }
    if (!this.value.equals(value))
    {
      ChangeImpl<T> change = new ChangeImpl<T>(this.value, value);
      fixClaudenda();
      this.value = value;
      if (notify)
        notify(change);
      return change;
    }
    return null;
  }
}
