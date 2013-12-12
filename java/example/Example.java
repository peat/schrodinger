import schrodinger.*;

import java.util.List;
import java.util.Arrays;

public class Example {
  public static void main( String[] args ) {
    System.out.println("Check out example/Example.java for the source!");
  	
    Box<Double> five = new Box(5.0);
    Box<Double> fiveSquared = Box.apply( new Square(), five );

    System.out.println("fiveSquared is " + fiveSquared.toString() );

    Box<Double> eight = new Box(8.0);
    Box<Double> eightPlusFivePlusFive = Box.apply( new Add(), Arrays.asList( eight, five, five ) );

    System.out.println("eightPlusFivePlusFive is " + eightPlusFivePlusFive.toString() );

    Box<String> foo = new Box("foo");
    Box<String> oof = Box.apply( new Reverse(), foo );

  	System.out.println("oof is " + oof.toString() );

  	Box<String> shortString = new Box("short string");
  	Box<String> reallyShortString = new Box("!");
  	Box<String> longString = new Box("this is a considerably longer string");
  	List<Box<String>> filteredList = Box.apply( new ShortFilter(), Arrays.asList( shortString, longString, reallyShortString ) );

    System.out.println("filteredList is " + filteredList.toString() );
  }
}

class Square implements Change<Double> {
  public Double apply( Double v ) {
	 return Math.pow( v.doubleValue(), 2.0 );
  }
}

class Add implements Combine<Double> {
  public Double apply( Double a, Double b ) {
    return a + b;
  }
}

class Reverse implements Change<String> {
  public String apply( String v ) {
    StringBuffer sb = new StringBuffer(v);
    return sb.reverse().toString();
  }
}

class ShortFilter implements Filter<String> {
  public Boolean apply( String v ) {
    if (v.length() < 15) {
	    return true;
    } else {
	    return false;
    }
  }
}
