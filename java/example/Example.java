import schrodinger.*;

import java.util.*;

public class Example {

    public static void main( String[] args ) {

	System.out.println("Check out example/Example.java for the source!");
	
	Box<Double> five = new Box(5.0);
	Box<Double> fiveSquared = Box.apply( new Square(), five );

	System.out.println("fiveSquared is " + fiveSquared.toString() );

	Box<Double> eight = new Box(8.0);
	Box<Double> eightPlusFivePlusFive = Box.combine( new Add(), Arrays.asList( eight, five, five ) );

	System.out.println("eightPlusFivePlusFive is " + eightPlusFivePlusFive.toString() );

	Box<String> foo = new Box("foo");
	Box<String> oof = Box.apply( new Reverse(), foo );

	System.out.println("oof is " + oof.toString() );

	Box<String> shortString = new Box("short string");
	Box<String> reallyShortString = new Box("!");
	Box<String> longString = new Box("this is a considerably longer string");
	List<Box<String>> filteredList = Box.filter( new ShortFilter(), Arrays.asList( shortString, longString, reallyShortString ) );

	System.out.println("filteredList is " + filteredList.toString() );
    }

}

class Square implements BoxApply<Double> {
    public Double apply( Double v ) {
	return Math.pow( v.doubleValue(), 2.0 );
    }
}

class Add implements BoxCombine<Double> {
    public Double combine( Double a, Double b ) {
	return a + b;
    }
}

class Reverse implements BoxApply<String> {
    public String apply( String v ) {
	StringBuffer sb = new StringBuffer(v);
	return sb.reverse().toString();
    }
}

class ShortFilter implements BoxFilter<String> {
    public Boolean filter( String v ) {
	if (v.length() < 15) {
	    return true;
	} else {
	    return false;
	}
    }
}
