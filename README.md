# Overview

This is a simple library for enumerating values under _constraints_.
For example, suppose you wanted to generate all arrays of size `3`
which are _sorted_.  Or, perhaps you wanted to generate all solutions
to a given formula.  Then, this is the library for you!

## Getting Started

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

Suppose we want to enumerate all `Point` instances for `0 <= x <= 5`
and `0 <= y <= 5`.  We can do this quite easily as follows:

```Java
Constraint.Set<Point> constraints = new Constraint.Set<>(vs -> new Point(vs[0],vs[1]));
Constraint.Variable x = constraints.add(between(0,5));
Constraint.Variable y = constraints.add(between(0,5));
//
for(Point p : constraints) {
   System.out.println(p);
}		
```

Here, I've declared `x` and `y` explicitly to help clarify what's
going on but, in this case at least, we don't actually need them.  

Essentially, what's happening above is that we are declaring two
variables in our constraint set and enumerating them.  We've provided
a _project_ function which takes an `int[]` array (where `length==2`
in this case) and constructs an instance of `Point`.
