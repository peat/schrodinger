package schrodinger;

public class Box<T> {
    
    private T _boxedValue;

    public Box() {}

    public Box( T v ) {
	_boxedValue = v;
    }

    public T unpack() {
	return _boxedValue;
    }

    public Boolean isEmpty() {
	if (unpack() == null) {
	    return true;
	} else {
	    return false;
	}
    }

    public T or( T d ) {
	if (isEmpty()) {
	    return d;
	} else {
	    return unpack();
	}
    }

    public Box apply( BoxOp<T> bo ) {
	if (isEmpty()) {
	    return new Box<T>();
	} else {
	    return new Box<T>( bo.apply( unpack() ) );
	}
    }

}
