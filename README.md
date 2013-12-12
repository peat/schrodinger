# The Horror, The Horror!

`null` values are terrible, awful things. Lets look at some Java code for a moment:

```java
public Double mySquare( Double x ) {
  return Math.pow( x, 2.0 );
}
```

Pretty straight forward, eh? We receive `x`, then square it and return the results.

This is fine and dandy until someone passes in a `null`, and everything explodes into a giant fireball of `NullPointerException`.

So ... maybe can we can do some interior decoration, avoid the exception, and return a reasonable default if there happens to be a `null`:

```java
public Double mySquare( Double x ) {
  if (x != null) {
    return Math.pow( x, 2.0 );
  } else {
    return 0.0;
  }
}
```

At about this point, we take a sip of coffee and realize that *more than half the code* consists of `null` handling. Ugh.

That sucks, and this is really trivial code. Heaven forbid we have to write safe code that's significantly less contrived!

Given that `null` exists, and that we have to deal with Other People's Code which may or may not give us unexpected `null` pointers, it seems reasonable that we should have a way to deal with values that may or may not be `null`.

But first, a slight detour ...

## Schrodinger's Box

In the *Schrodinger's Cat* thought experiment, there's a cat who may or may not be dead inside of a box. Ignore the apparent paradox of the Copenhagen Interpretation of quantum mechanics, and focus on the idea that you have a box containing something of an indeterminate state.

Without knowing anything about the cat, you can still do things with the box: you can put it on a shelf. You can stick it in the garage. You can leave it on the coffee table and creep out guests with a story about might-be-dead cats. That's the cool thing about the box ... you know it exists, and you can do things with it.

For example, you can take the box off the coffee table and take it to your mom's house. That's a fairly complex process: you lift up the box, take it outside, strap it on the back of your stylish single speed bicycle, pedal the bike to your mom's house while obeying your local traffic laws, then hand the box to your mom ... *without having to know if the cat is alive or dead.*

Wouldn't it be cool if we could put some sort of box around might-be-null values, so that we can do things with them without having to worry if they're `null`?

## `import schrodinger`

So, let me just throw this out there:

```java
public Double mySquare( Double x ) {
  Box<Double> y = new Box(x);
  return Box.apply( new Square(), y ).or( 0.0 );
}
```

*WAT*? 

Lets take this apart, bit by bit.

First, that `Box` thing: it appears to be a generic class with a few static methods on it.

Second, `new Box(x)` produces a `Box` that contains `x` -- a `Double` that might be `null`.

Third, `Box.apply( new Square(), y )` smells a bit like `Math.pow( x, 2.0 )`.

Finally, the `.or( 0.0 )` method provides a default value, just in case `x` happens to be `null`.

In other words: we can perform generic operations on `Box` instances without having to check for `null` values.

## What is `Square()`?

This is where things get a little odd. `Square()` does the grunt work of squaring the value inside the `Box` ... but how does it operate on `y`?

Here's what it looks like:

```java
public class Square<Double> extends Change {
  public Double apply( Double base ) {
    return Math.pow( base, 2.0 );
  }
}
```

The plumbing is pretty straight forward: the `Change.apply()` allows you to work directly with the raw values, but *only if* the `Box` instance can supply a legitimate value. Otherwise, it's completely ignored. 

This means you can write code that deals directly with the raw data, without having to worry if it's present. Note that this code can also be reused for any `Box<Double>`.

## Unpacking and Default Values

It's cool that we can safely do operations within the safe confines of a `Box`, but what if we actually want to pass that value around to other code that isn't `Box` aware?

```java
Box<Double> y = new Box( x );
// do a bunch of stuff to y
Double z = y.unpack();
```

OK -- we can get the `Double`, but we're right back into the land of might-be-null values. We'll just end up doing something like this:

```java
Double z = y.unpack();
if (z == null) {
  z = 0.0; // safe default!
}
```

That way, `z` is either going to be the value of our computations, or `0.0` ... but damnit, we're trying to get out of the might-be-null handling business. The `unpack()` method is *dangerous*, because we might forget to follow it up with the `null` check.

This is nicer:

```java
Double z = y.or( 0.0 );
```

If `y` is a `Box` with a value, `z` will be the `Double` value ... and if `y` is empty, `z` will be `0.0`.

Now, we actually have an end-to-end system for eliminating the possibility of a `NullPointerException`!

This brings us back to our original suggestion:

```java
public Double mySquare( Double x ) {
  return Box.apply( new Square(), new Box(x) ).or(0.0)
}
```


## Lists?

That's all quite nice, but what if you want to do something like add up a list of boxed numbers?

```java
Box<Integer> x = new Box(5);
Box<Integer> y = new Box(2);
Box<Integer> z = new Box(9);
Box.apply( new Add(), Arrays.toList( x, y, z ) ); // .. Box(16)
```

That's pretty cool. The implementation of `Add()` looks like this:

```java
public class Add<Integer> extends Combine {
	public Integer apply( Integer a, Integer b ) {
		return a + b;
	}
}
```

Now, `combine` only takes two parameters ... how does it handle the three parameters in the example?

Again, `Box` is pretty smart: it combines the first two parameters together (adding `x` and `y`), takes that result, and combines it with the next (and last) parameter `z`. Kind of like this:

```java
a = apply( x, y )
return apply( a, z )
```

If you're familiar with functional programming, you'll notice that this is a *left fold* operation.

What happens if one of the boxes contains a `null` value? The `Box.apply()` method ignores it completely, and continues the operation as if it weren't present! This is quite handy when dealing with big sets of data, where it's safe to discard bad data.

Another fun method for lists is `Box.apply()` -- given a `Filter` instance and a `List` of boxes, it will filter the list. For example:

```java
Box<Integer> x = new Box(5);
Box<Integer> y = new Box(12);
Box<Integer> z = new Box(3);

List<Box<Integer>> noY = Box.apply( new LessThanTen(), Arrays.toList( x, y, z ) )
```

In which case, `noY` only contains boxes `x` and `z`, because `y` is greater than 10.

## Up Next

Lots. Maybe. If people are interested, I'd be happy to keep hacking and taking patches. If not, consider it a simple hacking exercise. :)

It might be fun to write some examples for other `null` prone languages -- C++, C#, or Objective-C, anyone?

## Running

Clone the repo, and do this:

	$ cd java/
	$ ant example
	
... the last block of output is the results of a pretty simple set of operations. Open `java/examples/Example.java` to see what's going on.
