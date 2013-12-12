package schrodinger;

public interface Filter<T> {
  public Boolean apply( T v );
}
