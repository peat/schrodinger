package schrodinger;

public interface Combine<T> {
  public T apply( T a, T b );
}
