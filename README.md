# The Horror, The Horror

`null` values are terrible, awful things. Lets look at some Java code for a moment:

```java
public Double mySquare( Double x ) {
  return Math.pow( x, 2.0 );
}
```

Pretty straight forward, eh? We receive `x`, then square it and return the results.

This is fine and dandy until someone passes in a `null`, and everything explodes into a giant fireball of `NullPointerException`.

So ... maybe can we can do some interior decoration, avoid the exception, and make it someone else's problem:

```java
public Double mySquare( Double x ) {
  if (x != null) {
    return Math.pow( x, 2.0 );
  } else {
    return null;
  }
}
```

At about this point, we take a sip of coffee and realize that more than half the code consists of `null` handling, and that anything calling your code also has to deal with the possibility of a returned `null` value.

That sucks, and this isn't even interesting code. Heaven forbid we have to write safe code that's significantly less contrived!

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
public Box<Double> mySquare( Double x ) {
  Box<Double> y = new Box( x );
  return x.apply( new Square() );
}
```

*WAT*? 

Lets take this apart, bit by bit.

First, that `Box` thing: it appears to be a generic class with a few static methods on it.

Second, `new Box( x )` produces a `Box` that contains `x` -- a `Double` that might be `null`.

Third, `y.apply( new Square() )` smells a bit like `Math.pow( x, 2.0 )` ... but it's operating within the `Box` and it returns a `Box` value.

Wait. What about `null`?

## `isEmpty`

`Box` is smart about it's operations: if it contains a `null`, the `apply()` method will simply return another empty `Box`.

You can manually check to see if a `Box` is empty with the `isEmpty()` method, but generally speaking that is handled for you!

In other words, we can efficiently perform a bunch of operations on `Box` classes without having to check for `null` values.

## Unpacking

It's cool that we can safely do operations on the values in these boxes, but what if we actually want to pass that value around to other code that isn't `Box` aware?

```java
Box<Double> y = new Box( x );
// do a bunch of stuff to y
Double z = y.unpack();
```

Great -- we can get the `Double` value, but at this point we're right back into the land of might-be-null values. If we're feeling safety conscious, we'll do doing something like this:

```java
Double z = y.unpack();
if (z == null) {
  z = 0.0;
}
```

That way, `z` is either going to be the value of our computations, or `0.0` ... but damnit, we don't want to be in the might-be-null handling business. The `unpack()` method is *dangerous*, because we might forget to follow it up with the `null` check.

This is nicer:

```java
Double z = y.or( 0.0 );
```

If `y` is a `Box` with a value, `z` will be the `Double` value ... if `y` is empty, `z` will be `0.0`.

Now, we actually have an end-to-end system for eliminating the possibility of a `NullPointerException`!

```java
public Double mySquare( Double x ) {
  return new Box( x ).apply( new Square() ).or(0.0)
}
```

## What is `Square()`?

This is where things get a little weird. `Square()` does the grunt work of squaring the value inside the `Box` ... but how does it operate on `y`?

Here's what it looks like:

```java
public class Square<Double> extends BoxOp {
  public Double apply( Double base ) {
    return Math.pow( base, 2.0 );
  }
}
```

The plumbing is pretty straight forward. The `apply()` method is called by the `Box`, which supplies it's own value ... and this is guaranteed to be a real value, not a `null`, because an empty `Box` won't actually call the `apply()` method on your operation.

Another method is available for changing the `Box` value. `convert(x)` handles operations that change the type of the value contained within the box (eg: `Double` to `String`).

In this way, you should be able to do something like:

```java
Box.up( someNumber() ).apply( new ToString() ).or("oops!")
```

## Up Next

Lots. Maybe. If people are interested, I'd be happy to keep hacking and taking patches. If not, consider it a simple hacking exercise. :)

It might be fun to write some examples for other `null` prone languages -- C++ anyone?


## Running

Clone the repo, and do this:

	$ cd java/
	$ ant example
	
... the last block of output is the results of a pretty simple set of operations. Open `java/examples/Example.java` to see what's going on.
