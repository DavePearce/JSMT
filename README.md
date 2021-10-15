# Overview

This is a simple library for enumerating values under _constraints_.
For example, suppose you wanted to generate all arrays of size `3`
which are _sorted_.  Or, perhaps you wanted to generate all solutions
to a given formula.  Then, this is the library for you!

## Example 1 --- Tuples

As a simple example, let's suppose we suppose we want to enumerate all
pairs `[x,y]` where `0 <= x <= 5` and `0 <= y <= 5`.  We can do this
quite easily as follows:

```Java
Constraint.Set<String> constraints = new Constraint.Set<>(Arrays::toString);
Variable x = constraints.declare(between(0, 5));
Variable y = constraints.declare(between(0, 5));
//
for(String s : constraints) {
	System.out.println(s);
}
```

Here, we've declared `x` and `y` explicitly to help clarify what's
going on but, in this case at least, we don't actually need them.
Essentially, we've declared two variables in our constraint set which
are constrained to be between `0` and `5` (inclusive).  We've also
provided `Arrays::toString` as our _projection_ function which
generates a `String` from each variable assignment (in this case an
`int[]` array where `length==2`).

Running the above gives the following output:

```
[0, 0]
[0, 1]
[0, 2]
[0, 3]
...
[5, 4]
[5, 5]
```

Now, suppose we want to restrict this to those pairs where `x < y`
holds.  To do this, we can adjust the declaration of `y` as follows:

```Java
...
Variable y = constraints.declare(and(between(0, 5), greaterThan(x)));
...
```

This now includes the constraint that `y` must be _greater than_ `x`.
This means assignments such as `[5,4]` and `[5,5]` are no longer
included in the set.

As a final example, suppose we want to enumerate all triples `[x,y,z]`
where `x+y == z`.  We can do this quite easily with the following declarations:

```Java
Variable x = constraints.declare(between(0, 5));
Variable y = constraints.declare(and(between(0, 5)));
Variable z = constraints.declare(and(between(0, 5), equal(x.add(y))));
```

The output we get from running our program is:

```
[0, 0, 0]
[0, 1, 1]
[0, 2, 2]
[0, 3, 3]
...
[4, 0, 4]
[4, 1, 5]
[5, 0, 5]
```

At this point, we can start to generate more sophisticated constraints
on our variables.

## Example 2 --- Point Projections

As a simple example, let's suppose we have the following `Point`
class:


```Java
public class Point {
  private final int x;
  private final int y;

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  ...
 
  public String toString() {
    return "{x=" + x + ",y=" + y + "}";
  }  
}
```

```Java
Constraint.Set<Point> constraints = new Constraint.Set<>(vs -> new Point(vs[0],vs[1]));
Variable x = constraints.declare(between(0,5));
Variable y = constraints.declare(between(0,5));
//
for(Point p : constraints) {
   System.out.println(p);
}		
```

We've also
provided a _projection_ function which takes an `int[]` array (where
`length==2` in this case) and constructs an instance of `Point`.

## Example 3 --- Arrays

