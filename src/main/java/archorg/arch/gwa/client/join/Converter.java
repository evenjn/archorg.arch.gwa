package archorg.arch.gwa.client.join;

public abstract class Converter<X, Y>
{
  protected abstract Y in(X value);

  protected abstract X out(Y value);
}
