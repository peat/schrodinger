package schrodinger;

import java.util.List;

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

    public String toString() {
	return "Box(" + unpack().toString() + ")";
    }

    public static <T> Box apply( BoxApply<T> ba, Box<T> b ) {
	if (b.isEmpty()) {
	    return new Box<T>();
	} else {
	    return new Box<T>( ba.apply( b.unpack() ) );
	}
    }

    public static <T> Box combine( BoxCombine<T> bc, List<Box<T>> boxes ) {
	
	Box<T> resultBox = new Box<T>();
 
	for (Box<T> b : boxes) {
	    // ignore empty 'b' instead of failing all over the place.
	    if (b.isEmpty()) {
		continue;
	    }

	    // if resultBox is empty, then we're starting from scratch. Set it to 'b', and go for the next box.
	    if (resultBox.isEmpty()) {
		resultBox = b;
		continue;
	    }

	    // at this point, we know we have values for resultBox and b; calculate the new resultBox!
	    T aVal = resultBox.unpack();
	    T bVal = b.unpack();

	    resultBox = new Box<T>( bc.combine( aVal, bVal ) ); 
	}

	return resultBox;
    }

    public static <T> List filter( BoxFilter<T> bf, List<Box<T>> boxes ) {
	List<Box<T>> boxList = new java.util.ArrayList<Box<T>>();

	for (Box<T> b : boxes) {
	    // ignore empty 'b' instead of failing all over the place
	    if (b.isEmpty()) {
		continue;
	    }

	    if ( bf.filter( b.unpack() ) ) {
		boxList.add( b );
	    }
	}

	return boxList;
    }

}
