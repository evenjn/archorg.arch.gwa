package archorg.arch.gwa.client.beacon;

import it.celi.research.balrog.beacon.SimpleBeacon;
import it.celi.research.balrog.beacon.SimpleBeaconChange;
import it.celi.research.balrog.beacon.SimpleBeaconImpl;
import it.celi.research.balrog.event.Observer;

public abstract class SafeSimpleBeaconImpl<T>
  implements
  SimpleBeacon<T>
{
  protected abstract T safeReplace(
    T value);

  private final SimpleBeaconImpl<T> beacon;

  public SafeSimpleBeaconImpl(
    SimpleBeaconImpl<T> beacon)
  {
    this.beacon = beacon;
  }

  @Override
  public boolean isNotNull()
  {
    return beacon.isNotNull();
  }

  @Override
  public boolean isNull()
  {
    return beacon.isNull();
  }

  @Override
  public T get()
  {
    return beacon.get();
  }

  @Override
  public boolean valueEquals(
    T value)
  {
    return beacon.valueEquals(value);
  }

  @Override
  public void subscribe(
    Observer<? super SimpleBeaconChange<? extends T>> observer)
  {
    beacon.subscribe(observer);
  }

  @Override
  public void unsubscribe(
    Observer<? super SimpleBeaconChange<? extends T>> observer)
  {
    beacon.unsubscribe(observer);
  }

  @Override
  public void clearSubscribers()
  {
    beacon.clearSubscribers();
  }

  @Override
  public SimpleBeaconChange<T> setNevertheless(
    T value)
  {
    return beacon.setNevertheless(safeReplace(value));
  }

  @Override
  public SimpleBeaconChange<T> setNevertheless(
    T value,
    boolean notify)
  {
    return beacon.setNevertheless(safeReplace(value),
      notify);
  }

  @Override
  public SimpleBeaconChange<T> setIfNotEqual(
    T value)
  {
    return beacon.setIfNotEqual(safeReplace(value));
  }

  @Override
  public SimpleBeaconChange<T> setIfNotEqual(
    T value,
    boolean notify)
  {
    return beacon.setIfNotEqual(safeReplace(value),
      notify);
  }
}
