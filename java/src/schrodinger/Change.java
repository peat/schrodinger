package schrodinger;

public interface Change<T> {
  public T apply( T v );
}
